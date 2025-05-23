    package com.example.cardstackview;

    import android.app.Activity;
    import android.app.AlertDialog;
    import android.content.Intent;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Toast;

    import androidx.activity.OnBackPressedCallback;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.ActionBarDrawerToggle;
    import androidx.core.graphics.Insets;
    import androidx.core.view.GravityCompat;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    import androidx.drawerlayout.widget.DrawerLayout;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.google.android.material.appbar.MaterialToolbar;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.android.material.navigation.NavigationView;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.io.BufferedReader;
    import java.io.InputStreamReader;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.util.ArrayList;
    import java.util.List;

    public class ModeloTelaPrincipalFragment extends Fragment {

        private List<Vagas> listaVagas = new ArrayList<>();
        private static final int REQUEST_CRIAR_VAGA = 1001;
        private static final int REQUEST_DETALHES_VAGA = 1002;
        private static final int RESULT_EXCLUIR_VAGA = Activity.RESULT_FIRST_USER;

        private RecyclerView recyclerView;
        private AdaptadorTelaPrincipal adapter;
        private MaterialToolbar topAppBar;
        private DrawerLayout drawerLayout;
        private NavigationView navigationView;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.tela_principal_layout, container, false);

            inicializarComponentes(view);
            configurarWindowInsets(view);
            configurarDrawerLayout(view);
            configurarRecyclerView(view);
            configurarListeners(view);
            carregarVagas();

            return view;
        }

        private void inicializarComponentes(View view) {
            topAppBar = view.findViewById(R.id.idTelaPrincipalTopAppBar);
            drawerLayout = view.findViewById(R.id.idDrawer);
            navigationView = view.findViewById(R.id.idNavView);
            recyclerView = view.findViewById(R.id.idRecLista);
        }

        private void carregarVagas() {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        URL url = new URL(Api.URL_GET_VAGAS);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);

                        // Adicione logs para debug
                        System.out.println("Tentando conectar em: " + Api.URL_GET_VAGAS);

                        int responseCode = connection.getResponseCode();
                        System.out.println("Código de resposta: " + responseCode);

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(connection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            reader.close();

                            System.out.println("Resposta do servidor: " + response.toString());
                            return response.toString();
                        } else {
                            System.out.println("Erro HTTP: " + responseCode);
                            return "error:" + responseCode;
                        }
                    } catch (Exception e) {
                        System.out.println("Exceção: " + e.getMessage());
                        e.printStackTrace();
                        return "exception:" + e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String s) {
                    System.out.println("Resultado recebido: " + s);

                    if (s == null) {
                        Toast.makeText(requireContext(), "Erro: resposta nula da API", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (s.startsWith("error:")) {
                        Toast.makeText(requireContext(), "Erro HTTP: " + s.substring(6), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (s.startsWith("exception:")) {
                        Toast.makeText(requireContext(), "Exceção: " + s.substring(10), Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        JSONObject response = new JSONObject(s);
                        System.out.println("JSON recebido: " + response.toString());

                        if (response.has("error")) {
                            if (response.getBoolean("error")) {
                                String message = response.optString("message", "Erro desconhecido");
                                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        if (response.has("vagas")) {
                            JSONArray vagasArray = response.getJSONArray("vagas");
                            System.out.println("Número de vagas: " + vagasArray.length());

                            listaVagas.clear();

                            for (int i = 0; i < vagasArray.length(); i++) {
                                JSONObject vagaJson = vagasArray.getJSONObject(i);
                                System.out.println("Vaga " + i + ": " + vagaJson.toString());

                                Vagas vaga = new Vagas(
                                        vagaJson.optInt("id_vagas", 0),
                                        vagaJson.optString("titulo_vagas", "Sem título"),
                                        vagaJson.optString("descricao_vagas", "Sem descrição"),
                                        vagaJson.optString("local_vagas", "Local não informado"),
                                        vagaJson.optString("salario_vagas", "Salário não informado"),
                                        vagaJson.optString("requisitos_vagas", "Requisitos não informados"),
                                        vagaJson.optString("nivel_experiencia", "Nível não informado"),
                                        vagaJson.optString("tipo_contrato", "Tipo não informado"),
                                        vagaJson.optString("area_atuacao", "Área não informada"),
                                        vagaJson.optString("beneficios", "Sem benefícios"),
                                        vagaJson.optInt("id_empresa", 0),
                                        vagaJson.optString("nome_empresa", "Empresa não informada")
                                );

                                listaVagas.add(vaga);
                            }

                            // Verifica se o adapter foi inicializado
                            if (adapter == null) {
                                adapter = new AdaptadorTelaPrincipal(requireContext(), listaVagas);
                                recyclerView.setAdapter(adapter);
                            }

                            adapter.notifyDataSetChanged();
                            System.out.println("Total de vagas carregadas: " + listaVagas.size());
                        } else {
                            Toast.makeText(requireContext(), "Formato de resposta inválido", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(),
                                "Erro ao analisar JSON: " + e.getMessage() + "\nResposta: " + s,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }

        private void configurarWindowInsets(View view) {
            ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        private void configurarDrawerLayout(View view) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    requireActivity(),
                    drawerLayout,
                    topAppBar,
                    R.string.open_drawer,
                    R.string.close_drawer
            );
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        private void configurarRecyclerView(View view) {
            recyclerView = view.findViewById(R.id.idRecLista);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            adapter = new AdaptadorTelaPrincipal(requireContext(), listaVagas);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(vaga -> {
                Log.d("VagaClick", "Vaga clicada: " + vaga.getTitulo());

                Intent intent = new Intent(requireActivity(), DetalheVagaActivity.class);
                intent.putExtra("vaga", vaga);
                intent.putExtra("isPessoaJuridica", true); // ou false conforme necessário

                try {
                    startActivityForResult(intent, REQUEST_DETALHES_VAGA);
                } catch (Exception e) {
                    Log.e("VagaClick", "Erro ao abrir detalhes", e);
                    Toast.makeText(requireContext(), "Erro ao abrir detalhes", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void configurarListeners(View view) {
            navigationView.setNavigationItemSelectedListener(this::handleNavigationItemSelected);
            view.findViewById(R.id.idAFAB).setOnClickListener(v -> abrirCriarVaga());
        }

        private boolean handleNavigationItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.idLoginItemMenu) {
                abrirLoginCandidato();
            } else if (id == R.id.idVagasItemMenu) {
                reiniciarActivity();
            } else if (id == R.id.idConfigItemMenu) {
                exibirMensagem("Você já está em Configurações");
            } else if (id == R.id.idAjudaItemMenu) {
                abrirFeedback();
            } else if (id == R.id.idSobreItemMenu) {
                abrirSobreNos();
            }

            drawerLayout.closeDrawers();
            return true;
        }

        private void abrirCriarVaga() {
            startActivityForResult(new Intent(requireActivity(), CriarVagaActivity.class), REQUEST_CRIAR_VAGA);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            // Verifica se há dados e se a requisição é a esperada
            if (data == null) {
                return;
            }

            try {
                // Tratamento para exclusão de vaga
                if (requestCode == REQUEST_DETALHES_VAGA && resultCode == RESULT_EXCLUIR_VAGA) {
                    Vagas vagaExcluida = (Vagas) data.getSerializableExtra("vagaExcluida");
                    if (vagaExcluida != null) {
                        // Encontra a posição da vaga na lista
                        int position = -1;
                        for (int i = 0; i < listaVagas.size(); i++) {
                            if (listaVagas.get(i).getVaga_id() == vagaExcluida.getVaga_id()) {
                                position = i;
                                break;
                            }
                        }

                        if (position != -1) {
                            // Remove a vaga e atualiza a RecyclerView
                            listaVagas.remove(position);
                            adapter.notifyItemRemoved(position);

                            // Mostra mensagem de sucesso
                            Toast.makeText(getContext(), "Vaga excluída com sucesso!", Toast.LENGTH_SHORT).show();

                            // Opcional: recarrega os dados para garantir consistência
                            carregarVagas();
                        } else {
                            Toast.makeText(getContext(), "Vaga não encontrada na lista", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                // Tratamento para nova vaga publicada
                else if (requestCode == REQUEST_CRIAR_VAGA && resultCode == Activity.RESULT_OK) {
                    Vagas vaga = (Vagas) data.getSerializableExtra("vagaPublicada");
                    if (vaga != null) {
                        // Adiciona no início da lista
                        listaVagas.add(0, vaga);
                        adapter.notifyItemInserted(0);
                        recyclerView.smoothScrollToPosition(0);

                        // Mostra mensagem de sucesso
                        Toast.makeText(getContext(), "Vaga publicada com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                // Tratamento de erros genéricos
                Toast.makeText(getContext(), "Erro ao processar resultado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }


        private String getExtraKey(int requestCode) {
            return requestCode == RESULT_EXCLUIR_VAGA ? "vagaExcluida" : "vagaPublicada";
        }

        private void adicionarVaga(Vagas vaga) {
            listaVagas.add(vaga);
            adapter.notifyItemInserted(listaVagas.size() - 1);
            exibirMensagem("Vaga publicada com sucesso!");
        }

        private void atualizarVaga(Vagas vagaAtualizada) {
            for (int i = 0; i < listaVagas.size(); i++) {
                if (listaVagas.get(i).getTitulo().equals(vagaAtualizada.getTitulo())) {
                    listaVagas.set(i, vagaAtualizada);
                    adapter.notifyItemChanged(i);
                    exibirMensagem("Vaga atualizada com sucesso!");
                    break;
                }
            }
        }

        private void removerVaga(Vagas vagaExcluida) {
            for (int i = 0; i < listaVagas.size(); i++) {
                if (listaVagas.get(i).getTitulo().equals(vagaExcluida.getTitulo())) {
                    listaVagas.remove(i);
                    adapter.notifyItemRemoved(i);
                    exibirMensagem("Vaga excluída com sucesso!");
                    break;
                }
            }
        }

        private void abrirLoginCandidato() {
            startActivity(new Intent(requireActivity(), LoginPessoaFisica.class));
            requireActivity().finish();
        }

        private void reiniciarActivity() {
            startActivity(new Intent(requireActivity(), MainActivity.class));
        }

        private void abrirFeedback() {
            startActivity(new Intent(requireActivity(), FeedbackActivity.class));
        }

        private void abrirSobreNos() {
            startActivity(new Intent(requireActivity(), SobreNosActivity.class));
        }

        private void exibirMensagem(String mensagem) {
            Toast.makeText(requireContext(), mensagem, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResume() {
            super.onResume();
            configurarBotaoVoltar();
        }

        private void configurarBotaoVoltar() {
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        requireActivity().onBackPressed();
                    }
                }
            });
        }
    }
