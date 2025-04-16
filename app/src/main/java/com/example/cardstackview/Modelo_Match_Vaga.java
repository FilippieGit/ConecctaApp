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

public class Modelo_Match_Vaga extends Fragment {

    private RecyclerView recyclerView;
    private List<MatchVaga> listaMatchVagaList;
    MaterialToolbar idTopAppBar;
    DrawerLayout idDrawer;
    NavigationView idNavView;

    public Modelo_Match_Vaga() {
        // Construtor vazio obrigatório
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.lista_model, container, false);

        // Aplicando os insets para evitar que o conteúdo fique sobrepondo a barra de status
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializando os componentes
        idTopAppBar = view.findViewById(R.id.idTelaPrincipalTopAppBar);  // MaterialToolbar
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
            }
        });

        // Configurando o RecyclerView
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

    // Função para redirecionar para a tela de Login de Candidato
    private void goToLoginCandidato() {
        Intent intent = new Intent(getActivity(), LoginPessoaFisica.class);
        startActivity(intent);
        getActivity().finish(); // Fecha a tela atual, se desejar
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
                    requireActivity().onBackPressed(); // Chama o comportamento padrão
                }
            }
        });
    }
}

