package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cardstackview.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    MaterialToolbar idTopAppBar;
    DrawerLayout idDrawer;
    NavigationView idNavView;

    ActivityMainBinding binding;

    List<String> aceitos = new ArrayList<>();
    List<String> negados = new ArrayList<>();

    // 👇 Adicione aqui fora
    CardStackLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        // Configurar os cards
        List<CardActivity> cards = new ArrayList<>();
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.card1, null), "texto 1"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.imgiii, null), "texto 2"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.imgiiii, null), "texto 3"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.card1, null), "texto 1"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.imgiii, null), "texto 2"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.imgiiii, null), "texto 3"));

        CardAdapter adapter = new CardAdapter(cards);

        // Crie o CardStackListener antes
        CardStackListener cardStackListener = new CardStackListener() {
            @Override
            public void onCardSwiped(Direction direction) {
                int position = layoutManager.getTopPosition() - 1;
                if (position >= 0 && position < cards.size()) {
                    String texto = cards.get(position).getContent();

                    // Referência para os TextViews
                    TextView tvAceitos = findViewById(R.id.tvAceitos);
                    TextView tvRecusados = findViewById(R.id.tvRecusados);

                    if (direction == Direction.Right) {
                        aceitos.add(texto);
                        Toast.makeText(MainActivity.this, "Aceito: " + texto, Toast.LENGTH_SHORT).show();
                    } else if (direction == Direction.Left) {
                        negados.add(texto);
                        Toast.makeText(MainActivity.this, "Negado: " + texto, Toast.LENGTH_SHORT).show();
                    }

                    // Atualizar os TextViews na tela
                    tvAceitos.setText("Aceitos: " + String.join(", ", aceitos));
                    tvRecusados.setText("Recusados: " + String.join(", ", negados));
                }
            }


            @Override public void onCardDragging(Direction direction, float ratio) {}
            @Override public void onCardRewound() {}
            @Override public void onCardCanceled() {}
            @Override public void onCardAppeared(View view, int position) {}
            @Override public void onCardDisappeared(View view, int position) {}
        };

// Inicialize o layoutManager agora que a variável existe
        layoutManager = new CardStackLayoutManager(this, cardStackListener);
        layoutManager.setStackFrom(StackFrom.None);
        layoutManager.setVisibleCount(3);


        binding.cardStack.setLayoutManager(layoutManager);
        binding.cardStack.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Drawer e NavigationView
        idTopAppBar = findViewById(R.id.idMainTopAppBar);
        idDrawer = findViewById(R.id.idDrawer);
        idNavView = findViewById(R.id.idNavView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                idDrawer,
                idTopAppBar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        idDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // Listener do menu lateral
        idNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.idLoginItemMenu) {
                    goToLoginCandidato();
                } else if (id == R.id.idVagasItemMenu) {
                    Toast.makeText(MainActivity.this, "Você já está em Encontrar Vagas", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.idConfigItemMenu) {
                    startActivity(new Intent(MainActivity.this, ConfigActivity.class));
                } else if (id == R.id.idAjudaItemMenu) {
                    startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
                } else if (id == R.id.idSobreItemMenu) {
                    startActivity(new Intent(MainActivity.this, SobreNosActivity.class));
                }

                idDrawer.closeDrawers();
                return true;
            }
        });

        // 🎯 Listener da Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
                startActivity(intent);
                return true;
            }

            // Adicione mais ações aqui se tiver outros itens
            return false;
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
        Intent intent = new Intent(MainActivity.this, LoginPessoaFisica.class);
        startActivity(intent);
        finish();
    }
}
