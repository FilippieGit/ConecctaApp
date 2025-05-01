package com.example.cardstackview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetalheVagaActivity extends AppCompatActivity {

    private ImageView imageLogoDetalhe;
    private TextView textTituloDetalhe, textDescricaoDetalhe, textLocalizacaoDetalhe;
    private TextView textSalarioDetalhe, textRequisitosDetalhe;
    private TextView textNivelExperienciaDetalhe, textTipoContratoDetalhe, textAreaAtuacaoDetalhe;
    private Vaga vaga;
    private ImageButton btnVoltarDetalhe; // Add this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_vaga_layout);

        inicializarComponentes();
        exibirDetalhesVaga();

        FloatingActionButton btnExcluir = findViewById(R.id.BtnDetalheExcluir);
        btnExcluir.setOnClickListener(v -> excluirVaga());

        // Add listener for back button
        btnVoltarDetalhe.setOnClickListener(v -> finish());
    }

    private void inicializarComponentes() {
        imageLogoDetalhe = findViewById(R.id.imageLogoDetalhe);
        textTituloDetalhe = findViewById(R.id.textTituloDetalhe);
        textDescricaoDetalhe = findViewById(R.id.textDescricaoDetalhe);
        textLocalizacaoDetalhe = findViewById(R.id.textLocalizacaoDetalhe);
        textSalarioDetalhe = findViewById(R.id.textSalarioDetalhe);
        textRequisitosDetalhe = findViewById(R.id.textRequisitosDetalhe);
        textNivelExperienciaDetalhe = findViewById(R.id.textNivelExperienciaDetalhe);
        textTipoContratoDetalhe = findViewById(R.id.textTipoContratoDetalhe);
        textAreaAtuacaoDetalhe = findViewById(R.id.textAreaAtuacaoDetalhe);
        btnVoltarDetalhe = findViewById(R.id.btnVoltarDetalhe); // Initialize it
    }

    private void exibirDetalhesVaga() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("vaga")) {
            vaga = (Vaga) intent.getSerializableExtra("vaga");
            textTituloDetalhe.setText(vaga.getTitulo());
            textDescricaoDetalhe.setText(vaga.getDescricao());
            textLocalizacaoDetalhe.setText("Localização: " + vaga.getLocalizacao());
            textSalarioDetalhe.setText("Salário: " + vaga.getSalario());
            textRequisitosDetalhe.setText("Requisitos: " + vaga.getRequisitos());
            textNivelExperienciaDetalhe.setText("Nível: " + vaga.getNivelExperiencia());
            textTipoContratoDetalhe.setText("Contrato: " + vaga.getTipoContrato());
            textAreaAtuacaoDetalhe.setText("Área: " + vaga.getAreaAtuacao());
        } else {
            textTituloDetalhe.setText("Erro ao carregar os dados");
            textDescricaoDetalhe.setText("Tente novamente mais tarde.");
        }
    }

    private void excluirVaga() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("vagaExcluida", vaga);
        setResult(Activity.RESULT_FIRST_USER, resultIntent);
        finish();
    }
}
