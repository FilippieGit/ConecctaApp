package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class TelaEmpresaActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_empresa);

        // Inicializa os componentes
        topAppBar = findViewById(R.id.idTopAppBar);
        drawerLayout = findViewById(R.id.idDrawer);
        navigationView = findViewById(R.id.idNavView);

        // Configura a Toolbar
        setSupportActionBar(topAppBar);

        // Configura o Drawer Navigation
        setupDrawerNavigation();

        // Configura o Bottom Navigation
        setupBottomNavigation();
    }

    private void setupDrawerNavigation() {
        // Configura o toggle do drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                topAppBar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configura os itens do menu do drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.idLoginItemMenu) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else if (id == R.id.idVagasItemMenu) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else if (id == R.id.idConfigItemMenu) {
                startActivity(new Intent(this, ConfigActivity.class));
            } else if (id == R.id.idAjudaItemMenu) {
                startActivity(new Intent(this, FeedbackActivity.class));
            } else if (id == R.id.idSobreItemMenu) {
                startActivity(new Intent(this, SobreNosActivity.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_favorite) {
                selectedFragment = new ModeloBancoTalentosFragment();
            } else if (id == R.id.nav_home) {
                selectedFragment = new ModeloTelaPrincipalFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ModeloCurrAleatFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_empresa, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Define o item inicial selecionado
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}