package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

        // Aplicando paddings para barras de sistema
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialização de componentes
        idTopAppBar = view.findViewById(R.id.idBancoTopAppBar);
        idDrawer = view.findViewById(R.id.idDrawer);
        idNavView = view.findViewById(R.id.idNavView);

        // Drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(),
                idDrawer,
                idTopAppBar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        idDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation menu item click handling
        idNavView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.idLoginItemMenu) {
                goToLoginCandidato();
            } else if (id == R.id.idVagasItemMenu) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.idConfigItemMenu) {
                Toast.makeText(getActivity(), "Você já está em Configurações", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.idAjudaItemMenu) {
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent);
            } else if (id == R.id.idSobreItemMenu) {
                Intent intent = new Intent(getActivity(), SobreNosActivity.class);
                startActivity(intent);
            }

            idDrawer.closeDrawers();
            return true;
        });

        // RecyclerView
        recyclerView = view.findViewById(R.id.idRecLista);
        listaList = new ArrayList<>();
        listaList.add(new Lista("Empresa1", R.drawable.logo));
        listaList.add(new Lista("Empresa2", R.drawable.logo));
        listaList.add(new Lista("Empresa3", R.drawable.logo));
        listaList.add(new Lista("Empresa4", R.drawable.logo));
        listaList.add(new Lista("Empresa5", R.drawable.logo));

        Adapter adapter = new Adapter(requireContext(), listaList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void goToLoginCandidato() {
        Intent intent = new Intent(getActivity(), LoginPessoaFisica.class);
        startActivity(intent);
        getActivity().finish(); // Fecha tela atual
    }

    @Override
    public void onResume() {
        super.onResume();

        // Comportamento do botão de voltar
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (idDrawer.isDrawerOpen(GravityCompat.START)) {
                    idDrawer.closeDrawer(GravityCompat.START);
                } else {
                    requireActivity().onBackPressed();
                }
            }
        });
    }
}
