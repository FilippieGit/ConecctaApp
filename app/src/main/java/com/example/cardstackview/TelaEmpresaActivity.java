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

        // Fragmento inicial
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_empresa, new Modelo_Match_Vaga())
                .commit();

        // Listener do BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_favorite) {
                selectedFragment = new teste(); // ou qualquer outro fragment
            } else if (id == R.id.nav_home) {
                selectedFragment = new Modelo_Match_Vaga();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ModeloVafaga(); // ou qualquer outro fragment
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_empresa, selectedFragment)
                        .commit();
            }

            return true;
        });

    }
}
