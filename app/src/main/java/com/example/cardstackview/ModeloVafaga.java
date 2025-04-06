package com.example.cardstackview;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ModeloVafaga extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ListaVaga> listaVagaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lista_vaga_layout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaVagaList = new ArrayList<>();
        listaVagaList.add(new ListaVaga("Vaga1", R.drawable.logo));
        listaVagaList.add(new ListaVaga("Vaga2", R.drawable.logo));
        listaVagaList.add(new ListaVaga("Vaga3", R.drawable.logo));
        listaVagaList.add(new ListaVaga("Vaga4", R.drawable.logo));
        listaVagaList.add(new ListaVaga("Vaga5", R.drawable.logo));

        recyclerView = findViewById(R.id.idRecLista);
        AdapterVaga adapter = new AdapterVaga(this, listaVagaList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }
}