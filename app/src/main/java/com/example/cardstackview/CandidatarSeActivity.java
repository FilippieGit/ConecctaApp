package com.example.cardstackview;

import android.app.ProgressDialog;
import android.content.Intent;
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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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

        // Obter dados da Intent de forma segura
        Intent intent = getIntent();
        if (intent != null) {
            // Tratar vaga_id que pode ser Integer ou String
            if (intent.hasExtra("vaga_id")) {
                Object vagaIdObj = intent.getExtras().get("vaga_id");
                if (vagaIdObj instanceof Integer) {
                    vagaId = String.valueOf((Integer) vagaIdObj);
                } else if (vagaIdObj instanceof String) {
                    vagaId = (String) vagaIdObj;
                }
            }

            vagaTitulo = intent.getStringExtra("vaga_titulo");
        }

        // Verificar se os dados necessários estão presentes
        if (vagaId == null) {
            Toast.makeText(this, "Erro: ID da vaga não informado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inicializarComponentes();
        configurarListeners();

        // No método onCreate()
        if (getIntent() == null || getIntent().getExtras() == null) {
            Toast.makeText(this, "Erro: Dados da vaga não encontrados", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        vagaId = getIntent().getStringExtra("vaga_id");
        vagaTitulo = getIntent().getStringExtra("vaga_titulo");

        if (vagaId == null) {
            Toast.makeText(this, "Erro: ID da vaga não informado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
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
            if (validarCampos()) {
                enviarRespostas();
            }
        });
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
        if (userId == null) {
            Toast.makeText(this, "Erro: Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (vagaId == null) {
            Toast.makeText(this, "Erro: ID da vaga não definido", Toast.LENGTH_SHORT).show();
            return;
        }

        String resposta1 = editP1.getText().toString().trim();
        String resposta2 = editP2.getText().toString().trim();
        String resposta3 = editP3.getText().toString().trim();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando candidatura...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                // Criar JSON com todas as respostas
                JSONObject respostasJson = new JSONObject();
                respostasJson.put("interesse", resposta1);
                respostasJson.put("expectativas", resposta2);
                respostasJson.put("valores", resposta3);

                // Configurar conexão - usar application/x-www-form-urlencoded
                URL url = new URL(Api.URL_CANDIDATAR_VAGA);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Criar os parâmetros no formato correto para POST
                String postData = "user_id=" + URLEncoder.encode(userId, "UTF-8") +
                        "&vaga_id=" + URLEncoder.encode(vagaId, "UTF-8") +
                        "&respostas=" + URLEncoder.encode(respostasJson.toString(), "UTF-8");

                Log.d("Candidatura", "Dados enviados: " + postData);

                // Enviar dados
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                // Processar resposta
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    Log.d("Candidatura", "Resposta do servidor: " + response.toString());

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
                } else {
                    StringBuilder errorResponse = new StringBuilder();
                    try {
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        String errorLine;
                        while ((errorLine = errorReader.readLine()) != null) {
                            errorResponse.append(errorLine);
                        }
                        errorReader.close();
                    } catch (Exception e) {
                        errorResponse.append("Não foi possível ler a mensagem de erro");
                    }

                    final String finalErrorMsg = "HTTP error code: " + responseCode + "\n" + errorResponse.toString();
                    Log.e("Candidatura", finalErrorMsg);

                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(CandidatarSeActivity.this, "Erro no servidor: " + finalErrorMsg, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e("Candidatura", "Erro ao enviar candidatura", e);
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(CandidatarSeActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}