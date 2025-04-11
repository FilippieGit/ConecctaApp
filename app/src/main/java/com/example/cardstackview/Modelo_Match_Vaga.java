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

public class Modelo_Match_Vaga extends AppCompatActivity {

    RecyclerView recyclerView;

    List<ListaMatchVaga> listaMatchVagaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lista_match_vaga_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaMatchVagaList = new ArrayList<>();

        listaMatchVagaList.add(new ListaMatchVaga("Match1", R.drawable.logo));
        listaMatchVagaList.add(new ListaMatchVaga("Match2", R.drawable.logo));
        listaMatchVagaList.add(new ListaMatchVaga("Match3", R.drawable.logo));
        listaMatchVagaList.add(new ListaMatchVaga("Match4", R.drawable.logo));
        listaMatchVagaList.add(new ListaMatchVaga("Match5", R.drawable.logo));


        recyclerView = findViewById(R.id.idPrincipalLista);

        Adapter_Match_Vaga adapter = new Adapter_Match_Vaga(this, listaMatchVagaList);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true); // Corrigindo o método
        recyclerView.setAdapter(adapter);


    }
}