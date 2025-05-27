package com.example.cardstackview;

import android.app.ProgressDialog;
import android.content.Intent;
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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
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
                txtHabilidades.setText(formatarCampo("Habilidades", vaga.getHabilidades_desejaveis()));
            }
        }
    }

    private String formatarCampo(String rotulo, String valor) {
        return String.format("%s: %s", rotulo, valor != null ? valor : "Não informado");
    }

    private void publicarVaga() {
        if (vaga == null) {
            Toast.makeText(this, "Dados da vaga não encontrados", Toast.LENGTH_SHORT).show();
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
        params.put("habilidades_desejaveis", vaga.getHabilidades_desejaveis());
        params.put("ramo", vaga.getRamo());
        params.put("vinculo", vaga.getVinculo());
        params.put("id_empresa", String.valueOf(vaga.getEmpresa_id()));

        new PublicarVagaTask(progressDialog).execute(params);
    }

    private class PublicarVagaTask extends AsyncTask<HashMap<String, String>, Void, String> {
        private final ProgressDialog progressDialog;

        public PublicarVagaTask(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected String doInBackground(HashMap<String, String>... params) {
            // Implementação da conexão HTTP (mesma do código original)
            // ...
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            try {
                JSONObject jsonResponse = new JSONObject(result);
                if (!jsonResponse.getBoolean("error")) {
                    Toast.makeText(VagaPreVisualizacaoActivity.this,
                            "Vaga publicada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(VagaPreVisualizacaoActivity.this,
                            "Erro: " + jsonResponse.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(VagaPreVisualizacaoActivity.this,
                        "Erro ao processar resposta", Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", "Erro no JSON: " + result, e);
            }
        }
    }
}