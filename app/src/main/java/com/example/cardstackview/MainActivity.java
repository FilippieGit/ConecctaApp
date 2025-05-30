package com.example.cardstackview;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cardstackview.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar idTopAppBar;
    private DrawerLayout idDrawer;
    private NavigationView idNavView;
    private ActivityMainBinding binding;
    private CardStackLayoutManager layoutManager;
    private CardAdapter adapter;
    private List<Vagas> vagasList = new ArrayList<>();
    private List<Vagas> allVagas = new ArrayList<>();
    private ProgressBar progressBar;
    private int currentPosition = 0;
    private static final int BATCH_SIZE = 10;
    private boolean isLoading = false;

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.getRoot());

        progressBar = findViewById(R.id.progressBar);
        adapter = new CardAdapter(this, vagasList);
        setupCardStackView();
        setupNavigation();
        buscarVagas();
    }

    private void setupCardStackView() {
        CardStackListener cardStackListener = new CardStackListener() {
            @Override
            public void onCardSwiped(Direction direction) {
                int pos = layoutManager.getTopPosition() - 1;
                if (pos >= 0 && pos < vagasList.size()) {
                    Vagas vaga = vagasList.get(pos);

                    executor.execute(() -> {
                        if (direction == Direction.Right) {
                            processarLike(vaga);
                        }

                        runOnUiThread(() -> {
                            if (pos >= vagasList.size() - 3 && currentPosition < allVagas.size()) {
                                loadNextBatch();
                            }
                        });
                    });
                }
            }

            @Override
            public void onCardDragging(Direction direction, float ratio) {
                if (direction == Direction.Bottom && ratio > 0.5f) {
                    binding.cardStack.rewind();
                    binding.cardStack.swipe();
                }
            }

            @Override public void onCardRewound() {}
            @Override public void onCardCanceled() {}
            @Override public void onCardAppeared(View view, int position) {}
            @Override public void onCardDisappeared(View view, int position) {}
        };

        layoutManager = new CardStackLayoutManager(this, cardStackListener);
        layoutManager.setStackFrom(StackFrom.None);
        layoutManager.setVisibleCount(3);
        layoutManager.setDirections(Arrays.asList(Direction.Left, Direction.Right, Direction.Bottom));

        binding.cardStack.setLayoutManager(layoutManager);
        binding.cardStack.setAdapter(adapter);

        ImageButton btnReject = findViewById(R.id.btnReject);
        ImageButton btnLike = findViewById(R.id.btnLike);
        ImageButton btnDown = findViewById(R.id.btnDown);

        btnLike.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(400)
                    .build();
            layoutManager.setSwipeAnimationSetting(setting);
            binding.cardStack.swipe();
        });

        btnReject.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(400)
                    .build();
            layoutManager.setSwipeAnimationSetting(setting);
            binding.cardStack.swipe();
        });

        btnDown.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Bottom)
                    .setDuration(400)
                    .build();
            layoutManager.setSwipeAnimationSetting(setting);
            binding.cardStack.swipe();
        });
    }

    private void processarLike(Vagas vaga) {
        VagaDatabaseHelper dbHelper = new VagaDatabaseHelper(MainActivity.this);
        if (!dbHelper.isVagaFavorita(vaga.getVaga_id())) {
            dbHelper.adicionarVagaFavorita(vaga);
            runOnUiThread(() ->
                    Toast.makeText(MainActivity.this, "Vaga favoritada!", Toast.LENGTH_SHORT).show()
            );
        }
        registrarInteresse(vaga);
    }

    private void loadNextBatch() {
        if (isLoading || currentPosition >= allVagas.size()) return;

        isLoading = true;
        binding.progressBar.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int end = Math.min(currentPosition + BATCH_SIZE, allVagas.size());
            List<Vagas> batch = allVagas.subList(currentPosition, end);

            int startPosition = vagasList.size();
            vagasList.addAll(batch);
            currentPosition = end;

            adapter.notifyItemRangeInserted(startPosition, batch.size());
            isLoading = false;
            binding.progressBar.setVisibility(View.GONE);

            preloadImages(batch);
        }, 150);
    }

    private void preloadImages(List<Vagas> batch) {
        executor.execute(() -> {
            // Implemente o pré-carregamento de imagens aqui se necessário
        });
    }

    private void buscarVagas() {
        new PerformNetworkRequest(Api.URL_GET_VAGAS, null, CODE_GET_REQUEST).execute();
    }

    private void registrarInteresse(Vagas vaga) {
        executor.execute(() -> {
            HashMap<String, String> params = new HashMap<>();
            params.put("vaga_id", String.valueOf(vaga.getVaga_id()));
            params.put("usuario_id", "1");

            try {
                String resultado = new RequestHandler().sendPostRequest(Api.URL_REGISTRAR_INTERESSE, params);
                JSONObject response = new JSONObject(resultado);
                if (response.getBoolean("error")) {
                    Log.e("API", "Erro ao registrar interesse: " + response.getString("message"));
                }
            } catch (Exception e) {
                Log.e("API", "Erro ao registrar interesse", e);
            }
        });
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            binding.cardStack.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            binding.cardStack.setVisibility(View.VISIBLE);

            try {
                JSONObject response = new JSONObject(s);
                if (!response.getBoolean("error")) {
                    if (response.has("vagas")) {
                        JSONArray vagasArray = response.getJSONArray("vagas");

                        allVagas.clear();
                        vagasList.clear();
                        currentPosition = 0;

                        for (int i = 0; i < vagasArray.length(); i++) {
                            JSONObject vagaJson = vagasArray.getJSONObject(i);

                            String beneficios = vagaJson.optString("beneficios_vagas", "Não informado");
                            if (beneficios == null || beneficios.equals("null") || beneficios.trim().isEmpty()) {
                                beneficios = "Não informado";
                            }

                            Vagas vaga = new Vagas(
                                    vagaJson.optInt("id_vagas"),
                                    vagaJson.optString("titulo_vagas", "Não informado"),
                                    vagaJson.optString("descricao_vagas", "Não informado"),
                                    vagaJson.optString("local_vagas", "Não informado"),
                                    vagaJson.optString("salario_vagas", "Não informado"),
                                    vagaJson.optString("requisitos_vagas", "Não informado"),
                                    vagaJson.optString("nivel_experiencia", "Não informado"),
                                    vagaJson.optString("tipo_contrato", "Não informado"),
                                    vagaJson.optString("area_atuacao", "Não informado"),
                                    vagaJson.optString("beneficios_vagas", "Não informado"),
                                    vagaJson.optString("vinculo_vagas", "Não informado"),
                                    vagaJson.optString("ramo_vagas", "Não informado"),
                                    vagaJson.optInt("id_empresa"),
                                    vagaJson.optString("nome_empresa", "Empresa não informada"),
                                    null
                            );

                            allVagas.add(vaga);
                        }

                        loadNextBatch();
                    }
                } else {
                    Toast.makeText(MainActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Erro ao processar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            try {
                if (requestCode == CODE_POST_REQUEST) {
                    return requestHandler.sendPostRequest(url, params);
                } else if (requestCode == CODE_GET_REQUEST) {
                    return requestHandler.sendGetRequest(url);
                }
            } catch (Exception e) {
                return "{\"error\":true,\"message\":\"" + e.getMessage() + "\"}";
            }
            return null;
        }
    }

    private void setupNavigation() {
        idTopAppBar = findViewById(R.id.idMainTopAppBar);
        setSupportActionBar(idTopAppBar);

        idDrawer = findViewById(R.id.idDrawer);
        idNavView = findViewById(R.id.idNavView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, idDrawer, idTopAppBar,
                R.string.open_drawer, R.string.close_drawer
        );
        idDrawer.addDrawerListener(toggle);
        toggle.syncState();

        idNavView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.idLoginItemMenu) {
                startActivity(new Intent(this, LoginPessoaFisica.class));
                finish();
            } else if (id == R.id.idVagasItemMenu) {
                Toast.makeText(this, "Já está em Vagas", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.idConfigItemMenu) {
                startActivity(new Intent(this, ConfigActivity.class));
            } else if (id == R.id.idAjudaItemMenu) {
                startActivity(new Intent(this, FeedbackActivity.class));
            } else if (id == R.id.idSobreItemMenu) {
                startActivity(new Intent(this, SobreNosActivity.class));
            }

            idDrawer.closeDrawers();
            return true;
        });

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            } else if (itemId == R.id.nav_favorite) {
                startActivity(new Intent(this, FavoritosActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}