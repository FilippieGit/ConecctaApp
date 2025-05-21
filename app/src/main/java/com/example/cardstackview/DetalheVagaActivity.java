package com.example.cardstackview;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetalheVagaActivity extends AppCompatActivity {

    private ImageView imageLogoDetalhe;
    private TextView textTituloDetalhe, textDescricaoDetalhe, textLocalizacaoDetalhe;
    private TextView textSalarioDetalhe, textRequisitosDetalhe;
    private TextView textNivelExperienciaDetalhe, textTipoContratoDetalhe, textAreaAtuacaoDetalhe;
    private ImageButton btnVoltarDetalhe;
    private FloatingActionButton btnExcluir;
    private Vagas vaga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_vaga_layout);

        inicializarComponentes();

        // Verifique se é uma empresa (pessoa jurídica)
        boolean isPessoaJuridica = getIntent().getBooleanExtra("isPessoaJuridica", false);
        vaga = (Vagas) getIntent().getSerializableExtra("vaga");

        // Mostrar botão apenas para empresas
        btnExcluir.setVisibility(isPessoaJuridica ? View.VISIBLE : View.GONE);
        btnExcluir.setOnClickListener(v -> mostrarDialogoConfirmacao());

        exibirDetalhesVaga();
        btnVoltarDetalhe.setOnClickListener(v -> finish());
    }

    private void mostrarDialogoConfirmacao() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir esta vaga permanentemente?")
                .setPositiveButton("Excluir", (dialog, which) -> excluirVaga())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirVaga() {
        if (vaga == null) return;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Excluindo vaga...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    URL url = new URL(Api.URL_EXCLUIR_VAGA);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    // Envie o parâmetro id_vaga
                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write("id_vaga=" + vaga.getVaga_id());
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
                        return !jsonResponse.getBoolean("error");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                progressDialog.dismiss();
                if (success) {
                    Toast.makeText(DetalheVagaActivity.this, "Vaga excluída com sucesso", Toast.LENGTH_SHORT).show();

                    // Retorne para a tela anterior com o resultado
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("vagaExcluida", vaga);
                    setResult(RESULT_FIRST_USER, resultIntent);
                    finish();
                } else {
                    Toast.makeText(DetalheVagaActivity.this, "Erro ao excluir vaga", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void inicializarComponentes() {
        imageLogoDetalhe = findViewById(R.id.imageLogoDetalhe);
        textTituloDetalhe = findViewById(R.id.textTituloDetalhe);
        textDescricaoDetalhe = findViewById(R.id.textDescricaoDetalhe);
        textLocalizacaoDetalhe = findViewById(R.id.textLocalizacaoDetalhe);
        textSalarioDetalhe = findViewById(R.id.textSalarioDetalhe);
        textRequisitosDetalhe = findViewById(R.id.textRequisitosDetalhe);
        textNivelExperienciaDetalhe = findViewById(R.id.textNivelExperienciaDetalhe);
        textTipoContratoDetalhe = findViewById(R.id.textTipoContratoDetalhe);
        textAreaAtuacaoDetalhe = findViewById(R.id.textAreaAtuacaoDetalhe);
        btnVoltarDetalhe = findViewById(R.id.btnVoltarDetalhe);
    }

    private void exibirDetalhesVaga() {
        Vagas vaga = (Vagas) getIntent().getSerializableExtra("vaga");
        if (vaga != null) {
            textTituloDetalhe.setText(vaga.getTitulo());
            textDescricaoDetalhe.setText(vaga.getDescricao());
            textLocalizacaoDetalhe.setText("Localização: " + vaga.getLocalizacao());
            textSalarioDetalhe.setText("Salário: " + vaga.getSalario());
            textRequisitosDetalhe.setText("Requisitos: " + vaga.getRequisitos());
            textNivelExperienciaDetalhe.setText(vaga.getNivel_experiencia());
            textTipoContratoDetalhe.setText(vaga.getTipo_contrato());
            textAreaAtuacaoDetalhe.setText(vaga.getArea_atuacao());
        } else {
            textTituloDetalhe.setText("Erro ao carregar os dados");
            textDescricaoDetalhe.setText("Tente novamente mais tarde.");
        }
    }
}
