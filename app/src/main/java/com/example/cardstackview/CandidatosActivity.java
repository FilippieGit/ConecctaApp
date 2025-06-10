package com.example.cardstackview;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
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
    private static final String TAG = "CandidatosActivity";
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

        // Inicializa os componentes
        btnNotificarAprovados = findViewById(R.id.btnNotificarAprovados);
        recyclerViewCandidatos = findViewById(R.id.recyclerViewCandidatos);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Configura a Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        vagaId = getIntent().getIntExtra("vaga_id", 0);
        setTitle("Candidatos - Vaga #" + vagaId);

        // Configura o adapter UMA ÚNICA VEZ
        adapter = new CandidatoAdapter(candidatosList, this::onCandidatoClick);

        // Configura o listener corretamente
        adapter.setOnStatusChangeListener((position, novoStatus) -> {
            Log.d(TAG, "Solicitada mudança de status para: " + novoStatus);
            atualizarStatusCandidato(position, novoStatus);
        });

        recyclerViewCandidatos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCandidatos.setAdapter(adapter);

        // Configura listeners
        btnNotificarAprovados.setOnClickListener(v -> notificarTodosAprovados());

        // Carrega os candidatos
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando candidatos...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        buscarCandidatos();
    }


    private void notificarTodosAprovados() {
        // Filtrar apenas candidatos aprovados
        List<Usuario> aprovados = new ArrayList<>();
        for (Usuario candidato : candidatosList) {
            if (candidato.getStatus().equalsIgnoreCase("aprovada")) {
                aprovados.add(candidato);
            }
        }

        if (aprovados.isEmpty()) {
            Toast.makeText(this, "Não há candidatos aprovados para notificar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar diálogo para confirmar o envio
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enviar emails para " + aprovados.size() + " aprovado(s)");
        builder.setMessage("Deseja enviar emails de aprovação para todos os candidatos aprovados?");

        builder.setPositiveButton("Enviar", (dialog, which) -> {
            enviarEmailsAprovados(aprovados);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void enviarEmailsAprovados(List<Usuario> aprovados) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando emails...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            int emailsEnviados = 0;
            int falhas = 0;
            StringBuilder erros = new StringBuilder();

            String assunto = "Parabéns! Você foi aprovado para a vaga #" + vagaId;
            String corpo = "Prezado candidato,\n\n" +
                    "É com grande satisfação que informamos que você foi aprovado no processo seletivo!\n\n" +
                    "Em breve nossa equipe entrará em contato com os próximos passos.\n\n" +
                    "Atenciosamente,\n" +
                    "Equipe de Recrutamento";

            // Defina seu email e senha do Gmail (ou outro SMTP)
            String emailRemetente = "vagas.coneccta@gmail.com";
            String senhaRemetente = "wxka yvsw luer kfgv";

            EmailSender emailSender = new EmailSender(emailRemetente, senhaRemetente);

            for (Usuario candidato : aprovados) {
                try {
                    emailSender.enviarEmail(candidato.getEmail(), assunto, corpo);
                    emailsEnviados++;
                } catch (Exception e) {
                    falhas++;
                    erros.append("• ").append(candidato.getEmail()).append(": ")
                            .append(e.getMessage()).append("\n");
                    Log.e("EMAIL_ERROR", "Erro ao enviar para " + candidato.getEmail(), e);
                }
            }

            final int enviadosFinal = emailsEnviados;
            final int falhasFinal = falhas;
            final String errosFinal = erros.toString();
            final Context context = this;

            runOnUiThread(() -> {
                progressDialog.dismiss();

                String mensagemFinal = "Tentativa de envio concluída:\n\n" +
                        "• Emails enviados: " + enviadosFinal + "\n" +
                        "• Falhas: " + falhasFinal +
                        (falhasFinal > 0 ? "\n\nErros encontrados:\n" + errosFinal : "");

                if (falhasFinal > 0) {
                    new AlertDialog.Builder(context)
                            .setTitle("Relatório de Envio")
                            .setMessage(mensagemFinal)
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    Toast.makeText(context, mensagemFinal, Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }







    private void enviarEmailIndividual(String emailDestinatario, String assunto, String corpo) throws Exception {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // Tipo para email
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailDestinatario});
        intent.putExtra(Intent.EXTRA_SUBJECT, assunto);
        intent.putExtra(Intent.EXTRA_TEXT, corpo);

        if (intent.resolveActivity(getPackageManager()) != null) {
            runOnUiThread(() -> startActivity(Intent.createChooser(intent, "Enviar email usando:")));
        } else {
            runOnUiThread(() -> {
                String mensagemErro = "Nenhum app de email instalado. Você pode:\n\n" +
                        "1. Instalar um app de email como Gmail ou Outlook\n" +
                        "2. Enviar manualmente para: " + emailDestinatario + "\n" +
                        "Assunto: " + assunto + "\n\n" +
                        "Corpo:\n" + corpo;

                new AlertDialog.Builder(CandidatosActivity.this)
                        .setTitle("Não foi possível enviar email")
                        .setMessage(mensagemErro)
                        .setPositiveButton("Copiar detalhes", (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            String clipText = "Para: " + emailDestinatario + "\n" +
                                    "Assunto: " + assunto + "\n\n" +
                                    corpo;
                            ClipData clip = ClipData.newPlainText("Email de aprovação", clipText);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(CandidatosActivity.this, "Detalhes copiados", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("OK", null)
                        .show();
            });

            throw new Exception("Nenhum app de email instalado");
        }
    }




    private void enviarNotificacoesAprovados(String mensagem) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando notificações...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                long recrutadorId = prefs.getLong("user_id", 0);

                // Configurar parâmetros
                Map<String, String> params = new HashMap<>();
                params.put("apicall", "notificarTodosAprovados");
                params.put("vaga_id", String.valueOf(vagaId));
                params.put("recrutador_id", String.valueOf(recrutadorId));
                params.put("mensagem", mensagem);

                Log.d(TAG, "Enviando para o servidor: " + params);

                // Configurar conexão
                URL url = new URL(Api.URL_NOTIFICAR_APROVADOS);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Connection", "close"); // Evitar keep-alive

                // Enviar dados
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(getPostDataString(params));
                writer.flush();
                writer.close();
                os.close();

                // Verificar código de resposta
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Código de resposta: " + responseCode);

                // Ler resposta mesmo em caso de erro
                InputStream inputStream = connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                Log.d(TAG, "Resposta completa: " + response.toString());

                if (response.length() == 0) {
                    throw new Exception("Resposta vazia do servidor");
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                runOnUiThread(() -> {
                    try {
                        if (!jsonResponse.getBoolean("error")) {
                            showResultDialog(jsonResponse);
                        } else {
                            Toast.makeText(CandidatosActivity.this,
                                    "Erro: " + jsonResponse.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        handleJsonError(e, response.toString());
                    }
                });

            } catch (SocketTimeoutException e) {
                handleNetworkError("Tempo de conexão esgotado", e);
            } catch (ConnectException e) {
                handleNetworkError("Não foi possível conectar ao servidor", e);
            } catch (ProtocolException e) {
                handleNetworkError("Erro de protocolo na conexão", e);
            } catch (IOException e) {
                handleNetworkError("Erro de comunicação com o servidor", e);
            } catch (JSONException e) {
                handleJsonError(e, "");
            } catch (Exception e) {
                handleGenericError(e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                runOnUiThread(progressDialog::dismiss);
            }
        }).start();
    }

    private void showResultDialog(JSONObject jsonResponse) throws JSONException {
        int notificados = jsonResponse.getInt("notificados");
        int emails = jsonResponse.getInt("emails_enviados");
        int pushes = jsonResponse.getInt("notificacoes_enviadas");

        String resultado = "Notificações enviadas com sucesso!\n\n";
        resultado += "Candidatos notificados: " + notificados + "\n";
        resultado += "E-mails enviados: " + emails + "\n";
        resultado += "Notificações push: " + pushes;

        if (jsonResponse.has("erros")) {
            JSONArray erros = jsonResponse.getJSONArray("erros");
            resultado += "\n\nErros (" + erros.length() + "):\n";
            for (int i = 0; i < erros.length() && i < 3; i++) {
                resultado += "• " + erros.getString(i) + "\n";
            }
            if (erros.length() > 3) {
                resultado += "• ...e mais " + (erros.length() - 3) + " erros";
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Resultado")
                .setMessage(resultado)
                .setPositiveButton("OK", null)
                .show();
    }

    private void handleNetworkError(String message, Exception e) {
        Log.e(TAG, message, e);
        runOnUiThread(() -> {
            Toast.makeText(CandidatosActivity.this,
                    message + ". Verifique sua conexão e tente novamente.",
                    Toast.LENGTH_LONG).show();
        });
    }

    private void handleJsonError(JSONException e, String response) {
        Log.e(TAG, "Erro ao processar JSON: " + e.getMessage(), e);
        Log.e(TAG, "Resposta original: " + response);

        runOnUiThread(() -> {
            Toast.makeText(CandidatosActivity.this,
                    "Erro no formato da resposta do servidor",
                    Toast.LENGTH_LONG).show();
        });
    }

    private void handleGenericError(Exception e) {
        Log.e(TAG, "Erro inesperado: " + e.getMessage(), e);
        runOnUiThread(() -> {
            Toast.makeText(CandidatosActivity.this,
                    "Erro: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        });
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

    private void atualizarStatusCandidato(int position, String novoStatus) {
        Log.d(TAG, "Atualizando status para posição " + position + " para " + novoStatus);

        if (position < 0 || position >= candidatosList.size()) {
            Log.e(TAG, "Posição inválida: " + position);
            return;
        }

        Usuario candidato = candidatosList.get(position);
        Log.d(TAG, "Status atual: " + candidato.getStatus() + ", Novo status: " + novoStatus);

        // Atualiza localmente primeiro para feedback imediato
        String statusAnterior = candidato.getStatus(); // Guarda o status anterior
        candidato.setStatus(novoStatus);
        adapter.notifyItemChanged(position);

        // Mostra diálogo de confirmação
        new AlertDialog.Builder(this)
                .setTitle("Confirmar alteração")
                .setMessage("Deseja realmente alterar o status para \"" + novoStatus + "\"?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    atualizarStatusNoServidor(position, novoStatus, candidato);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    // Reverte se o usuário cancelar
                    candidato.setStatus(statusAnterior);
                    adapter.notifyItemChanged(position);
                })
                .show();
    }

    private void atualizarStatusNoServidor(int position, String novoStatus, Usuario candidato) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Atualizando status...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(() -> {
            try {
                // Verificar dados
                if (candidato.getIdCandidatura() == 0 || vagaId == 0) {
                    throw new Exception("Dados inválidos para atualização");
                }

                // Configurar parâmetros
                Map<String, String> params = new HashMap<>();
                params.put("apicall", "atualizarStatusCandidatura");
                params.put("candidatura_id", String.valueOf(candidato.getIdCandidatura()));
                params.put("novo_status", novoStatus.toLowerCase());
                params.put("vaga_id", String.valueOf(vagaId));

                Log.d(TAG, "Enviando para o servidor: " + params);

                // Configurar conexão
                URL url = new URL(Api.URL_ATUALIZAR_STATUS_CANDIDATURA);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Enviar dados
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(getPostDataString(params));
                writer.flush();
                writer.close();
                os.close();

                // Processar resposta
                int responseCode = connection.getResponseCode();
                InputStream inputStream = responseCode == HttpURLConnection.HTTP_OK ?
                        connection.getInputStream() : connection.getErrorStream();

                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                Log.d(TAG, "Resposta bruta do servidor: " + response.toString());

                // Verificar se a resposta é JSON válido
                if (response.toString().startsWith("<")) {
                    throw new Exception("Erro no servidor: resposta em formato inválido");
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                if (!jsonResponse.getBoolean("error")) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Status atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                        // Atualizar localmente
                        candidatosList.get(position).setStatus(novoStatus);
                        adapter.notifyItemChanged(position);
                    });
                } else {
                    throw new Exception(jsonResponse.getString("message"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao atualizar status", e);
                runOnUiThread(() -> {
                    // Reverter mudança local
                    adapter.notifyItemChanged(position);

                    // Mostrar mensagem de erro adequada
                    String errorMsg = e.getMessage();
                    if (errorMsg != null && errorMsg.contains("JSONObject")) {
                        errorMsg = "Erro no formato da resposta do servidor";
                    }
                    Toast.makeText(this, "Erro: " + errorMsg, Toast.LENGTH_LONG).show();
                });
            } finally {
                runOnUiThread(dialog::dismiss);
            }
        }).start();
    }

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
        Log.d(TAG, "Buscando candidatos para vaga: " + vagaId);
        new Thread(() -> {
            try {
                String urlCompleta = Api.URL_LISTAR_CANDIDATURAS + "&vaga_id=" + vagaId;
                Log.d(TAG, "URL: " + urlCompleta);
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
                Log.e(TAG, "Erro ao buscar candidatos", e);
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void onCandidatoClick(Usuario usuario) {
        Intent intent = new Intent(CandidatosActivity.this, PerfilActivity.class);
        intent.putExtra("id", usuario.getId());
        intent.putExtra("nome", usuario.getNome());
        intent.putExtra("email", usuario.getEmail());
        intent.putExtra("setor", usuario.getCargo());
        intent.putExtra("telefone", usuario.getTelefone());
        intent.putExtra("descricao", usuario.getDescricao());
        intent.putExtra("experiencia", usuario.getExperienciaProfissional());
        intent.putExtra("formacao", usuario.getFormacaoAcademica());
        intent.putExtra("certificados", usuario.getCertificados());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}