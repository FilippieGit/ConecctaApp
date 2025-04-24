package com.example.cardstackview;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.cardstackview.databinding.ActivityMainBinding;

public class ListaMatchVaga extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Fragment inicial
        replaceFragment(new ModeloBancoTalentosActivity());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                replaceFragment(new ModeloCurrAleatActivity());
            } else if (id == R.id.nav_favorite) {
                replaceFragment(new ModeloTelaPrincipalActivity());
            } else if (id == R.id.nav_profile) {
                replaceFragment(new ModeloBancoTalentosActivity());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
