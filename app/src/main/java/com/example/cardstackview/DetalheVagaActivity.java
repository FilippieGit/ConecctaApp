package com.example.cardstackview;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DetalheVagaActivity extends AppCompatActivity {

    private TextView textBeneficiosDetalhe, textRamoDetalhe;
    private com.google.android.material.chip.ChipGroup chipGroupHabilidadesDetalhe;

    private ImageView imageLogoDetalhe;
    private TextView textTituloDetalhe, textDescricaoDetalhe, textLocalizacaoDetalhe;
    private TextView textSalarioDetalhe, textRequisitosDetalhe;
    private TextView textNivelExperienciaDetalhe, textTipoContratoDetalhe, textAreaAtuacaoDetalhe;
    private ImageButton btnVoltarDetalhe;
    private FloatingActionButton btnExcluir;
    private Vagas vaga;

    private ImageButton btnVerCandidatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_vaga_layout);

        inicializarComponentes();

        boolean isPessoaJuridica = getIntent().getBooleanExtra("isPessoaJuridica", false);
        vaga = (Vagas) getIntent().getSerializableExtra("vaga");

        // Configurar botão de candidatos
        if (isPessoaJuridica && vaga != null) {
            btnVerCandidatos.setVisibility(View.VISIBLE);
            btnVerCandidatos.setOnClickListener(v -> {
                Intent intent = new Intent(DetalheVagaActivity.this, CandidatosActivity.class);
                intent.putExtra("vaga_id", vaga.getVaga_id());
                startActivity(intent);
            });
        }

        // Log para verificar o objeto recebido e seus campos
        if (vaga != null) {
            Log.d("DetalheVagaActivity", "Vaga recebida: " + vaga.toString());
            Log.d("DetalheVagaActivity", "Habilidades desejáveis (String): " + vaga.getHabilidadesDesejaveisStr());
            Log.d("DetalheVagaActivity", "Habilidades desejáveis (List): " + vaga.getHabilidadesDesejaveis());
            Log.d("DetalheVagaActivity", "Descrição: " + vaga.getDescricao());
        } else {
            Log.d("DetalheVagaActivity", "Objeto vaga está null!");
        }

        if (btnExcluir != null) {
            btnExcluir.setVisibility(isPessoaJuridica ? View.VISIBLE : View.GONE);
            btnExcluir.setOnClickListener(v -> mostrarDialogoConfirmacao());
        }

        exibirDetalhesVaga();

        btnVoltarDetalhe.setOnClickListener(v -> finish());
    }


    private void inicializarComponentes() {
        textBeneficiosDetalhe = findViewById(R.id.textBeneficiosDetalhe);
        textRamoDetalhe = findViewById(R.id.textRamoDetalhe);
        chipGroupHabilidadesDetalhe = findViewById(R.id.chipGroupHabilidadesDetalhe);
        imageLogoDetalhe = findViewById(R.id.imageLogoDetalhe);
        textTituloDetalhe = findViewById(R.id.textTituloDetalhe);
        textDescricaoDetalhe = findViewById(R.id.textDescricaoDetalhe);
        textLocalizacaoDetalhe = findViewById(R.id.textLocalizacaoDetalhe);
        textSalarioDetalhe = findViewById(R.id.textSalarioDetalhe);
        textRequisitosDetalhe = findViewById(R.id.textRequisitosDetalhe);
        textNivelExperienciaDetalhe = findViewById(R.id.textNivelExperienciaDetalhe);
        textTipoContratoDetalhe = findViewById(R.id.textTipoContratoDetalhe);
        textAreaAtuacaoDetalhe = findViewById(R.id.textAreaAtuacaoDetalhe);
        textBeneficiosDetalhe = findViewById(R.id.textBeneficiosDetalhe);
        textRamoDetalhe = findViewById(R.id.textRamoDetalhe);
        chipGroupHabilidadesDetalhe = findViewById(R.id.chipGroupHabilidadesDetalhe);
        btnVoltarDetalhe = findViewById(R.id.btnVoltarDetalhe);
        btnExcluir = findViewById(R.id.BtnDetalheExcluir);
        btnVerCandidatos = findViewById(R.id.btnVerCandidatos);
    }


    private void exibirDetalhesVaga() {
        if (vaga != null) {
            textTituloDetalhe.setText(vaga.getTitulo());
            textDescricaoDetalhe.setText(vaga.getDescricao());
            textTituloDetalhe.setText(vaga.getTitulo());
            textDescricaoDetalhe.setText(vaga.getDescricao());
            textLocalizacaoDetalhe.setText("Localização: " + vaga.getLocalizacao());
            textSalarioDetalhe.setText("Salário: " + vaga.getSalario());
            textRequisitosDetalhe.setText("Requisitos: " + vaga.getRequisitos());
            textNivelExperienciaDetalhe.setText(vaga.getNivel_experiencia());
            textTipoContratoDetalhe.setText(vaga.getTipo_contrato());
            textAreaAtuacaoDetalhe.setText(vaga.getArea_atuacao());
            if (textBeneficiosDetalhe != null) textBeneficiosDetalhe.setText(vaga.getBeneficios());
            if (textRamoDetalhe != null) textRamoDetalhe.setText(vaga.getRamo());

            // Habilidades desejáveis (chips)
            chipGroupHabilidadesDetalhe.removeAllViews();


            Log.d("DetalheVagaActivity", "Habilidades (String): " + vaga.getHabilidadesDesejaveisStr());
            Log.d("DetalheVagaActivity", "Habilidades (Lista): " + vaga.getHabilidadesDesejaveis());


            List<String> habilidadesList = vaga.getHabilidadesDesejaveis();
            if (habilidadesList != null && !habilidadesList.isEmpty()) {
                for (String habilidade : habilidadesList) {
                    Chip chip = new Chip(this);
                    chip.setText(habilidade);
                    chip.setCheckable(false);
                    chipGroupHabilidadesDetalhe.addView(chip);
                }
            } else {
                Log.e("DetalheVagaActivity", "Lista de habilidades vazia ou nula!");
            }
        } else {
            textTituloDetalhe.setText("Erro ao carregar os dados");
            textDescricaoDetalhe.setText("Tente novamente mais tarde.");
        }
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
}
