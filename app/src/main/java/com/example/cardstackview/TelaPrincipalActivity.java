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

public class TelaPrincipalActivity extends Fragment {

    private RecyclerView recyclerView;
    private List<MatchVaga> listaMatchVagaList;
    MaterialToolbar idTopAppBar;
    DrawerLayout idDrawer;
    NavigationView idNavView;

    public TelaPrincipalActivity() {
        // Construtor vazio obrigatório
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tela_principal_layout, container, false);

        // Aplicando os insets para evitar que o conteúdo fique sobrepondo a barra de status
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializando os componentes
        idTopAppBar = view.findViewById(R.id.idTelaPrincipalTopAppBar);
        idDrawer = view.findViewById(R.id.idDrawer);
        idNavView = view.findViewById(R.id.idNavView);

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

        // Configuração do NavigationView
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

        // Configurando o RecyclerView
        recyclerView = view.findViewById(R.id.idRecLista);

        listaMatchVagaList = new ArrayList<>();
        listaMatchVagaList.add(new MatchVaga("Match1", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match2", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match3", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match4", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match5", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match1", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match2", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match3", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match4", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match5", R.drawable.logo));



        AdaptadorTelaPrincipal adapter = new AdaptadorTelaPrincipal(requireContext(), listaMatchVagaList);

        // Ação ao clicar em qualquer card
        adapter.setOnItemClickListener(vaga -> {
            Toast.makeText(getActivity(), "Clicou em: " + vaga.getTitulo(), Toast.LENGTH_SHORT).show();
            // Exemplo: abrir outra tela com os dados
            // Intent intent = new Intent(getActivity(), DetalheVagaActivity.class);
            // intent.putExtra("vaga", vaga);
            // startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return view;
    }

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
