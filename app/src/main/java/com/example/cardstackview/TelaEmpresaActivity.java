package com.example.cardstackview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_empresa);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userType = prefs.getString("user_type", "Física"); // Default: candidato
        boolean isEmpresa = userType.equalsIgnoreCase("Jurídica");

        navigationView = findViewById(R.id.navigation_view);

        // Filtra itens do menu de acordo com o tipo de usuário
        if (navigationView != null) {
            if (isEmpresa) {
                // Empresa: esconde itens que só fazem sentido para candidatos
                navigationView.getMenu().findItem(R.id.idCriarVagasItemMenu).setVisible(false);
                navigationView.getMenu().findItem(R.id.idLoginItemMenu).setVisible(false);
            } else {
                // Candidato: esconde itens que só fazem sentido para empresas
                navigationView.getMenu().findItem(R.id.idVagasItemMenu).setVisible(false);
                navigationView.getMenu().findItem(R.id.idLoginItemMenu).setVisible(false);
            }
        }

        // Inicializa componentes com os IDs do XML
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        topAppBar = findViewById(R.id.topAppBar);
        bottomNavigationView = findViewById(R.id.bottomNavigation);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.azulprimary));
        }

        // Configura a Toolbar
        setSupportActionBar(topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Configura o Drawer Toggle
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, topAppBar,
                R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configura o listener do ícone de navegação
        topAppBar.setNavigationOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

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

    /**
     * Atualiza o título da Toolbar
     * @param title Novo título a ser exibido
     */
    public void setToolbarTitle(String title) {
        if (topAppBar != null) {
            topAppBar.setTitle(title);
        }
    }



    /**
     * Mostra ou esconde o ícone de navegação (hamburger/back)
     * @param show true para mostrar o ícone, false para esconder
     */
    public void showNavigationIcon(boolean show) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(show);
        }
        toggle.setDrawerIndicatorEnabled(show);
    }

    /**
     * Altera o ícone de navegação
     * @param iconResId Resource ID do ícone a ser exibido
     * @param listener Listener para o clique no ícone
     */
    public void setNavigationIcon(int iconResId, View.OnClickListener listener) {
        if (topAppBar != null) {
            topAppBar.setNavigationIcon(iconResId);
            topAppBar.setNavigationOnClickListener(listener);
        }
    }

    /**
     * Restaura a configuração padrão da Toolbar (ícone hamburger e abre/fecha drawer)
     */
    public void restoreDefaultToolbar() {
        if (topAppBar != null) {
            topAppBar.setNavigationIcon(R.drawable.baseline_menu);
            topAppBar.setNavigationOnClickListener(view -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
            toggle.setDrawerIndicatorEnabled(true);
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
        } else if (itemId == R.id.idCriarVagasItemMenu) {
            startActivity(new Intent(this, CriarVagaActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void handleBottomNavigationItemSelected(int itemId) {
        Fragment selectedFragment = null;
        String title = "";  // Novo: define o título a ser usado

        if (itemId == R.id.nav_favorite) {
            selectedFragment = new ModeloBancoTalentosFragment();
            title = "Banco de Talentos";
        } else if (itemId == R.id.nav_home) {
            selectedFragment = new ModeloTelaPrincipalFragment();
            title = "Tela Principal";
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new PerfilEmpresaActivity();
            title = "Perfil da Empresa";
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_empresa, selectedFragment)
                    .commit();

            // Atualiza o título da toolbar
            setToolbarTitle(title);
        }
    }

    public long getUserId() {
        return getIntent().getLongExtra("USER_ID", -1);
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