package com.example.cardstackview;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPessoaJuridica extends AppCompatActivity {

    Button btnempresaentrar, btncadastrar, btnesquecisenha;

    TextInputEditText txtPessoaEmpresaEmail, txtPessoaEmpresaSenha;


    FirebaseAuth mAuth;
    ImageView imgLoginJbtnVoltar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_pessoa_j_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        btnempresaentrar = findViewById(R.id.btnPessoaEmpresaEntrar);
        btncadastrar = findViewById(R.id.btnPessoaEmpresaCriarConta);
        btnesquecisenha = findViewById(R.id.btnPessoaEmpresaEsqSenha);

        txtPessoaEmpresaEmail = findViewById(R.id.txtPessoaEmpresaEmail);
        txtPessoaEmpresaSenha = findViewById(R.id.txtPessoaEmpresaSenha);

        //Função de voltar

        imgLoginJbtnVoltar = findViewById(R.id.imgLoginJbtnVoltar);

        btnempresaentrar.setOnClickListener(view -> {
            String email = txtPessoaEmpresaEmail.getText().toString().trim();
            String password = txtPessoaEmpresaSenha.getText().toString().trim();

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
                                                    if (tipo != null && tipo.equals("Jurídica")) {
                                                        Toast.makeText(getApplicationContext(), "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(getApplicationContext(), TelaEmpresaActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Este login não é de Empresa.", Toast.LENGTH_SHORT).show();
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


        btncadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPessoaJuridica.this,CadPJuridicaActivity.class));
                finish();
            }
        });
    }
}