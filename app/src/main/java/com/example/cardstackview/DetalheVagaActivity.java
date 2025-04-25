package com.example.cardstackview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

public class DetalheVagaActivity extends AppCompatActivity {

    private ImageView imageLogoDetalhe;
    private TextView textTituloDetalhe, textDescricaoDetalhe, textLocalizacaoDetalhe;
    private TextView textSalarioDetalhe, textRequisitosDetalhe;
    private TextView textNivelExperienciaDetalhe, textTipoContratoDetalhe, textAreaAtuacaoDetalhe;
    private ChipGroup chipGroupHabilidadesDetalhe;
    private MaterialButton btnPublicar;
    private ImageButton btnEditar;

    private Vaga vaga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_vaga_layout);

        inicializarComponentes();
        configurarListeners();
        exibirDetalhesVaga();
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
        chipGroupHabilidadesDetalhe = findViewById(R.id.chipGroupHabilidadesDetalhe);

        btnPublicar = findViewById(R.id.btnPublicar);
        btnEditar = findViewById(R.id.btnEditar);
    }

    private void configurarListeners() {
        btnPublicar.setOnClickListener(v -> publicarVaga());
        btnEditar.setOnClickListener(v -> editarVaga());
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

            //TODO: Implementar exibição das habilidades no ChipGroup
            //chipGroupHabilidadesDetalhe.removeAllViews();
            //for (String habilidade : vaga.getHabilidades()) {
            //    Chip chip = new Chip(this);
            //    chip.setText(habilidade);
            //    chipGroupHabilidadesDetalhe.addView(chip);
            //}
        } else {
            // Se a Intent estiver vazia ou não existir, exibe uma mensagem de erro
            textTituloDetalhe.setText("Erro ao carregar os dados");
            textDescricaoDetalhe.setText("Tente novamente mais tarde.");
        }
    }

    private void publicarVaga() {
        // Retorna a vaga para o fragment
        Intent resultIntent = new Intent();
        resultIntent.putExtra("vagaPublicada", vaga);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void editarVaga() {
        // Abre a tela de edição
        Intent intent = new Intent(this, CriarVagaActivity.class);
        intent.putExtra("vagaParaEditar", vaga);
        setResult(Activity.RESULT_FIRST_USER, intent); // Usar um código diferente de RESULT_OK
        startActivity(intent);
        finish();
    }
}
