package com.example.cardstackview;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CandidatosActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCandidatos;
    private CandidatoAdapter adapter;
    private List<Usuario> candidatosList = new ArrayList<>();
    private int vagaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidatos_layout); // Usando o nome do seu layout

        // Configura a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Obtém o ID da vaga
        vagaId = getIntent().getIntExtra("vaga_id", 0);
        setTitle("Candidatos - Vaga #" + vagaId);

        // Configura o RecyclerView
        recyclerViewCandidatos = findViewById(R.id.recyclerViewCandidatos);
        adapter = new CandidatoAdapter(candidatosList);
        recyclerViewCandidatos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCandidatos.setAdapter(adapter);

        // Busca os candidatos (dados mockados)
        buscarCandidatosMockados();
    }

    private void buscarCandidatosMockados() {
        // Limpa a lista atual
        candidatosList.clear();

        // Adiciona candidatos de exemplo
        candidatosList.add(new Usuario(1, "João Silva", "joao@email.com", "Desenvolvedor Android"));
        candidatosList.add(new Usuario(2, "Maria Souza", "maria@email.com", "Desenvolvedor Front-end"));
        candidatosList.add(new Usuario(3, "Carlos Oliveira", "carlos@email.com", "Analista de Dados"));
        candidatosList.add(new Usuario(4, "Ana Santos", "ana@email.com", "UX Designer"));
        candidatosList.add(new Usuario(5, "Pedro Costa", "pedro@email.com", "Product Manager"));

        // Notifica o adapter que os dados mudaram
        adapter.notifyDataSetChanged();

        // Mostra mensagem informando que são dados de exemplo
        Toast.makeText(this, "Exibindo dados de exemplo temporariamente", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}