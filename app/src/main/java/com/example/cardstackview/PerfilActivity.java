package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PerfilActivity extends AppCompatActivity {

    // Componentes da interface
    private Button btnPEditarPerfil, btnPCompartilharPerfil;
    private ImageView imageViewPerfil;
    private TextView perfilNome, perfilEmail, perfilTelefone, perfilDescricao, perfilSetor;
    private TextView perfilExpProfissional, perfilFormAcademica, perfilCertificados;
    private TextView username, generouser, idadeuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_pessoa_layout);

        configurarLottie();
        initializeViews();

        // ⚠️ Adicione esta linha para configurar a seta:
        configurarSetaVoltarParaEmpresa();

        btnPCompartilharPerfil.setOnClickListener(v -> compartilharPerfil());

        boolean isProprioPerfil = !getIntent().hasExtra("nome");

        if (isProprioPerfil) {
            carregarDadosUsuario();
            btnPEditarPerfil.setOnClickListener(v ->
                    startActivity(new Intent(PerfilActivity.this, EdicaoPerfilPessoaActivity.class))
            );
        } else {
            loadCandidateData();
        }

        configurarBotoes(isProprioPerfil);
    }


    private void configurarLottie() {
        try {
            // Configura todas as animações Lottie
            LottieAnimationView lottieTwitter = findViewById(R.id.lottieTwitter);
            LottieAnimationView lottieLinkedin = findViewById(R.id.lottieLinkedin);
            LottieAnimationView lottieWhatsapp = findViewById(R.id.lottieWhatsapp);

            if (lottieTwitter != null) {
                lottieTwitter.enableMergePathsForKitKatAndAbove(true);
            }
            if (lottieLinkedin != null) {
                lottieLinkedin.enableMergePathsForKitKatAndAbove(true);
            }
            if (lottieWhatsapp != null) {
                lottieWhatsapp.enableMergePathsForKitKatAndAbove(true);
            }
        } catch (Exception e) {
            Log.e("PerfilActivity", "Erro ao configurar Lottie", e);
        }
    }

    private void configurarSetaVoltarParaEmpresa() {
        ImageView btnVoltar = findViewById(R.id.imgPerfilPessoabtnVoltar);
        if (btnVoltar == null) return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tipo = documentSnapshot.getString("tipo");
                        if ("Jurídica".equalsIgnoreCase(tipo)) {
                            btnVoltar.setVisibility(View.VISIBLE);
                            btnVoltar.setOnClickListener(v -> finish());
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("PerfilActivity", "Erro ao buscar tipo de usuário", e));
    }


    private void initializeViews() {
        try {
            btnPEditarPerfil = findViewById(R.id.btnPEditarPerfil);
            btnPCompartilharPerfil = findViewById(R.id.btnPCompartilharPerfil);
            imageViewPerfil = findViewById(R.id.imgPerfilCandidato);

            perfilNome = findViewById(R.id.perfilNome);
            perfilEmail = findViewById(R.id.perfilEmail);
            perfilTelefone = findViewById(R.id.perfilTelefone);
            perfilDescricao = findViewById(R.id.perfilDescricao);
            perfilSetor = findViewById(R.id.perfilSetor);
            perfilExpProfissional = findViewById(R.id.perfilExpProfissional);
            perfilFormAcademica = findViewById(R.id.perfilFormAcademica);
            perfilCertificados = findViewById(R.id.perfilCertificados);
            username = findViewById(R.id.username);
            generouser = findViewById(R.id.generouser);
            idadeuser = findViewById(R.id.idadeuser);
        } catch (Exception e) {
            Log.e("PerfilActivity", "Erro ao inicializar views", e);
            Toast.makeText(this, "Erro ao carregar interface", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCandidateData() {
        try {
            Intent intent = getIntent();
            setTextSafe(perfilNome, intent.getStringExtra("nome"));
            setTextSafe(perfilEmail, intent.getStringExtra("email"));
            setTextSafe(perfilSetor, intent.getStringExtra("setor"));
            setTextSafe(perfilTelefone, intent.getStringExtra("telefone"));
            setTextSafe(perfilDescricao, intent.getStringExtra("descricao"));
            setTextSafe(perfilExpProfissional, intent.getStringExtra("experiencia"));
            setTextSafe(perfilFormAcademica, intent.getStringExtra("formacao"));
            setTextSafe(perfilCertificados, intent.getStringExtra("certificados"));
            setTextSafe(username, intent.getStringExtra("username"));
            setTextSafe(generouser, intent.getStringExtra("genero"));
            setTextSafe(idadeuser, intent.getStringExtra("idade"));
        } catch (Exception e) {
            Log.e("PerfilActivity", "Erro ao carregar dados do candidato", e);
            Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTextSafe(TextView textView, String text) {
        if (textView != null) {
            textView.setText(text != null && !text.isEmpty() ? text : "Não informado");
        }
    }

    private void configurarBotoes(boolean isProprioPerfil) {
        if (btnPEditarPerfil == null || btnPCompartilharPerfil == null) {
            Log.e("PerfilActivity", "Botões não inicializados");
            return;
        }

        try {
            LinearLayout.LayoutParams paramsCompartilhar =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            isProprioPerfil ? 0.5f : 1f);

            btnPCompartilharPerfil.setLayoutParams(paramsCompartilhar);

            if (isProprioPerfil) {
                btnPEditarPerfil.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams paramsEditar =
                        new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                0.5f);
                btnPEditarPerfil.setLayoutParams(paramsEditar);
            } else {
                btnPEditarPerfil.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("PerfilActivity", "Erro ao configurar botões", e);
        }
    }

    private void compartilharPerfil() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            String shareMessage = "Confira o perfil de " + perfilNome.getText().toString() +
                    " no Coneccta: " + "\n\n" +
                    "Nome: " + perfilNome.getText().toString() + "\n" +
                    "Setor: " + perfilSetor.getText().toString() + "\n" +
                    "Experiência: " + perfilExpProfissional.getText().toString();

            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Compartilhar perfil via"));
        } catch (Exception e) {
            Log.e("PerfilActivity", "Erro ao compartilhar perfil", e);
            Toast.makeText(this, "Erro ao compartilhar", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarDadosUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = user.getUid();
        String url = Api.URL_GET_USER + userId;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(PerfilActivity.this,
                                "Erro na conexão: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(PerfilActivity.this,
                                    "Falha ao buscar dados do usuário",
                                    Toast.LENGTH_LONG).show());
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    JSONObject json = new JSONObject(jsonData);

                    runOnUiThread(() -> {
                        try {
                            setTextSafe(perfilNome, json.optString("nome"));
                            setTextSafe(perfilEmail, json.optString("email"));
                            setTextSafe(perfilTelefone, json.optString("telefone"));
                            setTextSafe(perfilDescricao, json.optString("descricao"));
                            setTextSafe(perfilSetor, json.optString("setor"));
                            setTextSafe(perfilExpProfissional, json.optString("experiencia_profissional"));
                            setTextSafe(perfilFormAcademica, json.optString("formacao_academica"));
                            setTextSafe(perfilCertificados, json.optString("certificados"));
                            setTextSafe(username, json.optString("username"));
                            setTextSafe(generouser, json.optString("genero"));
                            setTextSafe(idadeuser, json.optString("idade"));
                        } catch (Exception e) {
                            Log.e("PerfilActivity", "Erro ao processar resposta", e);
                        }
                    });
                } catch (JSONException | IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(PerfilActivity.this,
                                    "Erro ao processar dados: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}