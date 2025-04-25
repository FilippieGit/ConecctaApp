package com.example.cardstackview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetalheVagaActivity extends AppCompatActivity {

    private ImageView imageLogoDetalhe;
    private TextView textTituloDetalhe, textDescricaoDetalhe, textLocalizacaoDetalhe;
    private TextView textSalarioDetalhe, textRequisitosDetalhe;
    private TextView textNivelExperienciaDetalhe, textTipoContratoDetalhe, textAreaAtuacaoDetalhe;
    private Vaga vaga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button btnRemoverVaga = findViewById(R.id.BtnDetalheExcluir);
        btnRemoverVaga.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("vagaExcluida", vaga);
            setResult(Activity.RESULT_FIRST_USER, resultIntent); // RESULT_FIRST_USER = exclusão
            finish(); // Fecha a activity
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_vaga_layout);

        inicializarComponentes();
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

        findViewById(R.id.BtnDetalheExcluir).setOnClickListener(v -> {
            if (vaga != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("vaga_id", vaga.getId()); // ou qualquer identificador único
                setResult(RESULT_OK, resultIntent);
                finish(); // fecha a activity
            }
        });

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
            // Se a Intent estiver vazia ou não existir, exibe uma mensagem de erro
            textTituloDetalhe.setText("Erro ao carregar os dados");
            textDescricaoDetalhe.setText("Tente novamente mais tarde.");
        }
    }
}
