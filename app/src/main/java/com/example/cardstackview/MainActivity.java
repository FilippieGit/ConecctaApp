package com.example.cardstackview;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cardstackview.databinding.ActivityMainBinding;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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
    }
}