package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
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

public class LoginActivity extends AppCompatActivity {

    private Button btnEntrar, btnCriarConta, btnEsqSenha;
    private TextInputEditText txtEmail, txtSenha;
    private CheckBox checkEmpresa;
    private ImageView btnVoltar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_pessoa_fisica_layoyt);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializa views
        btnEntrar = findViewById(R.id.btnPessoaLoginEntrar);
        btnCriarConta = findViewById(R.id.btnPessoaLoginCriarConta);
        btnEsqSenha = findViewById(R.id.btnPessoaLoginEsqSenha);
        txtEmail = findViewById(R.id.txtPessoaLoginEmail);
        txtSenha = findViewById(R.id.txtPessoaLoginSenha);
        checkEmpresa = findViewById(R.id.checkEmpresa);
        btnVoltar = findViewById(R.id.imgLoginPbtnVoltar);

        // Configura listeners
        btnVoltar.setOnClickListener(v -> {
            startActivity(new Intent(this, SelecaoActivity.class));
            finish();
        });

        btnEntrar.setOnClickListener(v -> fazerLogin());

        btnCriarConta.setOnClickListener(v -> {
            startActivity(new Intent(this, SelecaoActivity.class));
            finish();
        });


        btnEsqSenha.setOnClickListener(v -> {
            startActivity(new Intent(this, RecSenhaActivity.class));
        });
    }

    private void fazerLogin() {
        String email = txtEmail.getText().toString().trim();
        String password = txtSenha.getText().toString().trim();
        boolean loginComoEmpresa = checkEmpresa.isChecked(); // Checkbox marcada = login como empresa

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            verificarTipoUsuario(user, loginComoEmpresa);
                        }
                    } else {
                        Toast.makeText(this, "Falha no login: " +
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void verificarTipoUsuario(FirebaseUser user, boolean loginComoEmpresa) {
        if (!user.isEmailVerified()) {
            Toast.makeText(this, "Por favor, verifique seu e-mail antes de fazer login",
                    Toast.LENGTH_LONG).show();
            mAuth.signOut();
            return;
        }

        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tipoUsuario = documentSnapshot.getString("tipo");

                        // Adicione este log para depuração
                        System.out.println("Tipo de usuário no banco: " + tipoUsuario);
                        System.out.println("Checkbox marcada? " + loginComoEmpresa);

                        if (tipoUsuario == null) {
                            Toast.makeText(this, "Tipo de usuário não definido", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            return;
                        }

                        // Verificação simplificada e mais clara
                        boolean tipoCorreto =
                                (loginComoEmpresa && tipoUsuario.equals("Jurídica")) ||
                                        (!loginComoEmpresa && tipoUsuario.equals("Física"));

                        if (tipoCorreto) {
                            redirecionarParaTelaCorreta(user, loginComoEmpresa);
                        } else {
                            String mensagemErro = loginComoEmpresa ?
                                    "Você tentou fazer login como empresa, mas esta conta é de candidato" :
                                    "Você tentou fazer login como candidato, mas esta conta é de empresa";

                            Toast.makeText(this, mensagemErro, Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }
                    } else {
                        Toast.makeText(this, "Dados do usuário não encontrados",
                                Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao verificar tipo de usuário: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                });
    }

    private void redirecionarParaTelaCorreta(FirebaseUser user, boolean isEmpresa) {
        System.out.println("Redirecionando para: " + (isEmpresa ? "TelaEmpresaActivity" : "MainActivity"));

        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

        Intent intent;
        if (isEmpresa) {
            intent = new Intent(this, TelaEmpresaActivity.class);
            intent.putExtra("empresa_id", user.getUid());
        } else {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("usuario_id", user.getUid());
        }

        startActivity(intent);
        finish();
    }
}