package com.example.cardstackview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ModeloVafaga extends Fragment {

    private RecyclerView recyclerView;
    private List<ListaVaga> listaVagaList;

    public ModeloVafaga() {
        // Construtor público vazio obrigatório
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.lista_vaga_layout, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = view.findViewById(R.id.recyclerView);

        listaVagaList = new ArrayList<>();
        listaVagaList.add(new ListaVaga("Vaga1", R.drawable.logo));
        listaVagaList.add(new ListaVaga("Vaga2", R.drawable.logo));
        listaVagaList.add(new ListaVaga("Vaga3", R.drawable.logo));
        listaVagaList.add(new ListaVaga("Vaga4", R.drawable.logo));
        listaVagaList.add(new ListaVaga("Vaga5", R.drawable.logo));

        AdapterVaga adapter = new AdapterVaga(requireContext(), listaVagaList);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
