package com.example.cardstackview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_layout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler().postDelayed(this::verificarUsuarioLogado, 1000);
    }

    private void verificarUsuarioLogado() {
        // Verifica se há um token salvo nas preferências
        boolean manterLogado = sharedPreferences.getBoolean("manter_logado", false);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified() && manterLogado) {
            // Usuário logado e marcou "manter conectado" - verificar tipo
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String tipo = documentSnapshot.getString("tipo");
                            Log.d("SplashDebug", "Tipo do usuário: " + tipo); // Adicione esta linha
                            if (tipo != null) {
                                tipo = tipo.trim();
                                boolean isEmpresa = tipo.equalsIgnoreCase("Jurídica");
                                Log.d("SplashDebug", "isEmpresa: " + isEmpresa); // E esta linha
                                redirecionarUsuario(currentUser, isEmpresa);
                            } else {
                                irParaLogin();
                            }
                        } else {
                            irParaLogin();
                        }
                    })
                    .addOnFailureListener(e -> irParaLogin());
        } else {
            // Usuário não logado ou não marcou "manter conectado" - vai para login
            irParaLogin();
        }
    }

    private void redirecionarUsuario(FirebaseUser user, boolean isEmpresa) {
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

    private void irParaLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}