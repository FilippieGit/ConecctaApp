package com.example.cardstackview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class CadastroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        btnVoltar.setOnClickListener(v -> voltarParaLogin());

        btnCadastrar.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String senha = edtSenha.getText().toString().trim();
            String nome = edtNome.getText().toString().trim();
            String cidade = edtCidade.getText().toString().trim();
            String CPF = edtCPF.getText().toString().trim();

            if (!validarCampos(email, senha, nome, cidade, CPF)) {
                return;
            }

            verificarECriarUsuario(email, senha, nome, cidade, CPF);
        });
    }

    private boolean validarCampos(String email, String senha, String nome, String cidade, String CPF) {
        if (email.isEmpty() || senha.isEmpty() || nome.isEmpty() || cidade.isEmpty() || CPF.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (senha.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Formato de e-mail inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void verificarECriarUsuario(String email, String senha, String nome, String cidade, String CPF) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verificando dados...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection("users")
                .whereEqualTo("CPF", CPF)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(this, "CPF já cadastrado", Toast.LENGTH_LONG).show();
                        } else {
                            // CORREÇÃO AQUI - ORDEM DOS PARÂMETROS
                            criarUsuario(email, senha, nome, cidade, CPF, "Física");
                        }
                    } else {
                        Toast.makeText(this, "Erro ao verificar CPF", Toast.LENGTH_LONG).show();
                        Log.e("Cadastro", "Erro CPF: " + task.getException());
                    }
                });
    }

    private void criarUsuario(String email, String senha, String nome, String cidade, String documento, String tipo) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Criando conta...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            salvarDadosUsuario(user, email, nome, cidade, documento, tipo, progressDialog);
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Erro no cadastro: " + getErrorMessage(task.getException()),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void salvarDadosUsuario(FirebaseUser user, String email, String nome,
                                    String cidade, String documento, String tipo,
                                    ProgressDialog progressDialog) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("nome", nome);
        userData.put("cidade", cidade);
        userData.put("tipo", tipo);
        userData.put("CPF", documento);
        userData.put("certificados", new ArrayList<String>());
        userData.put("experiencias", new ArrayList<String>());
        userData.put("formacoes", new ArrayList<String>());
        userData.put("dataCadastro", System.currentTimeMillis());

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    enviarEmailVerificacao(user, progressDialog);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Erro ao salvar dados", Toast.LENGTH_LONG).show();
                    Log.e("Cadastro", "Erro Firestore: " + e.getMessage());
                    user.delete(); // Remove usuário se não salvou dados
                });
    }

    private void enviarEmailVerificacao(FirebaseUser user, ProgressDialog progressDialog) {
        user.sendEmailVerification()
                .addOnCompleteListener(verifyTask -> {
                    progressDialog.dismiss();
                    if (verifyTask.isSuccessful()) {
                        Toast.makeText(this,
                                "Cadastro realizado! Verifique seu e-mail para ativar a conta.",
                                Toast.LENGTH_LONG).show();
                        voltarParaLogin();
                    } else {
                        Toast.makeText(this,
                                "Erro ao enviar verificação: " + getErrorMessage(verifyTask.getException()),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String getErrorMessage(Exception exception) {
        if (exception == null) return "Erro desconhecido";

        String errorMsg = exception.getMessage();
        if (errorMsg == null) return "Erro desconhecido";

        if (errorMsg.contains("email address is already")) {
            return "E-mail já cadastrado";
        } else if (errorMsg.contains("password is invalid")) {
            return "Senha inválida";
        } else if (errorMsg.contains("network error")) {
            return "Sem conexão com a internet";
        }
        return errorMsg;
    }

    private void voltarParaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        // Limpeza de recursos se necessário
        super.onDestroy();
    }
}