package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;




public class CadPJuridicaActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cad_pjuridica_layout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText edtEmail = findViewById(R.id.edtLoginEmail);
        EditText edtname = findViewById(R.id.edtNomeEmpresa);
        EditText edtwebsite = findViewById(R.id.edtWebsite);
        EditText edtCNPJ = findViewById(R.id.edtcnpj);
        EditText edtSenha = findViewById(R.id.edtLoginSenha);
        Button btnCadastrar = findViewById(R.id.btnLoginEntrar);
        ImageView btnVoltar = findViewById(R.id.imgEsqSenhabtnVoltar);

        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(CadPJuridicaActivity.this, LoginPessoaJuridica.class);
            startActivity(intent);
            finish(); // Fecha a tela atual para não voltar ao cadastro ao pressionar voltar
        });

        btnCadastrar.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String senha = edtSenha.getText().toString().trim();
            String nome = edtname.getText().toString().trim();
            String cidade = edtwebsite.getText().toString().trim();
            String CNPJ = edtCNPJ.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty() || nome.isEmpty() || cidade.isEmpty() || CNPJ.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Consulta no Firestore para verificar se o CPF já existe
            db.collection("users")
                    .whereEqualTo("CNPJ", CNPJ)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // CPF já cadastrado
                                Toast.makeText(this, "CPF já cadastrado. Não é possível concluir o cadastro.", Toast.LENGTH_LONG).show();
                            } else {
                                // CPF livre, pode criar usuário
                                mAuth.createUserWithEmailAndPassword(email, senha)
                                        .addOnCompleteListener(authTask -> {
                                            if (authTask.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                if (user != null) {

                                                    Map<String, Object> userMap = new HashMap<>();
                                                    userMap.put("CNPJ", CNPJ);
                                                    userMap.put("email", email);
                                                    userMap.put("nome", nome);
                                                    userMap.put("cidade", cidade);
                                                    userMap.put("tipo", "Jurídica");

                                                    db.collection("users").document(user.getUid())
                                                            .set(userMap)
                                                            .addOnSuccessListener(unused -> {
                                                                user.sendEmailVerification()
                                                                        .addOnCompleteListener(verifyTask -> {
                                                                            if (verifyTask.isSuccessful()) {
                                                                                Toast.makeText(this, "Cadastro realizado! Verifique seu e-mail.", Toast.LENGTH_LONG).show();
                                                                                finish(); // Fecha a tela de cadastro
                                                                            } else {
                                                                                Toast.makeText(this, "Erro ao enviar verificação: " + verifyTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                            }
                                                                        });
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(this, "Erro ao salvar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                            });
                                                }
                                            } else {
                                                Toast.makeText(this, "Erro: " + authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(this, "Erro ao verificar CPF: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

// Esse pedaço para ajustar o layout se quiser também (igual no físico)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
}