package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class teste extends Fragment {

    private RecyclerView recyclerView;
    private List<Lista> listaList;
    private MaterialToolbar idTopAppBar;
    private DrawerLayout idDrawer;
    private NavigationView idNavView;

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

        // Inicializando os componentes
        idTopAppBar = view.findViewById(R.id.idBancoTopAppBar);  // MaterialToolbar
        idDrawer = view.findViewById(R.id.idDrawer);  // DrawerLayout
        idNavView = view.findViewById(R.id.idNavView);  // NavigationView

        // Configuração do ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(),
                idDrawer,
                idTopAppBar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        idDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // Configuração do NavigationView para responder aos cliques
        idNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                // Trate os itens do menu aqui
                if (id == R.id.idLoginItemMenu) {
                    // Intent para Login
                } else if (id == R.id.idConfigItemMenu) {
                    // Intent para Configurações
                }

                idDrawer.closeDrawers(); // Fecha o Drawer
                return true;
            }
        });

        // Inicializando o RecyclerView
        recyclerView = view.findViewById(R.id.idRecLista);
        listaList = new ArrayList<>();
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

    @Override
    public void onResume() {
        super.onResume();

        // Substitui o comportamento do botão de voltar
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (idDrawer.isDrawerOpen(GravityCompat.START)) {
                    idDrawer.closeDrawer(GravityCompat.START); // Fecha o Drawer
                } else {
                    requireActivity().onBackPressed(); // Comportamento padrão
                }
            }
        });
    }
}
