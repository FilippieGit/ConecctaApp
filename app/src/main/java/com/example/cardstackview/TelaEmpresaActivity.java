package com.example.cardstackview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TelaEmpresaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_empresa);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Listener do BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_favorite) {
                selectedFragment = new ModeloBancoTalentosFragment(); // ou qualquer outro fragment
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

        // Define visualmente o item nav_home como selecionado
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

}
