package com.example.cardstackview;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CandidatosActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCandidatos;
    private CandidatoAdapter adapter;
    private List<Usuario> candidatosList = new ArrayList<>();
    private int vagaId;
    private ProgressDialog progressDialog;
    private Button btnNotificarAprovados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidatos_layout);

        btnNotificarAprovados = findViewById(R.id.btnNotificarAprovados);
        btnNotificarAprovados.setOnClickListener(v -> notificarTodosAprovados());

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

        // Configura o listener para mudanças de status
        adapter.setOnStatusChangeListener((position, novoStatus, motivo) -> {
            atualizarStatusCandidato(position, novoStatus, motivo);
        });

        recyclerViewCandidatos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCandidatos.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando candidatos...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        buscarCandidatos();
    }

    private void notificarTodosAprovados() {
        // Contar candidatos aprovados
        int countAprovados = 0;
        for (Usuario candidato : candidatosList) {
            if (candidato.getStatus().equalsIgnoreCase("aprovada")) {
                countAprovados++;
            }
        }

        if (countAprovados == 0) {
            Toast.makeText(this, "Não há candidatos aprovados para notificar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar confirmação antes de enviar
        new AlertDialog.Builder(this)
                .setTitle("Notificar Candidatos Aprovados")
                .setMessage("Você está prestes a enviar notificação para " + countAprovados + " candidato(s) aprovado(s). Deseja continuar?")
                .setPositiveButton("Continuar", (dialog, which) -> {
                    showMensagemDialog();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showMensagemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mensagem para os Candidatos");

        final EditText input = new EditText(this);
        input.setHint("Digite a mensagem que será enviada");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(3);
        input.setMaxLines(5);

        builder.setView(input);

        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String mensagem = input.getText().toString().trim();
            if (mensagem.isEmpty()) {
                Toast.makeText(this, "Por favor, escreva uma mensagem", Toast.LENGTH_SHORT).show();
                return;
            }

            enviarNotificacaoAprovados(mensagem);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void enviarNotificacaoAprovados(String mensagem) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando notificações...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                long recrutadorId = prefs.getLong("user_id", 0);

                URL url = new URL(Api.URL_NOTIFICAR_APROVADOS);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Parâmetros da requisição
                String postData = "vaga_id=" + URLEncoder.encode(String.valueOf(vagaId), "UTF-8") +
                        "&recrutador_id=" + URLEncoder.encode(String.valueOf(recrutadorId), "UTF-8") +
                        "&mensagem=" + URLEncoder.encode(mensagem, "UTF-8");

                // Enviar parâmetros
                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes(StandardCharsets.UTF_8));
                os.close();

                // Obter resposta
                int responseCode = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        responseCode == HttpURLConnection.HTTP_OK ?
                                connection.getInputStream() : connection.getErrorStream()));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // Verificar se a resposta não está vazia
                if (response.length() == 0) {
                    throw new Exception("Resposta vazia do servidor");
                }

                try {
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    runOnUiThread(() -> {
                        try {
                            if (!jsonResponse.getBoolean("error")) {
                                int notificados = jsonResponse.getInt("notificados");
                                int emails = jsonResponse.getInt("emails_enviados");

                                String resultado = "Notificações enviadas com sucesso!\n";
                                resultado += "Candidatos notificados: " + notificados + "\n";
                                resultado += "Emails enviados: " + emails;

                                new AlertDialog.Builder(CandidatosActivity.this)
                                        .setTitle("Sucesso")
                                        .setMessage(resultado)
                                        .setPositiveButton("OK", null)
                                        .show();
                            } else {
                                String errorMsg = jsonResponse.getString("message");
                                Toast.makeText(CandidatosActivity.this,
                                        "Erro: " + errorMsg,
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(CandidatosActivity.this,
                                    "Erro ao processar resposta do servidor",
                                    Toast.LENGTH_LONG).show();
                            Log.e("JSON_PARSE", "Erro ao extrair dados da resposta", e);
                        }
                    });
                } catch (JSONException e) {
                    throw new Exception("Resposta do servidor em formato inválido: " + response.toString());
                }

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    String errorMessage = "Erro no servidor (HTTP " + responseCode + ")";
                    if (responseCode == 500) {
                        errorMessage = "Erro interno no servidor. Por favor, tente novamente mais tarde.";
                    }
                    throw new Exception(errorMessage);
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(CandidatosActivity.this,
                            "Erro ao enviar notificações: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Log.e("NOTIFICACAO_ERRO", "Erro ao notificar aprovados", e);
                });
            } finally {
                runOnUiThread(progressDialog::dismiss);
            }
        }).start();
    }










    private void atualizarStatusCandidato(int position, String novoStatus, String motivo) {
        // Verificar conexão antes de continuar
        if (!Api.isURLReachable(this)) {
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario candidato = candidatosList.get(position);

        // Verificação de motivo para rejeição
        if ("rejeitada".equalsIgnoreCase(novoStatus) && (motivo == null || motivo.trim().isEmpty())) {
            Toast.makeText(this, "Motivo da rejeição é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar alteração")
                .setMessage("Deseja realmente alterar o status para \"" + novoStatus + "\"?")
                .setPositiveButton("Sim", (dialogInterface, i) -> {
                    ProgressDialog dialog = new ProgressDialog(this);
                    dialog.setMessage("Atualizando status...");
                    dialog.setCancelable(false);
                    dialog.show();

                    new Thread(() -> {
                        try {
                            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            long recrutadorId = prefs.getLong("user_id", 0);

                            // Configurar parâmetros
                            Map<String, String> params = new HashMap<>();
                            params.put("apicall", "atualizarStatusCandidatura");
                            params.put("candidatura_id", String.valueOf(candidato.getIdCandidatura()));
                            params.put("novo_status", novoStatus);
                            params.put("vaga_id", String.valueOf(vagaId));
                            params.put("recrutador_id", String.valueOf(recrutadorId));

                            if (motivo != null && !motivo.isEmpty()) {
                                params.put("motivo", motivo);
                            }

                            // Configurar conexão
                            URL url = new URL(Api.URL_ATUALIZAR_STATUS_CANDIDATURA);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setDoOutput(true);
                            connection.setConnectTimeout(Api.CONNECT_TIMEOUT);
                            connection.setReadTimeout(Api.READ_TIMEOUT);
                            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                            // Enviar dados
                            OutputStream os = connection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                            writer.write(getPostDataString(params));
                            writer.flush();
                            writer.close();
                            os.close();

                            // Verificar resposta
                            int responseCode = connection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                // Ler resposta completa
                                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                StringBuilder response = new StringBuilder();
                                String line;
                                while ((line = in.readLine()) != null) {
                                    response.append(line);
                                }
                                in.close();

                                JSONObject jsonResponse = new JSONObject(response.toString());
                                if (!jsonResponse.getBoolean("error")) {
                                    runOnUiThread(() -> {
                                        candidato.setStatus(novoStatus);
                                        if (novoStatus.equals("rejeitada")) {
                                            candidato.setMotivoRejeicao(motivo);
                                        }
                                        candidato.setRecrutadorId(recrutadorId);
                                        adapter.notifyItemChanged(position);
                                        Toast.makeText(CandidatosActivity.this,
                                                "Status atualizado com sucesso!",
                                                Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    throw new Exception(jsonResponse.getString("message"));
                                }
                            } else {
                                throw new Exception("Erro HTTP: " + responseCode);
                            }
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Log.e("API_ERROR", "Erro ao atualizar status", e);
                                Toast.makeText(CandidatosActivity.this,
                                        "Erro ao atualizar status: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                        } finally {
                            runOnUiThread(dialog::dismiss);
                        }
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    // Método auxiliar para converter Map para string de parâmetros
    private String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
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

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (!jsonResponse.getBoolean("error")) {
                        JSONArray candidatosArray = jsonResponse.getJSONArray("candidatos");

                        runOnUiThread(() -> {
                            candidatosList.clear();
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                                for (int i = 0; i < candidatosArray.length(); i++) {
                                    JSONObject candidatoJson = candidatosArray.getJSONObject(i);

                                    Usuario usuario = new Usuario(
                                            candidatoJson.getLong("id"),
                                            candidatoJson.getString("nome"),
                                            candidatoJson.getString("email"),
                                            candidatoJson.optString("cargo", ""),
                                            candidatoJson.getString("status"),
                                            dateFormat.parse(candidatoJson.getString("data_candidatura")),
                                            candidatoJson.optString("telefone", ""),
                                            candidatoJson.optString("descricao", ""),
                                            candidatoJson.optString("experiencia_profissional", ""),
                                            candidatoJson.optString("formacao_academica", ""),
                                            candidatoJson.optString("certificados", ""),
                                            candidatoJson.optString("username", ""),
                                            candidatoJson.optString("genero", ""),
                                            candidatoJson.optString("idade", "")
                                    );
                                    usuario.setIdCandidatura(candidatoJson.getLong("id_candidatura"));
                                    usuario.setMotivoRejeicao(candidatoJson.optString("motivo_rejeicao", null));
                                    usuario.setRecrutadorId(candidatoJson.optLong("recrutador_id", 0));

                                    candidatosList.add(usuario);
                                }
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.e("PARSE_ERROR", "Erro ao processar resposta", e);
                                Toast.makeText(CandidatosActivity.this,
                                        "Erro ao processar dados dos candidatos",
                                        Toast.LENGTH_SHORT).show();
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
        // Criar uma Intent para abrir a PerfilActivity
        Intent intent = new Intent(CandidatosActivity.this, PerfilActivity.class);

        // Passar os dados do candidato como extras
        intent.putExtra("id", usuario.getId());
        intent.putExtra("nome", usuario.getNome());
        intent.putExtra("email", usuario.getEmail());
        intent.putExtra("setor", usuario.getCargo());
        intent.putExtra("telefone", usuario.getTelefone());
        intent.putExtra("descricao", usuario.getDescricao());
        intent.putExtra("experiencia", usuario.getExperienciaProfissional());
        intent.putExtra("formacao", usuario.getFormacaoAcademica());
        intent.putExtra("certificados", usuario.getCertificados());

        // Iniciar a atividade
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
