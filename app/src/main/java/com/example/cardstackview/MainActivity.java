package com.example.cardstackview;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class
MainActivity extends AppCompatActivity {
    private MaterialToolbar idTopAppBar;
    private DrawerLayout idDrawer;
    private NavigationView idNavView;
    private ActivityMainBinding binding;
    private CardStackLayoutManager layoutManager;
    private CardAdapter adapter;
    private List<Vagas> vagasList = new ArrayList<>();
    private ProgressBar progressBar;

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Força o tema claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(binding.getRoot());

        progressBar = findViewById(R.id.progressBar);

        // Inicializa o adapter
        adapter = new CardAdapter(this, vagasList);

        // Configura o CardStackView
        setupCardStackView();

        // Configura a navegação
        setupNavigation();

        // Busca vagas da API
        buscarVagas();
    }

    private void setupCardStackView() {
        // Configura o CardStackListener
        CardStackListener cardStackListener = new CardStackListener() {
            // No seu CardStackListener na MainActivity
            @Override
            public void onCardSwiped(Direction direction) {
                int pos = layoutManager.getTopPosition() - 1;
                if (pos >= 0 && pos < vagasList.size()) {
                    Vagas vaga = vagasList.get(pos);
                    if (direction == Direction.Right) {
                        // Adiciona aos favoritos
                        VagaDatabaseHelper dbHelper = new VagaDatabaseHelper(MainActivity.this);
                        if (!dbHelper.isVagaFavorita(vaga.getVaga_id())) {
                            dbHelper.adicionarVagaFavorita(vaga);
                            Toast.makeText(MainActivity.this, "Vaga favoritada!", Toast.LENGTH_SHORT).show();
                        }
                        registrarInteresse(vaga);
                    }
                }
            }

            @Override public void onCardDragging(Direction direction, float ratio) {}
            @Override public void onCardRewound() {}
            @Override public void onCardCanceled() {}
            @Override public void onCardAppeared(View view, int position) {}
            @Override public void onCardDisappeared(View view, int position) {}
        };

        // Configura o LayoutManager
        layoutManager = new CardStackLayoutManager(this, cardStackListener);
        layoutManager.setStackFrom(StackFrom.None);
        layoutManager.setVisibleCount(3);
        layoutManager.setDirections(Arrays.asList(Direction.Left, Direction.Right));

        // Aplica configurações ao CardStackView
        binding.cardStack.setLayoutManager(layoutManager);
        binding.cardStack.setAdapter(adapter);

        // Configura botões de ação
        ImageButton btnReject = findViewById(R.id.btnReject);
        ImageButton btnLike = findViewById(R.id.btnLike);

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
    }

    private void setupNavigation() {
        idTopAppBar = findViewById(R.id.idMainTopAppBar);
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
            if (id == R.id.idLoginItemMenu) goToLoginCandidato();
            else if (id == R.id.idVagasItemMenu)
                Toast.makeText(this, "Já está em Vagas", Toast.LENGTH_SHORT).show();
            else if (id == R.id.idConfigItemMenu) startActivity(new Intent(this, ConfigActivity.class));
            else if (id == R.id.idAjudaItemMenu) startActivity(new Intent(this, FeedbackActivity.class));
            else if (id == R.id.idSobreItemMenu) startActivity(new Intent(this, SobreNosActivity.class));
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

    private void buscarVagas() {
        new PerformNetworkRequest(Api.URL_GET_VAGAS, null, CODE_GET_REQUEST).execute();
    }

    private void registrarInteresse(Vagas vaga) {
        HashMap<String, String> params = new HashMap<>();
        params.put("vaga_id", String.valueOf(vaga.getVaga_id()));
        params.put("usuario_id", "1"); // Substitua pelo ID do usuário logado

        new PerformNetworkRequest(Api.URL_REGISTRAR_INTERESSE, params, CODE_POST_REQUEST).execute();
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

            if (!s.trim().startsWith("{")) {
                Toast.makeText(MainActivity.this, "Resposta não é JSON:\n" + s.substring(0, Math.min(200, s.length())), Toast.LENGTH_LONG).show();
                Log.e("API_RESPONSE", "Resposta não JSON: " + s);
                return;
            }
            if (s == null || s.isEmpty()) {
                Toast.makeText(MainActivity.this, "Resposta vazia do servidor", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject response = new JSONObject(s);
                if (!response.getBoolean("error")) {
                    if (response.has("vagas")) {
                        JSONArray vagasArray = response.getJSONArray("vagas");
                        vagasList.clear();

                        for (int i = 0; i < vagasArray.length(); i++) {
                            JSONObject vagaJson = vagasArray.getJSONObject(i);

                            String beneficios = vagaJson.optString("beneficios_vagas", "Não informado");
                            if (beneficios == null || beneficios.equals("null") || beneficios.trim().isEmpty()) {
                                beneficios = "Não informado";
                            }

                            Vagas vaga = new Vagas(
                                    vagaJson.optInt("id_vagas"),                                  // vaga_id
                                    vagaJson.optString("titulo_vagas", "Não informado"),          // titulo
                                    vagaJson.optString("descricao_vagas", "Não informado"),       // descricao
                                    vagaJson.optString("local_vagas", "Não informado"),           // localizacao
                                    vagaJson.optString("salario_vagas", "Não informado"),         // salario
                                    vagaJson.optString("requisitos_vagas", "Não informado"),      // requisitos
                                    vagaJson.optString("nivel_experiencia", "Não informado"),     // nivel_experiencia
                                    vagaJson.optString("tipo_contrato", "Não informado"),         // tipo_contrato
                                    vagaJson.optString("area_atuacao", "Não informado"),          // area_atuacao
                                    vagaJson.optString("beneficios_vagas", "Não informado"),      // beneficios
                                    vagaJson.optString("vinculo_vagas", "Não informado"),         // vinculo
                                    vagaJson.optString("ramo_vagas", "Não informado"),            // ramo
                                    vagaJson.optInt("id_empresa"),                                // empresa_id
                                    vagaJson.optString("nome_empresa", "Empresa não informada"),  // nome_empresa
                                    null                                                          // habilidadesDesejaveis
                            );


                            vagasList.add(vaga);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, "Dados de vagas não encontrados", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        if (idDrawer.isDrawerOpen(GravityCompat.START)) {
            idDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void goToLoginCandidato() {
        startActivity(new Intent(this, LoginPessoaFisica.class));
        finish();
    }
}