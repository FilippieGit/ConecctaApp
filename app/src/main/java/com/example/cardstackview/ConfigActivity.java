package com.example.cardstackview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ConfigActivity extends AppCompatActivity {

    MaterialToolbar idTopAppBar;
    DrawerLayout idDrawer;
    NavigationView idNavView;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);

        // Inicializa o Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        // Inicializar os componentes
        idTopAppBar = findViewById(R.id.idConfigTopAppBar);
        idDrawer = findViewById(R.id.idDrawer);
        idNavView = findViewById(R.id.idNavView);

        // Configura o botão de sair da conta
        MaterialButton btnSair = findViewById(R.id.btnSairContaConfig);
        btnSair.setOnClickListener(v -> fazerLogout());

        // Configuração do ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                idDrawer,
                idTopAppBar,
                R.string.open_drawer,
                R.string.close_drawer
        );

        idDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // Configura o NavigationView para responder aos cliques
        idNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.idLoginItemMenu) {
                    goToLoginCandidato();
                } else if (id == R.id.idVagasItemMenu) {
                    Intent intent = new Intent(ConfigActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (id == R.id.idConfigItemMenu) {
                    Toast.makeText(ConfigActivity.this, "Você já está em Configurações", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.idAjudaItemMenu) {
                    Intent intent = new Intent(ConfigActivity.this, FeedbackActivity.class);
                    startActivity(intent);
                } else if (id == R.id.idSobreItemMenu) {
                    Intent intent = new Intent(ConfigActivity.this, SobreNosActivity.class);
                    startActivity(intent);
                }

                idDrawer.closeDrawers();
                return true;
            }
        });
    }

    public static void limparDadosLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Remove todos os dados relacionados ao login
        editor.remove("manter_logado");
        editor.remove("user_type");
        editor.remove("firebase_uid");
        editor.remove("user_email");
        editor.remove("user_id");
        editor.remove("nome");
        editor.remove("username");
        // Adicione aqui qualquer outro campo relacionado ao usuário

        // Commit as alterações imediatamente
        editor.commit(); // Usamos commit() em vez de apply() para garantir execução imediata
    }

    private void fazerLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Sair da conta")
                .setMessage("Tem certeza que deseja sair?")
                .setPositiveButton("Sair", (dialog, which) -> {
                    // Cria intent com flag de logout
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("FORCE_LOGOUT", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Inicia a LoginActivity e finaliza todas as outras
                    startActivity(intent);
                    finishAffinity();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (idDrawer.isDrawerOpen(GravityCompat.START)) {
            idDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void goToLoginCandidato() {
        Intent intent = new Intent(ConfigActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}