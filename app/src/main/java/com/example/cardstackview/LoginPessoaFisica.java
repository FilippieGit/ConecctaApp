package com.example.cardstackview;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import android.content.Intent;
import android.os.Bundle;
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

        mAuth = FirebaseAuth.getInstance();

        btnPessoaLoginEntrar = findViewById(R.id.btnPessoaLoginEntrar);
        btnPessoaLoginEsqSenha = findViewById(R.id.btnPessoaLoginEsqSenha);
        btnPessoaLoginCriarConta = findViewById(R.id.btnPessoaLoginCriarConta);

        txtPessoaLoginEmail = findViewById(R.id.txtPessoaLoginEmail);
        txtPessoaLoginSenha = findViewById(R.id.txtPessoaLoginSenha);

        imgLoginPbtnVoltar = findViewById(R.id.imgLoginPbtnVoltar);

        imgLoginPbtnVoltar.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SelecaoActivity.class));
            finish();
        });

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
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("users").document(user.getUid())
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {
                                                    String tipo = documentSnapshot.getString("tipo");
                                                    if (tipo != null && tipo.equals("Física")) {
                                                        Toast.makeText(getApplicationContext(), "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Este login não é de Pessoa Física.", Toast.LENGTH_SHORT).show();
                                                        mAuth.signOut(); // Faz logout para evitar acesso indevido
                                                    }
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Dados do usuário não encontrados.", Toast.LENGTH_LONG).show();
                                                    mAuth.signOut();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getApplicationContext(), "Erro ao buscar dados do usuário: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                mAuth.signOut();
                                            });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Verifique seu e-mail antes de entrar.", Toast.LENGTH_LONG).show();
                                    mAuth.signOut();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Erro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });



        btnPessoaLoginEsqSenha.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RecSenhaActivity.class));
        });

        btnPessoaLoginCriarConta.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), CadastroActivity.class));
            finish();
        });
    }
}
