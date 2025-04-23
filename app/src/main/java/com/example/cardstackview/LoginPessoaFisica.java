package com.example.cardstackview;

import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class LoginPessoaFisica extends AppCompatActivity {

    Button btnPessoaLoginEntrar, btnPessoaLoginCriarConta, btnPessoaLoginEsqSenha;
    TextInputEditText txtPessoaLoginEmail, txtPessoaLoginSenha;
    ImageView imgLoginPbtnVoltar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_pessoa_fisica_layoyt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inicializando os elementos da interface
        btnPessoaLoginEntrar = findViewById(R.id.btnPessoaLoginEntrar);
        btnPessoaLoginEsqSenha = findViewById(R.id.btnPessoaLoginEsqSenha);
        btnPessoaLoginCriarConta = findViewById(R.id.btnPessoaLoginCriarConta);

        txtPessoaLoginEmail = findViewById(R.id.txtPessoaLoginEmail);
        txtPessoaLoginSenha = findViewById(R.id.txtPessoaLoginSenha);

        imgLoginPbtnVoltar = findViewById(R.id.imgLoginPbtnVoltar);

        // Botão voltar
        imgLoginPbtnVoltar.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SelecaoActivity.class));
            finish();
        });

        // Botão entrar
        btnPessoaLoginEntrar.setOnClickListener(view -> {
            String email = txtPessoaLoginEmail.getText().toString().trim();
            String password = txtPessoaLoginSenha.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Erro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Botão esqueci a senha
        btnPessoaLoginEsqSenha.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RecSenhaActivity.class));
        });

        // Botão criar conta
        btnPessoaLoginCriarConta.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), CadastroActivity.class));
            finish();
        });
    }
}
