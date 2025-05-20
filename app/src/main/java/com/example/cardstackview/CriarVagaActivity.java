package com.example.cardstackview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CriarVagaActivity extends AppCompatActivity {

    private TextInputEditText edtTituloVaga, edtDescricaoVaga, edtLocalizacao, edtSalario, edtRequisitos;
    private Spinner spinnerNivelExperiencia, spinnerTipoContrato, spinnerAreaAtuacao;

    // Variáveis de instância para os dados do formulário
    private String titulo, descricao, localizacao, salario, requisitos, nivel, contrato, area, idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_vaga_layout);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        edtTituloVaga = findViewById(R.id.edtTituloVaga);
        edtDescricaoVaga = findViewById(R.id.edtDescricaoVaga);
        edtLocalizacao = findViewById(R.id.edtLocalizacao);
        edtSalario = findViewById(R.id.edtSalario);
        edtRequisitos = findViewById(R.id.edtRequisitos);

        spinnerNivelExperiencia = findViewById(R.id.spinnerNivelExperiencia);
        spinnerTipoContrato = findViewById(R.id.spinnerTipoContrato);
        spinnerAreaAtuacao = findViewById(R.id.spinnerAreaAtuacao);

        ArrayAdapter<CharSequence> nivelAdapter = ArrayAdapter.createFromResource(this,
                R.array.niveis_experiencia, android.R.layout.simple_spinner_item);
        nivelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivelExperiencia.setAdapter(nivelAdapter);

        ArrayAdapter<CharSequence> contratoAdapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_contrato, android.R.layout.simple_spinner_item);
        contratoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoContrato.setAdapter(contratoAdapter);

        ArrayAdapter<CharSequence> areaAdapter = ArrayAdapter.createFromResource(this,
                R.array.areas_atuacao, android.R.layout.simple_spinner_item);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAreaAtuacao.setAdapter(areaAdapter);

        MaterialButton btnCriarVaga = findViewById(R.id.btnCriarVaga);
        btnCriarVaga.setOnClickListener(v -> cadastrarVaga());

        findViewById(R.id.imgVoltar).setOnClickListener(v -> finish());
    }

    private void cadastrarVaga() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pegando os valores dos campos
        titulo = edtTituloVaga.getText().toString().trim();
        descricao = edtDescricaoVaga.getText().toString().trim();
        localizacao = edtLocalizacao.getText().toString().trim();
        salario = edtSalario.getText().toString().trim();
        requisitos = edtRequisitos.getText().toString().trim();
        nivel = spinnerNivelExperiencia.getSelectedItem().toString();
        contrato = spinnerTipoContrato.getSelectedItem().toString();
        area = spinnerAreaAtuacao.getSelectedItem().toString();

        // Validações dos campos
        if (titulo.isEmpty()) {
            edtTituloVaga.setError("Título da vaga é obrigatório");
            edtTituloVaga.requestFocus();
            return;
        }

        if (descricao.isEmpty()) {
            edtDescricaoVaga.setError("Descrição da vaga é obrigatória");
            edtDescricaoVaga.requestFocus();
            return;
        }

        if (localizacao.isEmpty()) {
            edtLocalizacao.setError("Localização é obrigatória");
            edtLocalizacao.requestFocus();
            return;
        }

        if (requisitos.isEmpty()) {
            edtRequisitos.setError("Requisitos são obrigatórios");
            edtRequisitos.requestFocus();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado! Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }
        idEmpresa = user.getUid();

        // Mostrar progresso (loading)
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cadastrando vaga...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Criar os parâmetros
        Map<String, String> params = new HashMap<>();
        params.put("titulo", titulo);
        params.put("descricao", descricao);
        params.put("localizacao", localizacao);
        params.put("salario", salario.isEmpty() ? "0" : salario);
        params.put("requisitos", requisitos);
        params.put("nivel_experiencia", nivel);
        params.put("tipo_contrato", contrato);
        params.put("area_atuacao", area);
        params.put("id_empresa", "1"); // <-- ALTERADO: valor inteiro existente no banco

        // Executar a tarefa assíncrona
        new CadastrarVagaTask(progressDialog).execute(params);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class CadastrarVagaTask extends AsyncTask<Map<String, String>, Void, String> {
        private final ProgressDialog progressDialog;

        public CadastrarVagaTask(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(Api.URL_CADASTRAR_VAGA);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setDoOutput(true);

                // Construir os parâmetros
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, String> param : params[0].entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(param.getKey());
                    postData.append('=');
                    postData.append(param.getValue());
                }
                byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

                // Enviar os dados
                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                outputStream.write(postDataBytes);
                outputStream.flush();
                outputStream.close();

                // Verificar status code
                int responseCode = urlConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return "{\"error\":true,\"message\":\"HTTP error code: " + responseCode + "\"}";
                }

                // Ler a resposta
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();

            } catch (Exception e) {
                return "{\"error\":true,\"message\":\"" + e.getMessage() + "\"}";
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            try {
                JSONObject jsonResponse = new JSONObject(result);
                if (!jsonResponse.getBoolean("error")) {
                    int idVaga = jsonResponse.optInt("id_vaga", 0);

                    // Crie o objeto Vagas usando as variáveis de instância
                    Vagas vaga = new Vagas(
                            idVaga,
                            titulo,
                            descricao,
                            localizacao,
                            salario,
                            requisitos,
                            nivel,
                            contrato,
                            area,
                            "", // benefícios
                            0,  // empresa_id (ajuste se for String na sua classe)
                            ""  // nome_empresa
                    );

                    Intent intent = new Intent(CriarVagaActivity.this, DetalheVagaActivity.class);
                    intent.putExtra("vaga", vaga);
                    intent.putExtra("isPessoaJuridica", true);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CriarVagaActivity.this,
                            "Erro: " + jsonResponse.getString("message"),
                            Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(CriarVagaActivity.this,
                        "Erro ao processar resposta: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
