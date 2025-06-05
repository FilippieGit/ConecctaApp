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
        // Ajuste: carregar o layout de pessoa jurídica, não o de pessoa física
        setContentView(R.layout.cad_pjuridica_layout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ajuste nos IDs para campos de empresa
        EditText edtEmail    = findViewById(R.id.edtLoginEmail);
        EditText edtNome     = findViewById(R.id.edtNomeEmpresa);
        EditText edtWebsite  = findViewById(R.id.edtWebsite);
        EditText edtCNPJ     = findViewById(R.id.edtcnpj);
        EditText edtSenha    = findViewById(R.id.edtLoginSenha);
        Button btnCadastrar  = findViewById(R.id.btnLoginEntrar);
        ImageView btnVoltar  = findViewById(R.id.imgEsqSenhabtnVoltar);

        btnVoltar.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnCadastrar.setOnClickListener(v -> {
            String email   = edtEmail.getText().toString().trim();
            String senha   = edtSenha.getText().toString().trim();
            String nome    = edtNome.getText().toString().trim();
            String website = edtWebsite.getText().toString().trim();
            String CNPJ    = edtCNPJ.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty() || nome.isEmpty() || website.isEmpty() || CNPJ.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (senha.length() < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifica se CNPJ já existe
            db.collection("users")
                    .whereEqualTo("CNPJ", CNPJ)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                Toast.makeText(this, "CNPJ já cadastrado", Toast.LENGTH_LONG).show();
                            } else {
                                criarEmpresa(email, senha, nome, website, CNPJ, "Jurídica");
                            }
                        } else {
                            Toast.makeText(this, "Erro ao verificar CNPJ: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void criarEmpresa(String email, String senha, String nome, String website, String documento, String tipo) {
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Map<String, Object> empresaData = new HashMap<>();
                            empresaData.put("email", email);
                            empresaData.put("nome", nome);
                            empresaData.put("website", website);
                            empresaData.put("CNPJ", documento);
                            empresaData.put("tipo", tipo);
                            empresaData.put("vagasPublicadas", new ArrayList<String>());
                            empresaData.put("dataCadastro", System.currentTimeMillis());

                            db.collection("users").document(user.getUid())
                                    .set(empresaData)
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
