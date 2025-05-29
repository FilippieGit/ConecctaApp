package com.example.cardstackview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

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
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VagaPreVisualizacaoActivity extends AppCompatActivity {

    private TextView txtTitulo, txtDescricao, txtLocalizacao, txtSalario;
    private TextView txtRequisitos, txtNivelExperiencia, txtTipoContrato;
    private TextView txtAreaAtuacao, txtBeneficios, txtHabilidades;
    private MaterialButton btnPublicar;
    private ImageButton btnEditar;
    private Vagas vaga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vaga_pre_visualizacao_layout);

        vincularComponentes();
        configurarListeners();
        carregarDadosVaga();
    }

    private void vincularComponentes() {
        txtTitulo = findViewById(R.id.preVisualizacaoTextTitulo);
        txtDescricao = findViewById(R.id.preVisualizacaoTextDescricao);
        txtLocalizacao = findViewById(R.id.preVisualizacaoTextLocalizacao);
        txtSalario = findViewById(R.id.preVisualizacaoTextSalario);
        txtRequisitos = findViewById(R.id.preVisualizacaoTextRequisitos);
        txtNivelExperiencia = findViewById(R.id.preVisualizacaoTextNivelExperiencia);
        txtTipoContrato = findViewById(R.id.preVisualizacaoTextTipoContrato);
        txtAreaAtuacao = findViewById(R.id.preVisualizacaoTextAreaAtuacao);
        txtBeneficios = findViewById(R.id.preVisualizacaoTextBeneficios);
        txtHabilidades = findViewById(R.id.preVisualizacaoTextHabilidades);

        btnPublicar = findViewById(R.id.btnPreVisualizacaoPublicar);
        btnEditar = findViewById(R.id.btnPreVisualizacaoBEditar);
    }

    private void configurarListeners() {
        btnPublicar.setOnClickListener(v -> publicarVaga());
        btnEditar.setOnClickListener(v -> finish()); // Volta para editar
    }

    private void carregarDadosVaga() {
        if (getIntent() != null && getIntent().hasExtra(Constants.EXTRA_VAGA)) {
            vaga = (Vagas) getIntent().getSerializableExtra(Constants.EXTRA_VAGA);

            if (vaga != null) {
                txtTitulo.setText(vaga.getTitulo());
                txtDescricao.setText(vaga.getDescricao());
                txtLocalizacao.setText(formatarCampo("Localização", vaga.getLocalizacao()));
                txtSalario.setText(formatarCampo("Salário", vaga.getSalario()));
                txtRequisitos.setText(formatarCampo("Requisitos", vaga.getRequisitos()));
                txtNivelExperiencia.setText(formatarCampo("Nível", vaga.getNivel_experiencia()));
                txtTipoContrato.setText(formatarCampo("Contrato", vaga.getTipo_contrato()));
                txtAreaAtuacao.setText(formatarCampo("Área", vaga.getArea_atuacao()));
                txtBeneficios.setText(formatarCampo("Benefícios", vaga.getBeneficios()));

                List<String> habilidadesList = vaga.getHabilidadesDesejaveis();
                String habilidadesFormatadas = habilidadesList.isEmpty() ? "Não informado" : String.join(", ", habilidadesList);
                txtHabilidades.setText(formatarCampo("Habilidades", habilidadesFormatadas));
            }
        }
    }


    private String formatarCampo(String rotulo, String valor) {
        return String.format("%s: %s", rotulo, valor != null ? valor : "Não informado");
    }

    private void publicarVaga() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_LONG).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Publicando vaga...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("titulo", vaga.getTitulo());
        params.put("localizacao", vaga.getLocalizacao());
        params.put("descricao", vaga.getDescricao());
        params.put("requisitos", vaga.getRequisitos());
        params.put("salario", vaga.getSalario());
        params.put("tipo_contrato", vaga.getTipo_contrato());
        params.put("area_atuacao", vaga.getArea_atuacao());
        params.put("beneficios", vaga.getBeneficios());
        params.put("nivel_experiencia", vaga.getNivel_experiencia());
        params.put("habilidades_desejaveis", vaga.getHabilidadesDesejaveisStr() != null ? vaga.getHabilidadesDesejaveisStr() : "");

        params.put("ramo", vaga.getRamo());
        params.put("vinculo", vaga.getVinculo());
        params.put("id_empresa", String.valueOf(vaga.getEmpresa_id()));

        new PublicarVagaTask(progressDialog).execute(params);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class PublicarVagaTask extends AsyncTask<HashMap<String, String>, Void, String> {
        private final ProgressDialog progressDialog;

        public PublicarVagaTask(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected String doInBackground(HashMap<String, String>... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String response = null;

            try {
                URL url = new URL(Api.URL_CADASTRAR_VAGA);
                connection = (HttpURLConnection) url.openConnection();

                // Configuração da conexão
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setConnectTimeout(15000); // 15 segundos
                connection.setReadTimeout(15000);    // 15 segundos
                connection.setDoOutput(true);

                // Verificar se a conexão foi criada corretamente
                if (connection == null) {
                    return "{\"error\":true,\"message\":\"Falha ao criar conexão\"}";
                }

                // Construir parâmetros POST
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, String> param : params[0].entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(param.getValue(), "UTF-8"));
                }

                // Enviar dados
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData.toString());
                writer.flush();
                writer.close();
                os.close();

                // Obter resposta
                int responseCode = connection.getResponseCode();
                InputStream inputStream;

                if (responseCode >= 200 && responseCode < 300) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                }

                // Ler resposta
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                response = responseBuilder.toString();

            } catch (MalformedURLException e) {
                Log.e("API_ERROR", "URL mal formada", e);
                response = "{\"error\":true,\"message\":\"URL do servidor inválida\"}";
            } catch (SocketTimeoutException e) {
                Log.e("API_ERROR", "Tempo de conexão esgotado", e);
                response = "{\"error\":true,\"message\":\"Tempo de conexão esgotado\"}";
            } catch (IOException e) {
                Log.e("API_ERROR", "Erro de IO", e);
                response = "{\"error\":true,\"message\":\"Erro de comunicação com o servidor\"}";
            } catch (Exception e) {
                Log.e("API_ERROR", "Erro inesperado", e);
                response = "{\"error\":true,\"message\":\"Erro inesperado: " + e.getMessage() + "\"}";
            } finally {
                // Fechar conexões
                if (reader != null) {
                    try { reader.close(); } catch (IOException e) { /* ignorar */ }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            try {
                if (result == null || result.isEmpty()) {
                    Toast.makeText(VagaPreVisualizacaoActivity.this,
                            "Erro: Nenhuma resposta do servidor", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject jsonResponse = new JSONObject(result);
                if (!jsonResponse.getBoolean("error")) {
                    Toast.makeText(VagaPreVisualizacaoActivity.this,
                            "Vaga publicada com sucesso!", Toast.LENGTH_SHORT).show();

                    // Navega para TelaEmpresaActivity
                    Intent intent = new Intent(VagaPreVisualizacaoActivity.this, TelaEmpresaActivity.class);
                    // Se quiser passar flag para atualizar vagas, descomente a linha abaixo:
                    // intent.putExtra("refresh_vagas", true);
                    VagaPreVisualizacaoActivity.this.startActivity(intent);

                    // Finaliza a activity atual para não voltar para ela
                    VagaPreVisualizacaoActivity.this.finish();

                } else {
                    String errorMsg = jsonResponse.optString("message", "Erro desconhecido");
                    Toast.makeText(VagaPreVisualizacaoActivity.this,
                            "Erro: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(VagaPreVisualizacaoActivity.this,
                        "Erro ao processar resposta do servidor", Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", "Erro no JSON: " + result, e);
            } catch (Exception e) {
                Toast.makeText(VagaPreVisualizacaoActivity.this,
                        "Erro inesperado", Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", "Erro geral: ", e);
            }
        }

    }
}