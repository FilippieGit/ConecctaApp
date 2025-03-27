package com.example.cardstackview;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class teste extends AppCompatActivity {

    RecyclerView recyclerView;

    List<Lista> listaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lista_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaList = new ArrayList<>();

        listaList.add(
                new Lista("Empresa1", R.drawable.logo)
        );
        listaList.add(
                new Lista("Empresa2", R.drawable.logo)
        );
        listaList.add(
                new Lista("Empresa3", R.drawable.logo)
        );
        listaList.add(
                new Lista("Empresa4", R.drawable.logo)
        );
        listaList.add(
                new Lista("Empresa5", R.drawable.logo)
        );

        recyclerView = findViewById(R.id.idRecLista);

        Adapter adapter = new Adapter(this, listaList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true); // Corrigindo o método
        recyclerView.setAdapter(adapter);


    }
}