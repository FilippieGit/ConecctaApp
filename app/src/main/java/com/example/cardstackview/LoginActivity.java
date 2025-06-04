package com.example.cardstackview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText txtEmail, txtSenha;
    private Button btnEntrar, btnCriarConta, btnEsqSenha;
    private FirebaseAuth mAuth;
    private String tipoUsuario = "Física";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        FirebaseApp.initializeApp(this); // IMPORTANTE: garante que o Firebase esteja iniciado
        mAuth = FirebaseAuth.getInstance(); // Inicializa FirebaseAuth

        // Views
        txtEmail = findViewById(R.id.txtPessoaLoginEmail);
        txtSenha = findViewById(R.id.txtPessoaLoginSenha);
        btnEntrar = findViewById(R.id.btnPessoaLoginEntrar);
        btnCriarConta = findViewById(R.id.btnPessoaLoginCriarConta);
        btnEsqSenha = findViewById(R.id.btnPessoaLoginEsqSenha);

        tipoUsuario = getIntent().getStringExtra("tipoUsuario");
        if (tipoUsuario == null) tipoUsuario = "Física";

        btnEntrar.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                fazerLogin();
            } else {
                Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_LONG).show();
            }
        });

        btnCriarConta.setOnClickListener(v -> {
            Intent intent = tipoUsuario.equals("Jurídico")
                    ? new Intent(this, CadPJuridicaActivity.class)
                    : new Intent(this, CadastroActivity.class);
            startActivity(intent);
            finish();
        });

        btnEsqSenha.setOnClickListener(v -> {
            startActivity(new Intent(this, RecSenhaActivity.class));
        });
    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        } catch (Exception e) {
            Log.e("Login", "Erro ao verificar conexão", e);
            return false;
        }
    }

    private void fazerLogin() {
        String email = txtEmail.getText().toString().trim();
        String senha = txtSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Autenticando...");
        dialog.setCancelable(false);
        dialog.show();

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            checkUserType(user);
                        } else {
                            Toast.makeText(this, "Verifique seu e-mail antes de entrar", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }
                    } else {
                        handleLoginError(task.getException());
                    }
                });
    }

    private void checkUserType(FirebaseUser user) {
        Log.d("LoginDebug", "Tipo de usuário esperado: " + tipoUsuario);
        FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String userType = document.getString("tipo");
                        Log.d("LoginDebug", "Tipo encontrado no Firestore: " + userType);
                        if (tipoUsuario.equalsIgnoreCase(userType)) {
                            redirectUser(user);
                        } else {
                            Toast.makeText(this, "Tipo de conta incorreto para este login", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }
                    } else {
                        Toast.makeText(this, "Dados do usuário não encontrados", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao acessar dados do usuário", Toast.LENGTH_LONG).show();
                    Log.e("Firestore", "Erro ao buscar tipo do usuário", e);
                    mAuth.signOut();
                });
    }

    private void redirectUser(FirebaseUser user) {
        Intent intent = tipoUsuario.equals("Jurídica")
                ? new Intent(this, TelaEmpresaActivity.class)
                : new Intent(this, MainActivity.class);
        intent.putExtra("user_id", user.getUid());
        startActivity(intent);
        finish();
    }

    private void handleLoginError(Exception e) {
        String mensagem = "Erro ao fazer login";

        if (e instanceof FirebaseAuthInvalidUserException) {
            mensagem = "Usuário não encontrado";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            mensagem = "Senha incorreta";
        } else if (e instanceof FirebaseNetworkException) {
            mensagem = "Erro de rede. Verifique sua conexão.";
        } else if (e != null) {
            mensagem = "Erro: " + e.getLocalizedMessage();
            Log.e("LoginError", "Erro detalhado", e);
        }

        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            checkUserType(user);
        }
    }
}
