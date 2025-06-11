package com.example.cardstackview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CandidatarSeActivity extends AppCompatActivity {

    private TextInputEditText editP1, editP2, editP3;
    private MaterialButton btnEnviar;
    private ImageView imgVoltar;
    private String vagaId, vagaTitulo;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidatarse_layout);

        mAuth = FirebaseAuth.getInstance();

        // Verificar autenticação do usuário
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Por favor, faça login primeiro", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Obter dados da Intent
        Intent intent = getIntent();
        if (intent == null || intent.getExtras() == null) {
            Toast.makeText(this, "Erro: Dados da vaga não encontrados", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tratar vaga_id que pode ser Integer ou String
        Object vagaIdObj = intent.getExtras().get("vaga_id");
        if (vagaIdObj instanceof Integer) {
            vagaId = String.valueOf((Integer) vagaIdObj);
        } else if (vagaIdObj instanceof String) {
            vagaId = (String) vagaIdObj;
        } else {
            Toast.makeText(this, "Erro: ID da vaga inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        vagaTitulo = intent.getStringExtra("vaga_titulo");

        inicializarComponentes();
        configurarListeners();
    }

    private void inicializarComponentes() {
        imgVoltar = findViewById(R.id.imgVoltar);
        btnEnviar = findViewById(R.id.btnCriarVaga);

        TextInputLayout inputLayoutP1 = findViewById(R.id.txtCandidatarseP1);
        TextInputLayout inputLayoutP2 = findViewById(R.id.txtCandidatarseP2);
        TextInputLayout inputLayoutP3 = findViewById(R.id.txtCandidatarseP3);

        editP1 = (TextInputEditText) inputLayoutP1.getEditText();
        editP2 = (TextInputEditText) inputLayoutP2.getEditText();
        editP3 = (TextInputEditText) inputLayoutP3.getEditText();
    }

    private void configurarListeners() {
        imgVoltar.setOnClickListener(v -> finish());

        btnEnviar.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                Toast.makeText(CandidatarSeActivity.this,
                        "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
                return;
            }

            if (validarCampos()) {
                enviarRespostas();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean validarCampos() {
        boolean valido = true;

        if (editP1.getText().toString().trim().isEmpty()) {
            editP1.setError("Por favor, responda esta pergunta");
            valido = false;
        }

        if (editP2.getText().toString().trim().isEmpty()) {
            editP2.setError("Por favor, responda esta pergunta");
            valido = false;
        }

        if (editP3.getText().toString().trim().isEmpty()) {
            editP3.setError("Por favor, responda esta pergunta");
            valido = false;
        }

        return valido;
    }

    private void enviarRespostas() {
        String userId = mAuth.getCurrentUser().getUid();
        String resposta1 = editP1.getText().toString().trim();
        String resposta2 = editP2.getText().toString().trim();
        String resposta3 = editP3.getText().toString().trim();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando candidatura...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                if (!isNetworkAvailable()) {
                    throw new Exception("Sem conexão com a internet");
                }

                // Obter recrutador_id da Intent
                String recrutadorId = getIntent().getStringExtra("recrutador_id");

                // Criar JSON com estrutura completa
                JSONObject requestBody = new JSONObject();
                requestBody.put("apicall", "candidatarVaga");
                requestBody.put("user_id", userId);
                requestBody.put("vaga_id", vagaId);
                if (recrutadorId != null) {
                    requestBody.put("recrutador_id", recrutadorId);
                }

                // Criar objeto de respostas
                JSONObject respostasObj = new JSONObject();
                respostasObj.put("interesse", resposta1);
                respostasObj.put("expectativas", resposta2);
                respostasObj.put("valores", resposta3);
                requestBody.put("respostas", respostasObj.toString()); // Enviar como string JSON

                // Configurar conexão
                URL url = new URL(Api.URL_CANDIDATAR_VAGA);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                // Enviar dados
                try (OutputStream os = connection.getOutputStream();
                     BufferedWriter writer = new BufferedWriter(
                             new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                    writer.write(requestBody.toString());
                    writer.flush();
                }

                // Verificar status da resposta
                int responseCode = connection.getResponseCode();
                InputStream inputStream;
                if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    inputStream = connection.getErrorStream();
                } else {
                    inputStream = connection.getInputStream();
                }

                // Ler resposta
                StringBuilder response = new StringBuilder();
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                // Verificar se a resposta está vazia
                if (response.length() == 0) {
                    throw new Exception("Resposta vazia do servidor");
                }

                // Parse da resposta JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
                boolean error = jsonResponse.getBoolean("error");
                String message = jsonResponse.getString("message");

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    if (!error) {
                        Toast.makeText(CandidatarSeActivity.this, message, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(CandidatarSeActivity.this, "Erro: " + message, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e("Candidatura", "Erro ao enviar candidatura", e);
                final String errorMessage = (e.getMessage() != null) ? e.getMessage() : "Erro desconhecido";
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(CandidatarSeActivity.this,
                            "Erro ao enviar candidatura: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                });
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("Candidatura", "Erro ao fechar reader", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}