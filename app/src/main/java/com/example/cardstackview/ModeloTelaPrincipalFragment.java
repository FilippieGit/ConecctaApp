package com.example.cardstackview;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tela_principal_layout, container, false);

        inicializarComponentes(view);
        configurarWindowInsets(view);
        configurarRecyclerView(view);
        configurarListeners(view);
        carregarVagas();

        return view;
    }

    private void inicializarComponentes(View view) {
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

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        return response.toString();
                    }
                    return "error:" + connection.getResponseCode();
                } catch (Exception e) {
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
                                    vagaJson.optString("habilidades_desejaveis", "")  // ← Corrigido: pega do JSON
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

    private void configurarRecyclerView(View view) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AdaptadorTelaPrincipal(requireContext(), listaVagas);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(vaga -> {
            Intent intent = new Intent(requireActivity(), DetalheVagaActivity.class);
            intent.putExtra("vaga", vaga);
            intent.putExtra("isPessoaJuridica", true);
            startActivityForResult(intent, REQUEST_DETALHES_VAGA);
        });
    }

    private void configurarListeners(View view) {
        view.findViewById(R.id.idAFAB).setOnClickListener(v ->
                startActivityForResult(new Intent(requireActivity(), CriarVagaActivity.class), REQUEST_CRIAR_VAGA));
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

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                });
    }
}