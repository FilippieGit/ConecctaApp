package com.example.cardstackview;

import android.content.Intent;  // Importante para usar a Intent
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
                R.string.open_drawer,  // String para quando o Drawer abrir
                R.string.close_drawer // String para quando o Drawer fechar
        );

        // Adiciona o listener para abrir/fechar o Drawer automaticamente
        idDrawer.addDrawerListener(toggle);
        toggle.syncState();  // Sincroniza o estado inicial

        // Configura o NavigationView para responder aos cliques
        idNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Ação para cada item do menu com if-else
                if (item.getItemId() == R.id.nav_home) {
                    // Exemplo de ação para o item "Início"
                    Toast.makeText(ConfigActivity.this, "Início selecionado", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.idConfigItemMenu) {
                    // Ação para o item "Configurações"
                    Toast.makeText(ConfigActivity.this, "Configurações selecionadas", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.idLoginItemMenu) {
                    // Ação para ir para o Login de Candidato
                    goToLoginCandidato();
                }
                // Fechar o Drawer após a seleção
                idDrawer.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Se o Drawer estiver aberto, ele será fechado ao pressionar o botão de voltar
        if (idDrawer.isDrawerOpen(GravityCompat.START)) {
            idDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Função para redirecionar para a tela de Login de Candidato
    private void goToLoginCandidato() {
        Intent intent = new Intent(ConfigActivity.this, LoginPessoaFisica.class); // Troque LoginCandidatoActivity pelo nome da sua atividade
        startActivity(intent); // Inicia a nova atividade
        finish(); // Opcional, se você quiser que o ConfigActivity seja fechado ao abrir o login
    }
}
