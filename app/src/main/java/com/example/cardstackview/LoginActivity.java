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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
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
                // Substitua a verificação direta por esta chamada assíncrona
                Api.checkApiReachability(this, new Api.ApiReachabilityCallback() {
                    @Override
                    public void onResult(boolean isReachable) {
                        runOnUiThread(() -> {
                            if (isReachable) {
                                fazerLogin();
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Servidor indisponível. Tente novamente mais tarde.",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
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

        // Verificar se já está logado
        verificarUsuarioLogado();
    }

    private void verificarUsuarioLogado() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            buscarDadosUsuarioNoBanco(user.getUid(), user.getEmail());
        }
    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao verificar conexão", e);
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
                                salvarDadosUsuario(user);
                                buscarDadosUsuarioNoBanco(user.getUid(), user.getEmail());
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

    private void salvarDadosUsuario(FirebaseUser user) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("firebase_uid", user.getUid());
        editor.putString("user_email", user.getEmail());
        editor.apply();

        Log.d(TAG, "Dados básicos do usuário salvos: " + user.getUid() + ", " + user.getEmail());
    }

    private void buscarDadosUsuarioNoBanco(String firebaseUid, String email) {
        String url = Api.buildUrl(Api.URL_GET_USER_BY_UID, new HashMap<String, String>() {{
            put("uid", firebaseUid);
        }});

        Log.d(TAG, "Buscando dados do usuário na URL: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "Resposta da API: " + response);
                    try {
                        JSONObject json = new JSONObject(response);

                        // Verificação flexível de sucesso
                        boolean success = !json.has("error") || !json.getBoolean("error");

                        if (success) {
                            JSONObject userData = json.getJSONObject("user");
                            salvarDadosUsuarioCompletos(userData);
                            redirecionarUsuario();
                        } else {
                            Log.w(TAG, "Usuário não encontrado, tentando sincronizar");
                            sincronizarUsuarioFirebase(firebaseUid, email);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao processar resposta", e);
                        Toast.makeText(LoginActivity.this, "Erro ao processar dados do usuário", Toast.LENGTH_SHORT).show();
                        sincronizarUsuarioFirebase(firebaseUid, email);
                    }
                },
                error -> {
                    Log.e(TAG, "Erro na requisição: " + error.getMessage());
                    Toast.makeText(LoginActivity.this, "Erro ao conectar com o servidor", Toast.LENGTH_SHORT).show();
                    sincronizarUsuarioFirebase(firebaseUid, email);
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void salvarDadosUsuarioCompletos(JSONObject userData) throws Exception {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("user_id", userData.getLong("id")); // Garanta que está usando o nome correto do campo
        editor.putString("user_type", userData.getString("tipo"));
        editor.putString("nome", userData.optString("nome", ""));
        editor.putString("username", userData.optString("username", ""));
        editor.putString("genero", userData.optString("genero", ""));
        editor.putInt("idade", userData.optInt("idade", 0));
        editor.putString("telefone", userData.optString("telefone", ""));
        editor.putString("setor", userData.optString("setor", ""));
        editor.putString("descricao", userData.optString("descricao", ""));
        editor.putString("experiencia_profissional", userData.optString("experiencia_profissional", ""));
        editor.putString("formacao_academica", userData.optString("formacao_academica", ""));
        editor.putString("certificados", userData.optString("certificados", ""));
        editor.putString("imagem_perfil", userData.optString("imagem_perfil", ""));
        editor.putString("CNPJ", userData.optString("CNPJ", ""));
        editor.putString("website", userData.optString("website", ""));

        editor.apply();

        Log.d(TAG, "Dados completos do usuário salvos: " + prefs.getAll().toString());
    }

    private void sincronizarUsuarioFirebase(String firebaseUid, String email) {
        ProgressDialog syncDialog = new ProgressDialog(this);
        syncDialog.setMessage("Sincronizando dados...");
        syncDialog.setCancelable(false);
        syncDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Api.URL_SYNC_USER,
                response -> {
                    syncDialog.dismiss();
                    Log.d(TAG, "Resposta da sincronização: " + response);

                    // Verifica se a resposta contém texto de sucesso
                    if (response.contains("✅") || response.contains("sucesso")) {
                        Log.i(TAG, "Sincronização concluída com sucesso");
                        buscarDadosUsuarioNoBanco(firebaseUid, email);
                    } else {
                        try {
                            // Tenta parsear como JSON se não for mensagem de sucesso
                            JSONObject json = new JSONObject(response);
                            if (!json.getBoolean("error")) {
                                buscarDadosUsuarioNoBanco(firebaseUid, email);
                            } else {
                                Toast.makeText(this, "Falha na sincronização", Toast.LENGTH_LONG).show();
                                redirecionarUsuario();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Erro ao processar resposta de sincronização", e);
                            buscarDadosUsuarioNoBanco(firebaseUid, email);
                        }
                    }
                },
                error -> {
                    syncDialog.dismiss();
                    Log.e(TAG, "Erro na requisição de sincronização: " + error.getMessage());
                    Toast.makeText(this, "Erro ao sincronizar com o servidor", Toast.LENGTH_LONG).show();
                    buscarDadosUsuarioNoBanco(firebaseUid, email);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", firebaseUid);
                params.put("email", email);
                params.put("nome", email.split("@")[0]);
                params.put("tipo", "Física");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void redirecionarUsuario() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userType = prefs.getString("user_type", "Física");

        Log.d(TAG, "Redirecionando usuário do tipo: " + userType);

        Intent intent;
        if (userType.equalsIgnoreCase("Jurídica")) {
            intent = new Intent(this, TelaEmpresaActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        // Adiciona flags para limpar a pilha de atividades
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

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
        }

        Log.e(TAG, mensagem, e);
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }
}