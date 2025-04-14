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

public class FeedbackActivity extends AppCompatActivity {

    MaterialToolbar idTopAppBar;
    DrawerLayout idDrawer;
    NavigationView idNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);

        // Inicializa os componentes
        idTopAppBar = findViewById(R.id.idConfigTopAppBar);
        idDrawer = findViewById(R.id.idDrawer);
        idNavView = findViewById(R.id.idNavView);

        // Configuração do Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                idDrawer,
                idTopAppBar,
                R.string.open_drawer,
                R.string.close_drawer
        );

        idDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // Define ações para os itens do menu lateral
        idNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.idLoginItemMenu) {
                    goToLoginCandidato();
                } else if (id == R.id.idVagasItemMenu) {
                    startActivity(new Intent(FeedbackActivity.this, MainActivity.class));
                } else if (id == R.id.idConfigItemMenu) {
                    startActivity(new Intent(FeedbackActivity.this, ConfigActivity.class));
                } else if (id == R.id.idAjudaItemMenu) {
                    Toast.makeText(FeedbackActivity.this, "Você já está em Ajuda/Feedback", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.idSobreItemMenu) {
                    startActivity(new Intent(FeedbackActivity.this, SobreNosActivity.class));
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

    private void goToLoginCandidato() {
        Intent intent = new Intent(FeedbackActivity.this, LoginPessoaFisica.class);
        startActivity(intent);
        finish();
    }
}
