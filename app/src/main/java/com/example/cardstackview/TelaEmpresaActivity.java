package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class TelaEmpresaActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_empresa);

        // Inicializa componentes
        topAppBar = findViewById(R.id.idMainTopAppBar);
        drawerLayout = findViewById(R.id.idDrawer);
        navigationView = findViewById(R.id.idNavView);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Configura a Toolbar
        setSupportActionBar(topAppBar);

        // Configura o Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, topAppBar,
                R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configura os itens do Navigation Drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigationItemSelected(item.getItemId());
            return true;
        });

        // Configura o Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigationItemSelected(item.getItemId());
            return true;
        });

        // Carrega o fragment inicial
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void handleNavigationItemSelected(int itemId) {
        if (itemId == R.id.idLoginItemMenu) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (itemId == R.id.idVagasItemMenu) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (itemId == R.id.idConfigItemMenu) {
            startActivity(new Intent(this, ConfigActivity.class));
        } else if (itemId == R.id.idAjudaItemMenu) {
            startActivity(new Intent(this, FeedbackActivity.class));
        } else if (itemId == R.id.idSobreItemMenu) {
            startActivity(new Intent(this, SobreNosActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void handleBottomNavigationItemSelected(int itemId) {
        Fragment selectedFragment = null;

        if (itemId == R.id.nav_favorite) {
            selectedFragment = new ModeloBancoTalentosFragment();
        } else if (itemId == R.id.nav_home) {
            selectedFragment = new ModeloTelaPrincipalFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ModeloCurrAleatFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_empresa, selectedFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Método para os fragments controlarem o drawer
    public void toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}