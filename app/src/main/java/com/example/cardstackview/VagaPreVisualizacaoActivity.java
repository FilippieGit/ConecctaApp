package com.example.cardstackview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Publicando vaga...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("titulo", vaga.getTitulo());
        params.put("descricao", vaga.getDescricao());
        params.put("localizacao", vaga.getLocalizacao());
        params.put("salario", vaga.getSalario());
        params.put("requisitos", vaga.getRequisitos());
        params.put("nivel_experiencia", vaga.getNivel_experiencia());
        params.put("tipo_contrato", vaga.getTipo_contrato());
        params.put("area_atuacao", vaga.getArea_atuacao());
        params.put("beneficios", vaga.getBeneficios());
        params.put("id_empresa", String.valueOf(vaga.getEmpresa_id()));

        new CadastrarVagaTask(progressDialog).execute(params);
    }

    private class CadastrarVagaTask extends AsyncTask<Map<String, String>, Void, Boolean> {
        private final ProgressDialog progressDialog;

        public CadastrarVagaTask(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected Boolean doInBackground(Map<String, String>... params) {
            Map<String, String> postData = params[0];
            try {
                URL url = new URL(Api.URL_CADASTRAR_VAGA);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

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
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    in.close();

                    String response = sb.toString();
                    JSONObject jsonResponse = new JSONObject(response);
                    return !jsonResponse.getBoolean("error");
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressDialog.dismiss();
            if (success) {
                Toast.makeText(VagaPreVisualizacaoActivity.this, "Vaga publicada com sucesso!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(VagaPreVisualizacaoActivity.this, TelaEmpresaActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(VagaPreVisualizacaoActivity.this, "Erro ao publicar vaga!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
