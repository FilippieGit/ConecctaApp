package com.example.cardstackview;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CandidatosActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCandidatos;
    private CandidatoAdapter adapter;
    private List<Usuario> candidatosList = new ArrayList<>();
    private int vagaId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidatos_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        vagaId = getIntent().getIntExtra("vaga_id", 0);
        setTitle("Candidatos - Vaga #" + vagaId);

        recyclerViewCandidatos = findViewById(R.id.recyclerViewCandidatos);
        adapter = new CandidatoAdapter(candidatosList, this::onCandidatoClick);
        recyclerViewCandidatos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCandidatos.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando candidatos...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        buscarCandidatos();
    }

    private void buscarCandidatos() {
        new Thread(() -> {
            try {
                String urlCompleta = Api.URL_LISTAR_CANDIDATURAS + "&vaga_id=" + vagaId;
                Log.d("API_REQUEST", "URL: " + urlCompleta);

                URL url = new URL(urlCompleta);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    Log.d("API_RESPONSE", "Resposta: " + response.toString());

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (!jsonResponse.getBoolean("error")) {
                        JSONArray candidatosArray = jsonResponse.getJSONArray("candidatos");

                        runOnUiThread(() -> {
                            candidatosList.clear();
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                                for (int i = 0; i < candidatosArray.length(); i++) {
                                    JSONObject candidatoJson = candidatosArray.getJSONObject(i);

                                    // Trate a data corretamente
                                    Date dataCandidatura = dateFormat.parse(candidatoJson.getString("data_candidatura"));

                                    // Crie o objeto Usuario com todos os campos
                                    Usuario usuario = new Usuario(
                                            candidatoJson.getLong("id"),
                                            candidatoJson.getString("nome"),
                                            candidatoJson.getString("email"),
                                            candidatoJson.optString("cargo", ""),
                                            candidatoJson.getString("status"),
                                            dataCandidatura,
                                            candidatoJson.optString("telefone", ""),
                                            candidatoJson.optString("descricao", ""),
                                            candidatoJson.optJSONObject("respostas"),
                                            candidatoJson.optInt("id_candidatura", 0)
                                    );
                                    candidatosList.add(usuario);
                                }
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                Log.e("JSON_ERROR", "Erro ao processar JSON", e);
                                Toast.makeText(CandidatosActivity.this,
                                        "Erro ao processar dados dos candidatos",
                                        Toast.LENGTH_SHORT).show();
                            } catch (ParseException e) {
                                Log.e("DATE_ERROR", "Erro ao parsear data", e);
                            } finally {
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        throw new Exception(jsonResponse.getString("message"));
                    }
                } else {
                    throw new Exception("Código de erro HTTP: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("NETWORK_ERROR", "Erro na requisição", e);
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(CandidatosActivity.this,
                            "Erro ao carregar candidatos: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void onCandidatoClick(Usuario usuario) {
        Toast.makeText(this, "Candidato: " + usuario.getNome(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
