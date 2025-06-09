package com.example.cardstackview;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

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
    private ImageButton btnVoltarDetalhe, btnVerCandidatos;
    private FloatingActionButton btnExcluir;
    private Button btnCandidatar;
    private Vagas vaga;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_vaga_layout);

        mAuth = FirebaseAuth.getInstance();
        inicializarComponentes();

        // Obter o tipo do usuário das SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userType = prefs.getString("user_type", "Física");
        boolean isPessoaJuridica = userType.equalsIgnoreCase("Jurídica");

        vaga = (Vagas) getIntent().getSerializableExtra("vaga");
        if (vaga == null) {
            Toast.makeText(this, "Erro: Dados da vaga não encontrados", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar visibilidade dos botões baseado no tipo de usuário
        if (isPessoaJuridica) {
            // Usuário é empresa - mostrar botão de ver candidatos e ocultar botão de candidatura
            btnVerCandidatos.setVisibility(View.VISIBLE);
            btnExcluir.setVisibility(View.VISIBLE);
            btnCandidatar.setVisibility(View.GONE);
        } else {
            // Usuário é candidato - mostrar botão de candidatura e ocultar botões de empresa
            btnVerCandidatos.setVisibility(View.GONE);
            btnExcluir.setVisibility(View.GONE);
            btnCandidatar.setVisibility(View.VISIBLE);
            verificarCandidaturaExistente();
        }

        if (vaga != null) {
            Log.d("DetalheVagaActivity", "Vaga recebida: " + vaga.toString());
            Log.d("DetalheVagaActivity", "Habilidades desejáveis (String): " + vaga.getHabilidadesDesejaveisStr());
            Log.d("DetalheVagaActivity", "Habilidades desejáveis (List): " + vaga.getHabilidadesDesejaveis());
            Log.d("DetalheVagaActivity", "Descrição: " + vaga.getDescricao());
        } else {
            Log.d("DetalheVagaActivity", "Objeto vaga está null!");
        }

        btnExcluir.setOnClickListener(v -> mostrarDialogoConfirmacao());
        btnVoltarDetalhe.setOnClickListener(v -> finish());

        btnVerCandidatos.setOnClickListener(v -> {
            Intent intent = new Intent(DetalheVagaActivity.this, CandidatosActivity.class);
            intent.putExtra("vaga_id", vaga.getVaga_id());
            startActivity(intent);
        });

        btnCandidatar.setOnClickListener(v -> {
            if (vaga != null) {
                Intent intent = new Intent(DetalheVagaActivity.this, CandidatarSeActivity.class);
                intent.putExtra("vaga_id", String.valueOf(vaga.getVaga_id()));
                intent.putExtra("vaga_titulo", vaga.getTitulo());
                startActivity(intent);
            }
        });

        exibirDetalhesVaga();
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
        btnVoltarDetalhe = findViewById(R.id.btnVoltarDetalhe);
        btnExcluir = findViewById(R.id.BtnDetalheExcluir);
        btnVerCandidatos = findViewById(R.id.btnVerCandidatos);
        btnCandidatar = findViewById(R.id.btnCandidatar);
    }

    private void verificarCandidaturaExistente() {
        if (vaga == null || mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        String vagaId = String.valueOf(vaga.getVaga_id());

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    URL url = new URL(Api.URL_VERIFICAR_CANDIDATURA + "?user_id=" + userId + "&vaga_id=" + vagaId);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String response = in.readLine();
                        in.close();

                        JSONObject jsonResponse = new JSONObject(response);
                        return jsonResponse.getBoolean("ja_candidatado");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean jaCandidatado) {
                if (jaCandidatado) {
                    btnCandidatar.setEnabled(false);
                    btnCandidatar.setText("Já candidatado");
                }
            }
        }.execute();
    }

    private void candidatarAVaga() {
        if (vaga == null || mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        String vagaId = String.valueOf(vaga.getVaga_id()); // Conversão aqui

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando candidatura...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    URL url = new URL(Api.URL_CANDIDATAR_VAGA);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write("user_id=" + userId + "&vaga_id=" + vagaId);
                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String response = in.readLine();
                        in.close();

                        JSONObject jsonResponse = new JSONObject(response);
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
                    Toast.makeText(DetalheVagaActivity.this, "Candidatura realizada com sucesso!", Toast.LENGTH_SHORT).show();
                    btnCandidatar.setEnabled(false);
                    btnCandidatar.setText("Já candidatado");
                } else {
                    Toast.makeText(DetalheVagaActivity.this, "Erro ao realizar candidatura", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void irParaPerguntas() {
        if (vaga == null) return;

        Intent intent = new Intent(this, CandidatarSeActivity.class);
        intent.putExtra("vaga_id", vaga.getVaga_id());
        intent.putExtra("vaga_titulo", vaga.getTitulo());
        startActivity(intent);
    }

    private void exibirDetalhesVaga() {
        if (vaga != null) {
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

            chipGroupHabilidadesDetalhe.removeAllViews();
            List<String> habilidadesList = vaga.getHabilidadesDesejaveis();
            if (habilidadesList != null && !habilidadesList.isEmpty()) {
                for (String habilidade : habilidadesList) {
                    Chip chip = new Chip(this);
                    chip.setText(habilidade);
                    chip.setCheckable(false);
                    chipGroupHabilidadesDetalhe.addView(chip);
                }
            }
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