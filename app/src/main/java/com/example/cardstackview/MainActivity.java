package com.example.cardstackview;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cardstackview.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MaterialToolbar idTopAppBar;
    DrawerLayout idDrawer;
    NavigationView idNavView;

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        List<CardActivity> cards=new ArrayList<>();
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.card1, null), "texto 1"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.imgiii, null), "texto 2"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.imgiiii, null), "texto 3"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.card1, null), "texto 1"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.imgiii, null), "texto 2"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.imgiiii, null), "texto 3"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.card1, null), "texto 1"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.imgiii, null), "texto 2"));
        cards.add(new CardActivity(ResourcesCompat.getDrawable(getResources(), R.drawable.imgiiii, null), "texto 3"));
        CardAdapter adapter=new CardAdapter(cards);
        binding.cardStack.setLayoutManager(new CardStackLayoutManager(getApplicationContext()));
        binding.cardStack.setAdapter(adapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });


        //drawer
        idTopAppBar = findViewById(R.id.idMainTopAppBar);

        idDrawer = findViewById(R.id.idDrawer);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, idDrawer, idTopAppBar, R.string.open_drawer, R.string.close_drawer);
        idDrawer.addDrawerListener(toggle);

        toggle.syncState();

    }
}