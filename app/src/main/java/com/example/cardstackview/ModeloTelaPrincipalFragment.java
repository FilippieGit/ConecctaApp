package com.example.cardstackview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class ModeloTelaPrincipalFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<MatchVaga> listaMatchVagaList;
    MaterialToolbar idTopAppBar;
    DrawerLayout idDrawer;
    NavigationView idNavView;
    private AdaptadorTelaPrincipal adapter;

    public ModeloTelaPrincipalFragment() {
        // Construtor vazio obrigatório
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tela_principal_layout, container, false);

        // Evita sobreposição com a barra de status
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializando os componentes
        idTopAppBar = view.findViewById(R.id.idTelaPrincipalTopAppBar);
        idDrawer = view.findViewById(R.id.idDrawer);
        idNavView = view.findViewById(R.id.idNavView);

        // Configurando o Drawer com a Toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(),
                idDrawer,
                idTopAppBar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        idDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // Itens do menu lateral
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

        // Configurando o FAB para abrir a tela de criação de vaga
        View fab = view.findViewById(R.id.idAFAB);
        fab.setOnClickListener(v -> {
            // Abre a tela de criação de vaga
            Intent intent = new Intent(getActivity(), CriarVagaActivity.class);  // Corrigido para CriarVagaActivity
            startActivityForResult(intent, 1);  // Inicia a atividade e aguarda o resultado
        });

        // RecyclerView
        recyclerView = view.findViewById(R.id.idRecLista);
        listaMatchVagaList = new ArrayList<>();

        // Exemplo de vagas
        listaMatchVagaList.add(new MatchVaga("Match1", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match2", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match3", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match4", R.drawable.logo));
        listaMatchVagaList.add(new MatchVaga("Match5", R.drawable.logo));

        adapter = new AdaptadorTelaPrincipal(requireContext(), listaMatchVagaList);

        // Clique no item
        adapter.setOnItemClickListener(vaga -> {
            Intent intent = new Intent(getActivity(), DetalheVagaActivity.class);
            intent.putExtra("titulo", vaga.getTitulo());
            intent.putExtra("imagem", vaga.getImage());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return view;
    }

    // Método que será chamado após a CriarVagaActivity retornar
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {  // Use Activity.RESULT_OK
            String titulo = data.getStringExtra("titulo");
            String descricao = data.getStringExtra("descricao");

            // Adiciona a nova vaga na lista e notifica o adapter
            MatchVaga novaVaga = new MatchVaga(titulo, R.drawable.logo);  // Use uma imagem padrão ou forneça uma
            listaMatchVagaList.add(novaVaga);
            adapter.notifyDataSetChanged();  // Atualiza a RecyclerView
        }
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