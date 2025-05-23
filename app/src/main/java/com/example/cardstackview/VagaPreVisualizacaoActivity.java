package com.example.cardstackview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VagaPreVisualizacaoActivity extends AppCompatActivity {

    private TextView preVisualizacaoTextTitulo, preVisualizacaoTextDescricao;
    private TextView preVisualizacaoTextLocalizacao, preVisualizacaoTextSalario;
    private TextView preVisualizacaoTextRequisitos, preVisualizacaoTextNivelExperiencia;
    private TextView preVisualizacaoTextTipoContrato, preVisualizacaoTextAreaAtuacao;
    private TextView preVisualizacaoTextBeneficios;
    private MaterialButton preVisualizacaoBtnPublicar;
    private ImageButton preVisualizacaoBtnEditar;
    private Vagas vaga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vaga_pre_visualizacao_layout);

        vincularComponentes();
        configurarListeners();
        exibirDadosVaga();
    }

    private void vincularComponentes() {
        preVisualizacaoTextTitulo = findViewById(R.id.preVisualizacaoTextTitulo);
        preVisualizacaoTextDescricao = findViewById(R.id.preVisualizacaoTextDescricao);
        preVisualizacaoTextLocalizacao = findViewById(R.id.preVisualizacaoTextLocalizacao);
        preVisualizacaoTextSalario = findViewById(R.id.preVisualizacaoTextSalario);
        preVisualizacaoTextRequisitos = findViewById(R.id.preVisualizacaoTextRequisitos);
        preVisualizacaoTextNivelExperiencia = findViewById(R.id.preVisualizacaoTextNivelExperiencia);
        preVisualizacaoTextTipoContrato = findViewById(R.id.preVisualizacaoTextTipoContrato);
        preVisualizacaoTextAreaAtuacao = findViewById(R.id.preVisualizacaoTextAreaAtuacao);
        preVisualizacaoTextBeneficios = findViewById(R.id.preVisualizacaoTextBeneficios);

        preVisualizacaoBtnPublicar = findViewById(R.id.btnPreVisualizacaoPublicar);
        preVisualizacaoBtnEditar = findViewById(R.id.btnPreVisualizacaoBEditar);
    }

    private void configurarListeners() {
        preVisualizacaoBtnPublicar.setOnClickListener(v -> publicarVaga());
        preVisualizacaoBtnEditar.setOnClickListener(v -> finish());
    }

    private void exibirDadosVaga() {
        if (getIntent() != null && getIntent().hasExtra(Constants.EXTRA_VAGA)) {
            vaga = (Vagas) getIntent().getSerializableExtra(Constants.EXTRA_VAGA);

            preVisualizacaoTextTitulo.setText(vaga.getTitulo());
            preVisualizacaoTextDescricao.setText(vaga.getDescricao());
            preVisualizacaoTextLocalizacao.setText("Local: " + vaga.getLocalizacao());
            preVisualizacaoTextSalario.setText("Salário: " + vaga.getSalario());
            preVisualizacaoTextRequisitos.setText("Requisitos: " + vaga.getRequisitos());
            preVisualizacaoTextNivelExperiencia.setText("Nível: " + vaga.getNivel_experiencia());
            preVisualizacaoTextTipoContrato.setText("Contrato: " + vaga.getTipo_contrato());
            preVisualizacaoTextAreaAtuacao.setText("Área: " + vaga.getArea_atuacao());
            preVisualizacaoTextBeneficios.setText("Benefícios: " + vaga.getBeneficios());
        }
    }

    private void publicarVaga() {
        // Verifica se a vaga existe
        if (vaga == null) {
            Toast.makeText(this, "Dados da vaga não encontrados", Toast.LENGTH_SHORT).show();
            return;
        }

        // Valida campos obrigatórios
        if (vaga.getTitulo() == null || vaga.getTitulo().isEmpty()) {
            Toast.makeText(this, "Título da vaga é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Publicando vaga...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("titulo", vaga.getTitulo());
        params.put("descricao", vaga.getDescricao() != null ? vaga.getDescricao() : "");
        params.put("localizacao", vaga.getLocalizacao() != null ? vaga.getLocalizacao() : "");
        params.put("salario", vaga.getSalario() != null ? vaga.getSalario() : "");
        params.put("requisitos", vaga.getRequisitos() != null ? vaga.getRequisitos() : "");
        params.put("nivel_experiencia", vaga.getNivel_experiencia() != null ? vaga.getNivel_experiencia() : "");
        params.put("tipo_contrato", vaga.getTipo_contrato() != null ? vaga.getTipo_contrato() : "");
        params.put("area_atuacao", vaga.getArea_atuacao() != null ? vaga.getArea_atuacao() : "");
        params.put("beneficios", vaga.getBeneficios() != null ? vaga.getBeneficios() : "");
        params.put("id_empresa", String.valueOf(vaga.getEmpresa_id()));

        new CadastrarVagaTask(progressDialog).execute(params);
    }

    private class CadastrarVagaTask extends AsyncTask<Map<String, String>, Void, String> {
        private final ProgressDialog progressDialog;

        public CadastrarVagaTask(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> postData = params[0];
            try {
                URL url = new URL(Api.URL_CADASTRAR_VAGA);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Adiciona logs para depuração
                Log.d("CadastroVaga", "URL: " + Api.URL_CADASTRAR_VAGA);
                Log.d("CadastroVaga", "Dados: " + postData.toString());

                Uri.Builder builder = new Uri.Builder();
                for (Map.Entry<String, String> entry : postData.entrySet()) {
                    builder.appendQueryParameter(entry.getKey(), entry.getValue());
                }
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("CadastroVaga", "Código de resposta: " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();

                String response = sb.toString();
                Log.d("CadastroVaga", "Resposta: " + response);

                return response;

            } catch (Exception e) {
                Log.e("CadastroVaga", "Erro: " + e.getMessage(), e);
                return "error:" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            if (result.startsWith("error:")) {
                Toast.makeText(VagaPreVisualizacaoActivity.this,
                        "Erro: " + result.substring(6), Toast.LENGTH_LONG).show();
                return;
            }

            try {
                JSONObject jsonResponse = new JSONObject(result);
                if (!jsonResponse.getBoolean("error")) {
                    Toast.makeText(VagaPreVisualizacaoActivity.this,
                            "Vaga publicada com sucesso!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(VagaPreVisualizacaoActivity.this, TelaEmpresaActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String message = jsonResponse.optString("message", "Erro desconhecido ao publicar vaga");
                    Toast.makeText(VagaPreVisualizacaoActivity.this,
                            message, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(VagaPreVisualizacaoActivity.this,
                        "Erro ao processar resposta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("CadastroVaga", "Erro no JSON: " + e.getMessage(), e);
            }
        }
    }
}
