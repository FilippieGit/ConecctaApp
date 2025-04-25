package com.example.cardstackview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ModeloTelaPrincipalFragment extends Fragment {

    private static final int REQUEST_CRIAR_VAGA = 1001;
    private static final int REQUEST_DETALHES_VAGA = 1002;
    private static final int RESULT_EXCLUIR_VAGA = Activity.RESULT_FIRST_USER;

    private RecyclerView recyclerView;
    private List<Vaga> listaVagas;
    private AdaptadorTelaPrincipal adapter;

    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tela_principal_layout, container, false);

        inicializarComponentes(view);
        configurarWindowInsets(view);
        configurarDrawerLayout(view);
        configurarRecyclerView(view);
        configurarListeners(view);

        return view;
    }

    private void inicializarComponentes(View view) {
        topAppBar = view.findViewById(R.id.idTelaPrincipalTopAppBar);
        drawerLayout = view.findViewById(R.id.idDrawer);
        navigationView = view.findViewById(R.id.idNavView);
        recyclerView = view.findViewById(R.id.idRecLista);
        listaVagas = new ArrayList<>();
    }

    private void configurarWindowInsets(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void configurarDrawerLayout(View view) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                requireActivity(),
                drawerLayout,
                topAppBar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configurarRecyclerView(View view) {
        // Dados de exemplo
        listaVagas.add(new Vaga("Desenvolvedor Android", "Desenvolver apps Android", "São Paulo", "5000", "Java, Kotlin"));
        listaVagas.add(new Vaga("Analista de Dados", "Análise de dados e BI", "Rio de Janeiro", "4500", "SQL, Power BI"));

        adapter = new AdaptadorTelaPrincipal(requireContext(), listaVagas);
        adapter.setOnItemClickListener(this::abrirDetalhesVaga);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void configurarListeners(View view) {
        navigationView.setNavigationItemSelectedListener(this::handleNavigationItemSelected);
        view.findViewById(R.id.idAFAB).setOnClickListener(v -> abrirCriarVaga());
    }

    private boolean handleNavigationItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.idLoginItemMenu) {
            abrirLoginCandidato();
        } else if (id == R.id.idVagasItemMenu) {
            reiniciarActivity();
        } else if (id == R.id.idConfigItemMenu) {
            exibirMensagem("Você já está em Configurações");
        } else if (id == R.id.idAjudaItemMenu) {
            abrirFeedback();
        } else if (id == R.id.idSobreItemMenu) {
            abrirSobreNos();
        }

        drawerLayout.closeDrawers();
        return true;
    }

    private void abrirCriarVaga() {
        startActivityForResult(new Intent(requireActivity(), CriarVagaActivity.class), REQUEST_CRIAR_VAGA);
    }

    private void abrirDetalhesVaga(Vaga vaga) {
        Intent intent = new Intent(getContext(), DetalheVagaActivity.class);
        intent.putExtra("vaga", vaga);
        startActivityForResult(intent, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;

        if (requestCode == REQUEST_CRIAR_VAGA && resultCode == Activity.RESULT_OK) {
            Vaga vaga = (Vaga) data.getSerializableExtra("vagaPublicada");
            if (vaga != null) {
                listaVagas.add(vaga);
                adapter.notifyItemInserted(listaVagas.size() - 1);
                Toast.makeText(getContext(), "Vaga publicada!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 1 && resultCode == Activity.RESULT_FIRST_USER) {
            Vaga vagaExcluida = (Vaga) data.getSerializableExtra("vagaExcluida");
            if (vagaExcluida != null) {
                removerVaga(vagaExcluida);
            }
        }
    }



    private String getExtraKey(int requestCode) {
        return requestCode == RESULT_EXCLUIR_VAGA ? "vagaExcluida" : "vagaPublicada";
    }

    private void adicionarVaga(Vaga vaga) {
        listaVagas.add(vaga);
        adapter.notifyItemInserted(listaVagas.size() - 1);
        exibirMensagem("Vaga publicada com sucesso!");
    }

    private void atualizarVaga(Vaga vagaAtualizada) {
        for (int i = 0; i < listaVagas.size(); i++) {
            if (listaVagas.get(i).getTitulo().equals(vagaAtualizada.getTitulo())) {
                listaVagas.set(i, vagaAtualizada);
                adapter.notifyItemChanged(i);
                exibirMensagem("Vaga atualizada com sucesso!");
                break;
            }
        }
    }

    private void removerVaga(Vaga vagaExcluida) {
        for (int i = 0; i < listaVagas.size(); i++) {
            if (listaVagas.get(i).getTitulo().equals(vagaExcluida.getTitulo())) {
                listaVagas.remove(i);
                adapter.notifyItemRemoved(i);
                exibirMensagem("Vaga excluída com sucesso!");
                break;
            }
        }
    }

    private void abrirLoginCandidato() {
        startActivity(new Intent(requireActivity(), LoginPessoaFisica.class));
        requireActivity().finish();
    }

    private void reiniciarActivity() {
        startActivity(new Intent(requireActivity(), MainActivity.class));
    }

    private void abrirFeedback() {
        startActivity(new Intent(requireActivity(), FeedbackActivity.class));
    }

    private void abrirSobreNos() {
        startActivity(new Intent(requireActivity(), SobreNosActivity.class));
    }

    private void exibirMensagem(String mensagem) {
        Toast.makeText(requireContext(), mensagem, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        configurarBotaoVoltar();
    }

    private void configurarBotaoVoltar() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    requireActivity().onBackPressed();
                }
            }
        });
    }
}
