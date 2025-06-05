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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CadPJuridicaActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cad_p_fisica_layout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtNome = findViewById(R.id.edtNome);
        EditText edtCidade = findViewById(R.id.edtCidade);
        EditText edtCPF = findViewById(R.id.edtCPF);
        EditText edtSenha = findViewById(R.id.edtSenha);
        Button btnCadastrar = findViewById(R.id.btnCadastrar);
        ImageView btnVoltar = findViewById(R.id.imgVoltarCadastro);

        btnVoltar.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnCadastrar.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String senha = edtSenha.getText().toString().trim();
            String nome = edtNome.getText().toString().trim();
            String cidade = edtCidade.getText().toString().trim();
            String CPF = edtCPF.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty() || nome.isEmpty() || cidade.isEmpty() || CPF.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (senha.length() < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifica se CPF já existe
            db.collection("users")
                    .whereEqualTo("CPF", CPF)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                Toast.makeText(this, "CPF já cadastrado", Toast.LENGTH_LONG).show();
                            } else {
                                criarUsuario(email, senha, nome, cidade, CPF, "Física");
                            }
                        } else {
                            Toast.makeText(this, "Erro ao verificar CPF: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void criarUsuario(String email, String senha, String nome, String cidade, String documento, String tipo) {
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("email", email);
                            userData.put("nome", nome);
                            userData.put("cidade", cidade);
                            userData.put("tipo", tipo);
                            userData.put("CPF", documento);

                            // **Campos adicionados para que o documento de pessoa física
                            // também tenha CNPJ e website (strings vazias) no Firestore**
                            userData.put("CNPJ", "");
                            userData.put("website", "");

                            // Manter os arrays que já existiam
                            userData.put("certificados", new ArrayList<String>());
                            userData.put("experiencias", new ArrayList<String>());
                            userData.put("formacoes", new ArrayList<String>());

                            userData.put("dataCadastro", System.currentTimeMillis());

                            db.collection("users").document(user.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(verifyTask -> {
                                                    if (verifyTask.isSuccessful()) {
                                                        Toast.makeText(this, "Cadastro realizado! Verifique seu e-mail.", Toast.LENGTH_LONG).show();
                                                        finish();
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
                        Toast.makeText(this, "Erro no cadastro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
