package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import com.bumptech.glide.Glide; // Para carregar a imagem de perfil
import com.google.firebase.auth.FirebaseAuth;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PerfilActivity extends AppCompatActivity {

    // Componentes da interface
    ImageView imgPerfilPbtnVoltar;
    Button btnPEditarPerfil;
    TextView perfilNome, perfilidade,perfilgenero , perfilEmail, perfilusername, perfilTelefone, perfilDescricao, perfilSetor, perfilExpProfissional, perfilFormAcademica, perfilCertificados;
    ImageView imageViewPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.perfil_pessoa_layout);

        // Referências para os elementos da UI
        imgPerfilPbtnVoltar = findViewById(R.id.imgPerfilPbtnVoltar);
        btnPEditarPerfil = findViewById(R.id.btnPEditarPerfil);
        perfilNome = findViewById(R.id.perfilNome);
        perfilEmail = findViewById(R.id.perfilEmail);
        perfilTelefone = findViewById(R.id.perfilTelefone);
        perfilDescricao = findViewById(R.id.perfilDescricao);
        perfilSetor = findViewById(R.id.perfilSetor);
        perfilExpProfissional = findViewById(R.id.perfilExpProfissional);
        perfilFormAcademica = findViewById(R.id.perfilFormAcademica);
        perfilCertificados = findViewById(R.id.perfilCertificados);
        perfilusername = findViewById(R.id.username);
        perfilgenero = findViewById(R.id.generouser);
        perfilidade = findViewById(R.id.idadeuser);
        imageViewPerfil = findViewById(R.id.imgPerfilCandidato); // A imagem de perfil

        // Função de voltar para a tela principal
        imgPerfilPbtnVoltar.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish(); // Apenas volta para a tela anterior
        });

        // Função de editar perfil
        btnPEditarPerfil.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), EdicaoPerfilPessoaActivity.class));
            finish();
        });

        // Carregar os dados do usuário após o login
        carregarDadosUsuario();
    }

    // Função para carregar os dados do usuário após o login
    private void carregarDadosUsuario() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Pegando o user_id do Firebase
        String url = "http://10.67.96.144/CRUD_user/getUser.php?id=" + userId;  // Alteração aqui para usar o parâmetro 'id'

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Erro na conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Falha ao buscar dados do usuário", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                String jsonData = response.body().string();

                runOnUiThread(() -> {
                    try {
                        JSONObject json = new JSONObject(jsonData);

                        // Preenche os campos com os dados do usuário
                        String nome = json.optString("nome", "");
                        String email = json.optString("email", "");
                        String telefone = json.optString("telefone", "");
                        String descricao = json.optString("descricao", "");
                        String setor = json.optString("setor", "");
                        String experiencia = json.optString("experiencia_profissional", "");
                        String formacao = json.optString("formacao_academica", "");
                        String certificados = json.optString("certificados", "");
                        String username = json.optString("nome", "");
                        String genero = json.optString("genero", "");
                        String idade = json.optString("idade", "");
                        String imagemPerfil = json.optString("imagem_perfil", "");

                        // Atualizando os campos de UI com os dados
                        perfilNome.setText(nome);
                        perfilEmail.setText(email);
                        perfilTelefone.setText(telefone);
                        perfilDescricao.setText(descricao);
                        perfilSetor.setText(setor);
                        perfilExpProfissional.setText(experiencia);
                        perfilFormAcademica.setText(formacao);
                        perfilCertificados.setText(certificados);
                        perfilusername.setText(username);
                        perfilgenero.setText(genero);
                        perfilidade.setText(idade);

                        // Se a URL da imagem de perfil não estiver vazia, carregue a imagem


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Erro ao processar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}
