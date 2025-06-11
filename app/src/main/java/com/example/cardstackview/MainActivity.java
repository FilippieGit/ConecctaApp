package com.example.cardstackview;

import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.concurrent.RejectedExecutionException;

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
    private static final int BATCH_SIZE = 20;
    private boolean isLoading = false;
    private NavigationView navigationView;
    private boolean isSwiping = false;
    private long lastSwipeTime = 0;
    private static final long MIN_SWIPE_INTERVAL = 500;

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private static final int REQUEST_CODE_FAVORITOS = 100;

    private ExecutorService executor;
    private VagaDatabaseHelper dbHelper;
    private boolean showFavoritesOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.getRoot());

        executor = AppExecutor.getExecutor();
        dbHelper = new VagaDatabaseHelper(this);
        progressBar = findViewById(R.id.progressBar);
        adapter = new CardAdapter(this, vagasList);

        setupCardStackView();
        setupNavigation();
        buscarVagas();

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userType = prefs.getString("user_type", "Física");
        boolean isEmpresa = userType.equalsIgnoreCase("Jurídica");

        navigationView = findViewById(R.id.navigation_view);

        if (navigationView != null) {
            // A lógica de visibilidade dos itens do menu para diferentes tipos de usuário
            // parece estar invertida ou configurada para esconder os itens em ambos os casos.
            // Revise esta parte se a intenção é que usuários "Jurídica" vejam "Criar Vagas"
            // e "Login", e "Física" não vejam.
            if (isEmpresa) {
                navigationView.getMenu().findItem(R.id.idCriarVagasItemMenu).setVisible(true); // Exemplo: para empresas
                navigationView.getMenu().findItem(R.id.idLoginItemMenu).setVisible(false); // Exemplo: já logado, não precisa de login
            } else {
                navigationView.getMenu().findItem(R.id.idCriarVagasItemMenu).setVisible(false);
                navigationView.getMenu().findItem(R.id.idLoginItemMenu).setVisible(true); // Exemplo: para usuários físicos se quiserem fazer login
            }
        }
    }

    private void setupCardStackView() {
        CardStackListener cardStackListener = new CardStackListener() {
            @Override
            public void onCardSwiped(Direction direction) {
                long now = System.currentTimeMillis();
                if (isSwiping || now - lastSwipeTime < MIN_SWIPE_INTERVAL ||
                        layoutManager.getTopPosition() >= vagasList.size()) {
                    return;
                }

                isSwiping = true;
                lastSwipeTime = now;

                int pos = layoutManager.getTopPosition() - 1;
                if (pos >= 0 && pos < vagasList.size()) {
                    Vagas vaga = vagasList.get(pos);

                    AppExecutor.getExecutor().execute(() -> {
                        if (direction == Direction.Right) {
                            processarLike(vaga);
                        } else if (direction == Direction.Left) {
                            // Ao deslizar para a esquerda, registra interesse, mas não salva nos favoritos
                            registrarInteresse(vaga);
                        }

                        runOnUiThread(() -> {
                            if (pos >= vagasList.size() - 5 && currentPosition < allVagas.size()) {
                                loadNextBatch();
                            }
                            isSwiping = false;
                        });
                    });
                } else {
                    isSwiping = false;
                }
            }

            @Override
            public void onCardDragging(Direction direction, float ratio) {}

            @Override public void onCardRewound() {}
            @Override public void onCardCanceled() {}
            @Override public void onCardAppeared(View view, int position) {}
            @Override public void onCardDisappeared(View view, int position) {}
        };

        layoutManager = new CardStackLayoutManager(this, cardStackListener);
        layoutManager.setStackFrom(StackFrom.None);
        layoutManager.setVisibleCount(3);
        layoutManager.setDirections(Arrays.asList(Direction.Left, Direction.Right));

        binding.cardStack.setLayoutManager(layoutManager);
        binding.cardStack.setAdapter(adapter);

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        ImageButton btnReject = findViewById(R.id.btnReject);
        ImageButton btnLike = findViewById(R.id.btnLike);
        ImageButton btnRefresh = findViewById(R.id.btnRefresh);

        btnLike.setOnClickListener(v -> {
            if (canSwipe()) {
                v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100)
                        .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100));
                isSwiping = true;
                lastSwipeTime = System.currentTimeMillis();

                int pos = layoutManager.getTopPosition();
                if (pos < vagasList.size()) {
                    Vagas vaga = vagasList.get(pos);
                    processarLike(vaga); // Processa o like (favorita e registra interesse)
                }

                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(400)
                        .build();
                layoutManager.setSwipeAnimationSetting(setting);
                binding.cardStack.swipe();

                new Handler(Looper.getMainLooper()).postDelayed(() -> isSwiping = false, 500);
            }
        });

        btnReject.setOnClickListener(v -> {
            if (canSwipe()) {
                isSwiping = true;
                lastSwipeTime = System.currentTimeMillis();

                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(400)
                        .build();
                layoutManager.setSwipeAnimationSetting(setting);
                binding.cardStack.swipe();

                int pos = layoutManager.getTopPosition();
                if (pos < vagasList.size()) {
                    Vagas vaga = vagasList.get(pos);
                    // Ação para rejeitar: apenas registrar interesse (ou ignorar)
                    registrarInteresse(vaga); // Apenas registra interesse para rejeição
                }

                new Handler(Looper.getMainLooper()).postDelayed(() -> isSwiping = false, 500);
            }
        });

        btnRefresh.setOnClickListener(v -> {
            v.animate().rotationBy(360).setDuration(500);
            recarregarCards();
        });
    }

    private void recarregarCards() {
        allVagas.clear();
        vagasList.clear();
        currentPosition = 0;
        adapter.notifyDataSetChanged();

        progressBar.setVisibility(View.VISIBLE);
        binding.cardStack.setVisibility(View.GONE);

        buscarVagas();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (layoutManager != null) {
                layoutManager.setTopPosition(0);
            }
            binding.cardStack.setVisibility(View.VISIBLE);
        }, 500);
    }

    private boolean canSwipe() {
        long now = System.currentTimeMillis();
        return !isSwiping &&
                now - lastSwipeTime >= MIN_SWIPE_INTERVAL &&
                !vagasList.isEmpty() &&
                layoutManager.getTopPosition() < vagasList.size();
    }

    private void processarLike(Vagas vaga) {
        if (vaga == null) return;

        // Registrar interesse sempre, independente de favoritar ou não
        registrarInteresse(vaga);

        runOnUiThread(() -> {
            if (!dbHelper.isVagaFavorita(vaga.getVaga_id())) {
                boolean salvou = dbHelper.adicionarVagaFavorita(vaga);
                if (salvou) {
                    Toast.makeText(MainActivity.this, "Vaga favoritada com sucesso!", Toast.LENGTH_SHORT).show();
                    Log.d("FAVORITOS", "Vaga " + vaga.getVaga_id() + " salva nos favoritos");

                    // Não remove mais a vaga da lista imediatamente se o objetivo é apenas favoritar
                    // Se você quer que a vaga desapareça depois de favoritar, mantenha a remoção
                    // int index = vagasList.indexOf(vaga);
                    // if (index != -1) {
                    //     vagasList.remove(index);
                    //     adapter.notifyItemRemoved(index);
                    // }

                    if (vagasList.size() < 5 && currentPosition < allVagas.size()) {
                        loadNextBatch();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Erro ao favoritar vaga", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Esta vaga já está nos seus favoritos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNextBatch() {
        Log.d("LOAD_BATCH", "Carregando lote. Posição atual: " + currentPosition +
                "/" + allVagas.size() + ", isLoading: " + isLoading);

        if (isLoading || isSwiping || currentPosition >= allVagas.size()) {
            Log.d("LOAD_BATCH", "Não carregou - Condição não atendida");
            return;
        }

        isLoading = true;
        binding.progressBar.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<Vagas> batch = new ArrayList<>();
            int loadedCount = 0;

            while (currentPosition < allVagas.size() && loadedCount < BATCH_SIZE) {
                Vagas vaga = allVagas.get(currentPosition);
                // Skip favorited cards
                if (!dbHelper.isVagaFavorita(vaga.getVaga_id())) {
                    batch.add(vaga);
                    loadedCount++;
                }
                currentPosition++;
            }

            Log.d("LOAD_BATCH", batch.size() + " vagas carregadas");

            if (!batch.isEmpty()) {
                int startPosition = vagasList.size();
                vagasList.addAll(batch);
                adapter.notifyItemRangeInserted(startPosition, batch.size());
            } else if (currentPosition >= allVagas.size()) {
                Toast.makeText(MainActivity.this, "Todas as vagas foram carregadas", Toast.LENGTH_SHORT).show();
            }

            isLoading = false;
            binding.progressBar.setVisibility(View.GONE);
            preloadImages(batch);
        }, 150);
    }

    private void preloadImages(List<Vagas> batch) {
        try {
            AppExecutor.getExecutor().execute(() -> {
                for (Vagas vaga : batch) {
                    Log.d("Preload", "Pré-carregando vaga: " + vaga.getVaga_id());
                }
            });
        } catch (RejectedExecutionException e) {
            Log.e("MainActivity", "Erro no pré-carregamento", e);
        }
    }

    private void buscarVagas() {
        Log.d("API", "Iniciando busca por vagas...");
        // Clear existing data
        allVagas.clear();
        vagasList.clear();
        currentPosition = 0;
        adapter.notifyDataSetChanged();

        new PerformNetworkRequest(Api.URL_GET_VAGAS, null, CODE_GET_REQUEST).execute();
    }

    private void registrarInteresse(Vagas vaga) {
        AppExecutor.getExecutor().execute(() -> {
            HashMap<String, String> params = new HashMap<>();
            params.put("vaga_id", String.valueOf(vaga.getVaga_id()));
            // ATENÇÃO: 'usuario_id' está fixo como "1" aqui.
            // Certifique-se de que isso é intencional ou substitua pelo ID do usuário logado.
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

                            Vagas vaga = new Vagas();
                            vaga.setVaga_id(vagaJson.optInt("id_vagas"));
                            vaga.setTitulo(vagaJson.optString("titulo_vagas", "Não informado"));
                            vaga.setDescricao(vagaJson.optString("descricao_vagas", "Não informado"));
                            vaga.setLocalizacao(vagaJson.optString("local_vagas", "Não informado"));
                            vaga.setSalario(vagaJson.optString("salario_vagas", "Não informado"));
                            vaga.setRequisitos(vagaJson.optString("requisitos_vagas", "Não informado"));
                            vaga.setNivel_experiencia(vagaJson.optString("nivel_experiencia", "Não informado"));
                            vaga.setTipo_contrato(vagaJson.optString("tipo_contrato", "Não informado"));
                            vaga.setArea_atuacao(vagaJson.optString("area_atuacao", "Não informado"));
                            vaga.setBeneficios(beneficios);
                            vaga.setVinculo(vagaJson.optString("vinculo_vagas", "Não informado"));
                            vaga.setRamo(vagaJson.optString("ramo_vagas", "Não informado"));
                            vaga.setEmpresa_id(vagaJson.optInt("id_empresa"));
                            vaga.setNome_empresa(vagaJson.optString("nome_empresa", "Empresa não informada"));
                            vaga.setHabilidadesDesejaveisStr(null);

                            // MODIFICADO: Corrigindo o problema do id_usuario.
                            // Agora, lê o id_usuario do JSON em vez de hardcodar 0.
                            // Verifica se o campo existe e não é nulo no JSON.
                            if (vagaJson.has("id_usuario") && !vagaJson.isNull("id_usuario")) {
                                vaga.setId_usuario(vagaJson.optLong("id_usuario"));
                            } else {
                                vaga.setId_usuario(null); // Define como null se o campo não existir ou for nulo
                            }

                            allVagas.add(vaga);
                        }

                        Log.d("API", "Total de vagas recebidas: " + allVagas.size());

                        if (!allVagas.isEmpty()) {
                            loadNextBatch();
                        } else {
                            Toast.makeText(MainActivity.this, "Nenhuma vaga disponível", Toast.LENGTH_SHORT).show();
                        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FAVORITOS) {
            // Após retornar dos favoritos, recarrega todas as vagas para garantir
            // que as alterações de favoritar/desfavoritar sejam refletidas.
            allVagas.clear();
            vagasList.clear();
            currentPosition = 0;
            buscarVagas();
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
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else if (id == R.id.idVagasItemMenu) {
                Toast.makeText(this, "Já está em Vagas", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.idConfigItemMenu) {
                startActivity(new Intent(this, ConfigActivity.class));
            } else if (id == R.id.idAjudaItemMenu) {
                startActivity(new Intent(this, FeedbackActivity.class));
            } else if (id == R.id.idSobreItemMenu) {
                startActivity(new Intent(this, SobreNosActivity.class));
            } else if (id == R.id.idCriarVagasItemMenu) {
                startActivity(new Intent(this, CriarVagaActivity.class));
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
                startActivityForResult(new Intent(this, FavoritosActivity.class), REQUEST_CODE_FAVORITOS);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public static class AppExecutor {
        private static ExecutorService instance;

        public static synchronized ExecutorService getExecutor() {
            if (instance == null || instance.isShutdown() || instance.isTerminated()) {
                instance = Executors.newFixedThreadPool(2);
            }
            return instance;
        }

        public static void shutdown() {
            if (instance != null) {
                instance.shutdownNow();
                instance = null;
            }
        }
    }
}