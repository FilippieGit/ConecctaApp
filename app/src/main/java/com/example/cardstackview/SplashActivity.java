package com.example.cardstackview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        new Handler().postDelayed(this::verificarUsuarioLogado, 1000);
    }

    private void verificarUsuarioLogado() {
        // 1. Verifica primeiro o Firebase Auth (fonte mais confiável)
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // 2. Verifica as preferências compartilhadas
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean manterLogado = prefs.getBoolean("manter_logado", false);
        String firebaseUid = prefs.getString("firebase_uid", null);

        // Lógica de decisão:
        if (currentUser != null) {
            // Usuário está autenticado no Firebase
            if (manterLogado && firebaseUid != null && firebaseUid.equals(currentUser.getUid())) {
                // Tudo consistente - prossegue para a próxima tela
                String userType = prefs.getString("user_type", "Física");
                redirecionarUsuario(userType.equalsIgnoreCase("Jurídica"), currentUser.getUid());
            } else {
                // Dados inconsistentes - faz logout completo
                fazerLogoutCompleto();
                irParaLogin();
            }
        } else {
            // Não há usuário no Firebase Auth
            if (manterLogado) {
                // Estado inconsistente - limpa os dados
                limparDadosLogin();
            }
            irParaLogin();
        }
    }

    private void fazerLogoutCompleto() {
        // 1. Faz logout do Firebase
        mAuth.signOut();

        // 2. Limpa os dados locais
        limparDadosLogin();
    }

    private void limparDadosLogin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("manter_logado");
        editor.remove("user_type");
        editor.remove("firebase_uid");
        editor.remove("user_email");
        editor.remove("user_id");
        editor.apply();
    }

    private void redirecionarUsuario(boolean isEmpresa, String userId) {
        Intent intent = isEmpresa ?
                new Intent(this, TelaEmpresaActivity.class) :
                new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void irParaLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}