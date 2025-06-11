package com.example.cardstackview;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModeloBancoTalentosFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<CandidatoRecusado> candidatosList;
    private AdaptadorBancoTalentos adapter;
    private MaterialToolbar idTopAppBar;
    private DrawerLayout idDrawer;
    private NavigationView idNavView;
    private FirebaseFirestore db;

    public ModeloBancoTalentosFragment() {
        // Construtor público vazio obrigatório
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bancodetalentos_layout, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialização do Firestore
        db = FirebaseFirestore.getInstance();


        idDrawer = view.findViewById(R.id.idDrawer);
        idNavView = view.findViewById(R.id.idNavView);

        // Configuração do DrawerToggle
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
            } else if (id == R.id.idCriarVagasItemMenu) {
                Intent intent = new Intent(getActivity(), CriarVagaActivity.class);
                startActivity(intent);
            }

            idDrawer.closeDrawers();
            return true;
        });

        // Configuração do RecyclerView
        recyclerView = view.findViewById(R.id.idRecLista);
        candidatosList = new ArrayList<>();
        adapter = new AdaptadorBancoTalentos(requireContext(), candidatosList);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        // Carregar candidatos recusados do Firestore
        carregarCandidatosRecusados();

        return view;
    }

    private void carregarCandidatosRecusados() {
        db.collection("candidatosRecusados")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        candidatosList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CandidatoRecusado candidato = document.toObject(CandidatoRecusado.class);
                            candidato.setId(document.getId());
                            candidatosList.add(candidato);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Erro ao carregar candidatos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToLoginCandidato() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
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