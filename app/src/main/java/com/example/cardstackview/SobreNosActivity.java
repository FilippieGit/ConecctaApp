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

public class SobreNosActivity extends AppCompatActivity {

    MaterialToolbar idTopAppBar;
    DrawerLayout idDrawer;
    NavigationView idNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobre_nos_layout);

        // Inicializar componentes
        idTopAppBar = findViewById(R.id.idSobreTopAppBar);
        idDrawer = findViewById(R.id.idDrawer);
        idNavView = findViewById(R.id.idNavView);

        // Configurar toggle do menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                idDrawer,
                idTopAppBar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        idDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // Itens do menu
        idNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.idLoginItemMenu) {
                    goToLoginCandidato();
                } else if (id == R.id.idVagasItemMenu) {
                    Intent intent = new Intent(SobreNosActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (id == R.id.idConfigItemMenu) {
                    Intent intent = new Intent(SobreNosActivity.this, ConfigActivity.class);
                    startActivity(intent);
                } else if (id == R.id.idAjudaItemMenu) {
                    Intent intent = new Intent(SobreNosActivity.this, FeedbackActivity.class);
                    startActivity(intent);
                } else if (id == R.id.idSobreItemMenu) {
                    Toast.makeText(SobreNosActivity.this, "Você já está em Sobre Nós", Toast.LENGTH_SHORT).show();
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

    // Função para redirecionar para login
    private void goToLoginCandidato() {
        Intent intent = new Intent(SobreNosActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
