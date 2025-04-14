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
        replaceFragment(new teste());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId(); // 👈 solução alternativa ao switch
            if (id == R.id.nav_home) {
                replaceFragment(new ModeloVafaga());
            } else if (id == R.id.nav_favorite) {
                replaceFragment(new Modelo_Match_Vaga());
            } else if (id == R.id.nav_profile) {
                replaceFragment(new teste());
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
