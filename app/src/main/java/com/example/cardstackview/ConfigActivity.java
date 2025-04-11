package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class ConfigActivity extends AppCompatActivity {

    MaterialToolbar idTopAppBar;
    DrawerLayout idDrawer;
    NavigationView idNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);

        // Inicializar os componentes
        idTopAppBar = findViewById(R.id.idConfigTopAppBar);
        idDrawer = findViewById(R.id.idDrawer);
        idNavView = findViewById(R.id.idNavView);

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
                    Intent intent = new Intent(ConfigActivity.this, FeedbackAcitivity.class);
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

    @Override
    public void onBackPressed() {
        if (idDrawer.isDrawerOpen(GravityCompat.START)) {
            idDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Função para redirecionar para a tela de Login de Candidato
    private void goToLoginCandidato() {
        Intent intent = new Intent(ConfigActivity.this, LoginPessoaFisica.class);
        startActivity(intent);
        finish(); // Fecha a tela atual, se desejar
    }
}
