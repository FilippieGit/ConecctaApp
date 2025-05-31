package com.example.cardstackview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

        // Obter dados da Intent
        Intent intent = getIntent();
        vagaId = intent.getStringExtra("vaga_id");
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
        String resposta1 = editP1.getText().toString().trim();
        String resposta2 = editP2.getText().toString().trim();
        String resposta3 = editP3.getText().toString().trim();

        JSONObject respostasJson = new JSONObject();
        try {
            respostasJson.put("pergunta1", resposta1);
            respostasJson.put("pergunta2", resposta2);
            respostasJson.put("pergunta3", resposta3);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando respostas...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                URL url = new URL(Api.URL_CANDIDATAR_VAGA);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postData = "user_id=" + userId +
                        "&vaga_id=" + vagaId +
                        "&respostas=" + respostasJson.toString() +
                        "&vaga_titulo=" + vagaTitulo;

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    boolean success = !jsonResponse.getBoolean("error");

                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        if (success) {
                            Toast.makeText(CandidatarSeActivity.this, "Candidatura enviada com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CandidatarSeActivity.this, "Erro ao enviar candidatura", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(CandidatarSeActivity.this, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}