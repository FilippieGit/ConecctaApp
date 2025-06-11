package com.example.cardstackview;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

    private String tituloVaga;  // Define as class fields
    private String nomeEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidatos_layout);

        // Inicializa os componentes
        btnNotificarAprovados = findViewById(R.id.btnNotificarAprovados);
        recyclerViewCandidatos = findViewById(R.id.recyclerViewCandidatos);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Initialize with default values
        tituloVaga = "Vaga";
        nomeEmpresa = "Empresa";

        // Configura a Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        vagaId = getIntent().getIntExtra("vaga_id", 0);
        // Define um título temporário
        setTitle("Processo Seletivo");

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

        buscarDetalhesVaga();
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
            enviarEmailsAprovados(aprovados, tituloVaga, nomeEmpresa);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void buscarDetalhesVaga() {
        new Thread(() -> {
            try {
                // URL corrigida - removendo o parâmetro duplicado
                String urlCompleta = Api.BASE_URL + "Api.php?apicall=getVagaById&vaga_id=" + vagaId;
                Log.d(TAG, "URL corrigida: " + urlCompleta);

                URL url = new URL(urlCompleta);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Código de resposta: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    inputStream.close();

                    Log.d(TAG, "Resposta bruta do servidor: " + response.toString());

                    JSONObject jsonResponse = new JSONObject(response.toString());

                    if (!jsonResponse.getBoolean("error")) {
                        JSONObject vagaJson = jsonResponse.getJSONObject("vaga");

                        tituloVaga = vagaJson.optString("titulo_vagas", "Vaga sem título");
                        nomeEmpresa = vagaJson.optString("nome_empresa", "Empresa não especificada");

                        Log.d(TAG, "Dados extraídos - Título: " + tituloVaga + ", Empresa: " + nomeEmpresa);

                        runOnUiThread(() -> {
                            setTitle(nomeEmpresa + " - Processo Seletivo: " + tituloVaga);
                        });
                    } else {
                        Log.e(TAG, "Erro na resposta: " + jsonResponse.optString("message", ""));
                    }
                } else {
                    throw new Exception("HTTP error code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar detalhes da vaga", e);
                runOnUiThread(() -> {
                    tituloVaga = "Vaga ID: " + vagaId;
                    nomeEmpresa = "Empresa";
                    setTitle("Processo Seletivo");
                    Toast.makeText(this, "Não foi possível carregar todos os detalhes da vaga",
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private boolean isEnvioEmAndamento = false; // Variável de controle de estado

    private void enviarEmailsAprovados(List<Usuario> aprovados, String tituloVaga, String nomeEmpresa) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando emails...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Usamos um array para contornar a restrição de final/effectively final
        final int[] contadores = new int[2]; // [0] = emailsEnviados, [1] = falhas
        final StringBuilder erros = new StringBuilder();

        new Thread(() -> {
            // Tratamento para valores nulos ou vazios
            String tituloFinal = (tituloVaga == null || tituloVaga.isEmpty()) ? "Vaga" : tituloVaga;
            String empresaFinal = (nomeEmpresa == null || nomeEmpresa.isEmpty()) ? "Empresa" : nomeEmpresa;

            // Template do email
            String assunto = "Parabéns! Você foi aprovado para a vaga: " + tituloFinal;
            String corpo = "Prezado candidato,\n\n" +
                    "É com grande satisfação que informamos que você foi aprovado no processo seletivo da " +
                    empresaFinal + " para a vaga de " + tituloFinal + "!\n\n" +
                    "Em breve nossa equipe entrará em contato com os próximos passos.\n\n" +
                    "Atenciosamente,\n" +
                    "Equipe de Recrutamento da " + empresaFinal;

            EmailSender emailSender = new EmailSender("vagas.coneccta@gmail.com", "wxka yvsw luer kfgv");

            // Validação adicional da lista de aprovados
            if (aprovados == null || aprovados.isEmpty()) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Nenhum candidato aprovado para notificar",
                            Toast.LENGTH_LONG).show();
                });
                return;
            }

            // Processamento dos emails
            for (Usuario candidato : aprovados) {
                try {
                    if (candidato == null || candidato.getEmail() == null) {
                        throw new Exception("Candidato inválido (email não disponível)");
                    }

                    if (!candidato.getEmail().contains("@")) {
                        throw new Exception("Email inválido");
                    }

                    emailSender.enviarEmail(candidato.getEmail(), assunto, corpo);
                    contadores[0]++; // Incrementa emailsEnviados

                    // Pequeno delay para evitar bloqueio do SMTP
                    Thread.sleep(500);
                } catch (Exception e) {
                    contadores[1]++; // Incrementa falhas
                    erros.append("• ").append(candidato != null ? candidato.getEmail() : "null")
                            .append(": ").append(e.getMessage()).append("\n");
                    Log.e("EMAIL_ERROR", "Erro ao enviar email", e);
                }
            }

            // Preparação do relatório final
            runOnUiThread(() -> {
                progressDialog.dismiss();

                String mensagemFinal = "Relatório de Envio:\n\n" +
                        "• Total de candidatos: " + aprovados.size() + "\n" +
                        "• Emails enviados: " + contadores[0] + "\n" +
                        "• Falhas: " + contadores[1];

                if (contadores[1] > 0) {
                    mensagemFinal += "\n\nErros encontrados:\n" + erros.toString();

                    new AlertDialog.Builder(CandidatosActivity.this)
                            .setTitle(contadores[1] == aprovados.size() ? "Falha no Envio" : "Envio Parcial")
                            .setMessage(mensagemFinal)
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    mensagemFinal += "\n\nTodos os emails foram enviados com sucesso!";

                    new AlertDialog.Builder(CandidatosActivity.this)
                            .setTitle("Sucesso")
                            .setMessage(mensagemFinal)
                            .setPositiveButton("OK", null)
                            .show();
                }
            });
        }).start();
    }

    // Método auxiliar para criar mensagem de resultado
    private String criarMensagemResultado(int total, int enviados, int falhas, String erros) {
        StringBuilder sb = new StringBuilder();
        sb.append("Relatório de Envio:\n\n")
                .append("• Total de candidatos: ").append(total).append("\n")
                .append("• Emails enviados: ").append(enviados).append("\n")
                .append("• Falhas: ").append(falhas);

        if (falhas > 0) {
            sb.append("\n\nErros encontrados:\n").append(erros);
        } else {
            sb.append("\n\nTodos os emails foram enviados com sucesso!");
        }

        return sb.toString();
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