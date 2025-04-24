package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar idTopAppBar;
    private DrawerLayout idDrawer;
    private NavigationView idNavView;
    private ActivityMainBinding binding;

    private List<String> aceitos = new ArrayList<>();
    private List<String> negados = new ArrayList<>();

    private CardStackLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        // 1) Monta lista de cards
        List<CardActivity> cards = new ArrayList<>();
        cards.add(new CardActivity(
                ResourcesCompat.getDrawable(getResources(), R.drawable.card1, null),
                "texto 1"
        ));
        cards.add(new CardActivity(
                ResourcesCompat.getDrawable(getResources(), R.drawable.imgiii, null),
                "texto 2"
        ));
        cards.add(new CardActivity(
                ResourcesCompat.getDrawable(getResources(), R.drawable.imgiiii, null),
                "texto 3"
        ));
        cards.add(new CardActivity(
                ResourcesCompat.getDrawable(getResources(), R.drawable.card1, null),
                "texto 1"
        ));
        cards.add(new CardActivity(
                ResourcesCompat.getDrawable(getResources(), R.drawable.card1, null),
                "texto 1"
        ));
        cards.add(new CardActivity(
                ResourcesCompat.getDrawable(getResources(), R.drawable.card1, null),
                "texto 1"
        ));
        cards.add(new CardActivity(
                ResourcesCompat.getDrawable(getResources(), R.drawable.card1, null),
                "texto 1"
        ));
        // … mais cards …
        CardAdapter adapter = new CardAdapter(cards);

        // 2) Define listener de swipe para registrar aceitos/negados
        CardStackListener cardStackListener = new CardStackListener() {
            @Override
            public void onCardSwiped(Direction direction) {
                int pos = layoutManager.getTopPosition() - 1;
                if (pos >= 0 && pos < cards.size()) {
                    String texto = cards.get(pos).getContent();
                    if (direction == Direction.Right) {
                        aceitos.add(texto);
                        Toast.makeText(MainActivity.this, "Aceito: " + texto, Toast.LENGTH_SHORT).show();
                    } else if (direction == Direction.Left) {
                        negados.add(texto);
                        Toast.makeText(MainActivity.this, "Negado: " + texto, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override public void onCardDragging(Direction dir, float ratio) {}
            @Override public void onCardRewound() {}
            @Override public void onCardCanceled() {}
            @Override public void onCardAppeared(View view, int position) {}
            @Override public void onCardDisappeared(View view, int position) {}
        };

        // 3) Configura o CardStackLayoutManager
        layoutManager = new CardStackLayoutManager(this, cardStackListener);
        layoutManager.setStackFrom(StackFrom.None);
        layoutManager.setVisibleCount(3);

        // 4) Associa ao CardStackView
        binding.cardStack.setLayoutManager(layoutManager);
        binding.cardStack.setAdapter(adapter);

        // 5) Botões de swipe programático (ícone X e coração)
        ImageButton btnReject = findViewById(R.id.btnReject); // X
        ImageButton btnLike   = findViewById(R.id.btnLike);   // Coração

        btnLike.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(400) // duração em milissegundos
                    .build();
            layoutManager.setSwipeAnimationSetting(setting);
            binding.cardStack.swipe();
        });

        btnReject.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(400) // duração em milissegundos
                    .build();
            layoutManager.setSwipeAnimationSetting(setting);
            binding.cardStack.swipe();
        });


        // 6) Edge-to-Edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        // 7) Drawer + NavigationView
        idTopAppBar = findViewById(R.id.idMainTopAppBar);
        idDrawer    = findViewById(R.id.idDrawer);
        idNavView   = findViewById(R.id.idNavView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, idDrawer, idTopAppBar,
                R.string.open_drawer, R.string.close_drawer
        );
        idDrawer.addDrawerListener(toggle);
        toggle.syncState();

        idNavView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.idLoginItemMenu)      goToLoginCandidato();
            else if (id == R.id.idVagasItemMenu) Toast.makeText(this, "Já está em Vagas", Toast.LENGTH_SHORT).show();
            else if (id == R.id.idConfigItemMenu)   startActivity(new Intent(this, ConfigActivity.class));
            else if (id == R.id.idAjudaItemMenu)    startActivity(new Intent(this, FeedbackActivity.class));
            else if (id == R.id.idSobreItemMenu)    startActivity(new Intent(this, SobreNosActivity.class));
            idDrawer.closeDrawers();
            return true;
        });

        // 8) Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }
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
        startActivity(new Intent(this, LoginPessoaFisica.class));
        finish();
    }
}

