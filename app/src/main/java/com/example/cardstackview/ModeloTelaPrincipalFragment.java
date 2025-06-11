package com.example.cardstackview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModeloTelaPrincipalFragment extends Fragment {
    private List<Vagas> listaVagas = new ArrayList<>();
    private static final int REQUEST_CRIAR_VAGA = 1001;
    private static final int REQUEST_DETALHES_VAGA = 1002;
    private static final int RESULT_EXCLUIR_VAGA = Activity.RESULT_FIRST_USER;

    private RecyclerView recyclerView;
    private AdaptadorTelaPrincipal adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tela_principal_layout, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1); // Deve ser o mesmo nome usado ao salvar

        if (userId == -1) {
            Toast.makeText(requireContext(), "Erro: ID do usuário não encontrado", Toast.LENGTH_LONG).show();
            return view;
        }

        inicializarComponentes(view);
        configurarWindowInsets(view);
        configurarRecyclerView(view);
        configurarListeners(view);

        carregarVagas(userId);

        return view;
    }

    private void inicializarComponentes(View view) {
        recyclerView = view.findViewById(R.id.idRecLista);
    }

    private void carregarVagas(long userId) {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Carregando vagas...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));

        String url = Api.buildUrl(Api.URL_GET_VAGAS_BY_USER, params);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    progressDialog.dismiss();
                    processarResposta(response);
                },
                error -> {
                    progressDialog.dismiss();
                    tratarErroRequisicao(error);
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    private void processarResposta(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray vagasArray = jsonResponse.getJSONArray("vagas");

            if (vagasArray.length() == 0) {
                Toast.makeText(requireContext(), "Nenhuma vaga encontrada", Toast.LENGTH_SHORT).show();
                return;
            }

            listaVagas.clear();
            for (int i = 0; i < vagasArray.length(); i++) {
                JSONObject vagaJson = vagasArray.getJSONObject(i);
                Vagas vaga = criarVagaFromJson(vagaJson);
                listaVagas.add(vaga);
            }

            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e("API_ERROR", "Erro ao processar resposta", e);
            Toast.makeText(requireContext(), "Erro ao processar dados", Toast.LENGTH_LONG).show();
        }
    }

    private Vagas criarVagaFromJson(JSONObject vagaJson) throws JSONException {
        Vagas vaga = new Vagas();
        vaga.setVaga_id(vagaJson.optInt("id_vagas", 0));
        vaga.setTitulo(vagaJson.optString("titulo_vagas", "Sem título"));
        vaga.setDescricao(vagaJson.optString("descricao_vagas", "Sem descrição"));
        vaga.setLocalizacao(vagaJson.optString("local_vagas", "Local não especificado"));
        vaga.setSalario(vagaJson.optString("salario_vagas", "Não informado"));
        vaga.setRequisitos(vagaJson.optString("requisitos_vagas", "Não informado"));
        vaga.setVinculo(vagaJson.optString("vinculo_vagas", "Não informado"));
        vaga.setBeneficios(vagaJson.optString("beneficios_vagas", "Não informado"));
        vaga.setRamo(vagaJson.optString("ramo_vagas", "Não informado"));
        vaga.setNivel_experiencia(vagaJson.optString("nivel_experiencia", "Não informado"));
        vaga.setTipo_contrato(vagaJson.optString("tipo_contrato", "Não informado"));
        vaga.setArea_atuacao(vagaJson.optString("area_atuacao", "Não informado"));
        vaga.setHabilidadesDesejaveisStr(vagaJson.optString("habilidades_desejaveis", "Não informado"));

        // MODIFICADO: Corrigindo a leitura do id_usuario.
        // Agora, lê o id_usuario do JSON como Long e define como null se não existir ou for nulo.
        if (vagaJson.has("id_usuario") && !vagaJson.isNull("id_usuario")) {
            vaga.setId_usuario(vagaJson.optLong("id_usuario"));
        } else {
            vaga.setId_usuario(null); // Define como null se o campo não existir ou for nulo
        }

        vaga.setNome_empresa(vagaJson.optString("nome_empresa", "Empresa não informada"));
        return vaga;
    }

    private void tratarErroRequisicao(com.android.volley.VolleyError error) {
        String errorMsg = "Erro na conexão";

        if (error.networkResponse != null) {
            errorMsg += " (Status: " + error.networkResponse.statusCode + ")";
        }

        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
    }

    private void configurarWindowInsets(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void configurarRecyclerView(View view) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AdaptadorTelaPrincipal(requireContext(), listaVagas);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(vaga -> {
            Intent intent = new Intent(requireActivity(), DetalheVagaActivity.class);
            intent.putExtra("vaga", vaga);
            startActivityForResult(intent, REQUEST_DETALHES_VAGA);
        });
    }

    private void configurarListeners(View view) {
        FloatingActionButton fab = view.findViewById(R.id.idAFAB);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                long userId = prefs.getLong("user_id", -1);

                if (userId != -1) {
                    Intent intent = new Intent(requireActivity(), CriarVagaActivity.class);
                    intent.putExtra("user_id", userId);
                    startActivityForResult(intent, REQUEST_CRIAR_VAGA);
                } else {
                    Toast.makeText(requireContext(), "Erro ao identificar usuário", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;

        try {
            if (requestCode == REQUEST_DETALHES_VAGA && resultCode == RESULT_EXCLUIR_VAGA) {
                Vagas vagaExcluida = (Vagas) data.getSerializableExtra("vagaExcluida");
                if (vagaExcluida != null) {
                    for (int i = 0; i < listaVagas.size(); i++) {
                        if (listaVagas.get(i).getVaga_id() == vagaExcluida.getVaga_id()) {
                            listaVagas.remove(i);
                            adapter.notifyItemRemoved(i);
                            Toast.makeText(getContext(), "Vaga excluída!", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            } else if (requestCode == REQUEST_CRIAR_VAGA && resultCode == Activity.RESULT_OK) {
                Vagas vaga = (Vagas) data.getSerializableExtra("vagaPublicada");
                if (vaga != null) {
                    listaVagas.add(0, vaga);
                    adapter.notifyItemInserted(0);
                    recyclerView.smoothScrollToPosition(0);
                    Toast.makeText(getContext(), "Vaga publicada!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}