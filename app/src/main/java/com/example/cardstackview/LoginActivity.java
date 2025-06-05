package com.example.cardstackview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar views
        txtEmail = findViewById(R.id.txtPessoaLoginEmail);
        txtSenha = findViewById(R.id.txtPessoaLoginSenha);
        btnEntrar = findViewById(R.id.btnPessoaLoginEntrar);
        btnCriarConta = findViewById(R.id.btnPessoaLoginCriarConta);
        btnEsqSenha = findViewById(R.id.btnPessoaLoginEsqSenha);

        btnEntrar.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                fazerLogin();
            } else {
                Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_LONG).show();
            }
        });

        btnCriarConta.setOnClickListener(v -> {
            startActivity(new Intent(this, SelecaoActivity.class));
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

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Formato de e-mail inválido", Toast.LENGTH_SHORT).show();
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
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                verificarTipoUsuario(user);
                            } else {
                                Toast.makeText(this,
                                        "Verifique seu e-mail antes de entrar. Verifique sua caixa de spam também.",
                                        Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                            }
                        }
                    } else {
                        handleLoginError(task.getException());
                    }
                });
    }

    private void verificarTipoUsuario(FirebaseUser user) {
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tipoUsuario = documentSnapshot.getString("tipo");

                        if (tipoUsuario != null) {
                            tipoUsuario = tipoUsuario.trim();
                            Log.d("LoginDebug", "Tipo de usuário encontrado: " + tipoUsuario);

                            // Salvar tipo do usuário nas preferências
                            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            prefs.edit().putString("user_type", tipoUsuario).apply();

                            redirecionarUsuario(user, tipoUsuario);
                        } else {
                            Toast.makeText(this, "Tipo de usuário não definido", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }
                    } else {
                        Toast.makeText(this, "Dados do usuário não encontrados", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao verificar tipo de usuário", Toast.LENGTH_LONG).show();
                    Log.e("Firestore", "Erro ao buscar tipo do usuário", e);
                    mAuth.signOut();
                });
    }

    private void redirecionarUsuario(FirebaseUser user, String tipoUsuario) {
        Intent intent;

        if (tipoUsuario.equalsIgnoreCase("Jurídica")) {
            intent = new Intent(this, TelaEmpresaActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        intent.putExtra("user_id", user.getUid());
        intent.putExtra("user_email", user.getEmail());
        intent.putExtra("user_type", tipoUsuario);

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
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            String savedUserType = prefs.getString("user_type", null);

            if (savedUserType != null) {
                redirecionarUsuario(user, savedUserType);
            } else {
                verificarTipoUsuario(user);
            }
        }
    }
}