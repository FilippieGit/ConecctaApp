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

public class ModeloCurrAleatFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<ListaVaga> listaVagaList;
    MaterialToolbar idTopAppBar;
    DrawerLayout idDrawer;
    NavigationView idNavView;

    public ModeloCurrAleatFragment() {
        // Construtor público vazio obrigatório
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.lista_vaga_layout, container, false);

        // Ajuste de insets
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialização dos componentes do Drawer
        idTopAppBar = view.findViewById(R.id.idCurriculosTopAppBar);
        idDrawer = view.findViewById(R.id.idDrawer);
        idNavView = view.findViewById(R.id.idNavView);

        // Toggle do menu lateral
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(),
                idDrawer,
                idTopAppBar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        idDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // Listener para o menu de navegação
        idNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.idLoginItemMenu) {
                    goToLoginCandidato();
                } else if (id == R.id.idVagasItemMenu) {
                    Toast.makeText(getActivity(), "Você já está em Vagas", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.idConfigItemMenu) {
                    Intent intent = new Intent(getActivity(), ConfigActivity.class);
                    startActivity(intent);
                } else if (id == R.id.idAjudaItemMenu) {
                    Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                    startActivity(intent);
                } else if (id == R.id.idSobreItemMenu) {
                    Intent intent = new Intent(getActivity(), SobreNosActivity.class);
                    startActivity(intent);
                }

                idDrawer.closeDrawers();
                return true;
            }
        });

        // Configuração da lista
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

    // Voltar ao login
    private void goToLoginCandidato() {
        Intent intent = new Intent(getActivity(), LoginPessoaFisica.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
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