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

public class teste extends Fragment {

    private RecyclerView recyclerView;
    private List<Lista> listaList;

    public teste() {
        // Construtor público vazio obrigatório
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bancodetalentos, container, false);

        // Ajuste de padding para barras do sistema (EdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialização do RecyclerView
        recyclerView = view.findViewById(R.id.idRecLista);
        listaList = new ArrayList<>();

        // Adiciona dados fictícios
        listaList.add(new Lista("Empresa1", R.drawable.logo));
        listaList.add(new Lista("Empresa2", R.drawable.logo));
        listaList.add(new Lista("Empresa3", R.drawable.logo));
        listaList.add(new Lista("Empresa4", R.drawable.logo));
        listaList.add(new Lista("Empresa5", R.drawable.logo));

        // Configura o Adapter
        Adapter adapter = new Adapter(requireContext(), listaList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
