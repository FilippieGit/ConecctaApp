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

public class Modelo_Match_Vaga extends Fragment {

    private RecyclerView recyclerView;
    private List<MatchVaga> listaMatchVagaList;

    public Modelo_Match_Vaga() {
        // Construtor vazio obrigatório
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.lista_model, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = view.findViewById(R.id.idRecLista);

        listaMatchVagaList = new ArrayList<>();
        listaMatchVagaList.add(new MatchVaga("Match1", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match2", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match3", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match4", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match5", R.drawable.logo));

        Adapter_Match_Vaga adapter = new Adapter_Match_Vaga(requireContext(), listaMatchVagaList);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
