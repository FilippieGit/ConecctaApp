package com.example.cardstackview;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class CadastroActivity extends AppCompatActivity {

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

            // Consulta no Firestore para verificar se o CPF já existe
            db.collection("users")
                    .whereEqualTo("CPF", CPF)
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
                                                    userMap.put("CPF", CPF);
                                                    userMap.put("email", email);
                                                    userMap.put("nome", nome);
                                                    userMap.put("cidade", cidade);
                                                    userMap.put("tipo", "Física");

// >>> adicione estas 3 linhas:
                                                    userMap.put("certificados", new ArrayList<String>());
                                                    userMap.put("experiencias", new ArrayList<String>());
                                                    userMap.put("formacoes", new ArrayList<String>());

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



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
