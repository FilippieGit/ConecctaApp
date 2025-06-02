package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoritosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VagaFavoritaAdapter adapter;
    private VagaDatabaseHelper databaseHelper;
    private View emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favoritos_layout);

        // Inicializa views
        recyclerView = findViewById(R.id.rvFavoritos);
        emptyState = findViewById(R.id.emptyState);

        // Configura RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializa database helper
        databaseHelper = new VagaDatabaseHelper(this);

        // Configura botão de voltar
        findViewById(R.id.imgVoltar).setOnClickListener(v -> finish());

        // Carrega vagas favoritas
        carregarVagasFavoritas();
    }

    private void carregarVagasFavoritas() {
        List<Vagas> vagasFavoritas = databaseHelper.getAllVagasFavoritas();

        if (vagasFavoritas.isEmpty()) {
            // Mostra estado vazio
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // Esconde estado vazio e mostra lista
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Configura adapter
            adapter = new VagaFavoritaAdapter(vagasFavoritas, new VagaFavoritaAdapter.OnVagaClickListener() {
                @Override
                public void onVagaClick(Vagas vaga) {
                    // Remove vaga dos favoritos
                    databaseHelper.removerVagaFavorita(vaga.getVaga_id());
                    // Atualiza lista
                    carregarVagasFavoritas();
                }

                @Override
                public void onVagaDetalhesClick(Vagas vaga) {
                    Intent intent = new Intent(FavoritosActivity.this, DetalheVagaActivity.class);
                    intent.putExtra("vaga", vaga); // Passa o objeto vaga como extra
                    startActivity(intent);
                }
            });

            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualiza lista quando a activity retorna ao foco
        carregarVagasFavoritas();
    }
}