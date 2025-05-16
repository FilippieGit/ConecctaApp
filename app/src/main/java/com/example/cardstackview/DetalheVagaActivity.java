package com.example.cardstackview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        // Suponha que você tenha essa informação em SharedPreferences ou recebeu via Intent
        boolean isPessoaJuridica = getIntent().getBooleanExtra("isPessoaJuridica", false);

        FloatingActionButton btnExcluir = findViewById(R.id.BtnDetalheExcluir);

        if (isPessoaJuridica) {
            btnExcluir.setVisibility(View.VISIBLE);
        } else {
            btnExcluir.setVisibility(View.GONE);
        }

        exibirDetalhesVaga();

        btnExcluir.setOnClickListener(v -> excluirVaga());

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
            Vagas vaga = (Vagas) intent.getSerializableExtra("vaga");
            textTituloDetalhe.setText(vaga.getTitulo());
            textDescricaoDetalhe.setText(vaga.getDescricao());
            textLocalizacaoDetalhe.setText("Localização: " + vaga.getLocalizacao());
            textSalarioDetalhe.setText("Salário: " + vaga.getSalario());
            textRequisitosDetalhe.setText("Requisitos: " + vaga.getRequisitos());
            textNivelExperienciaDetalhe.setText("Nível: " + vaga.getNivel_experiencia());
            textTipoContratoDetalhe.setText("Contrato: " + vaga.getTipo_contrato());
            textAreaAtuacaoDetalhe.setText("Área: " + vaga.getArea_atuacao());
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
