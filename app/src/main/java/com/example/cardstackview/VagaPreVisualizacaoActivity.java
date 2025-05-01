package com.example.cardstackview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class VagaPreVisualizacaoActivity extends AppCompatActivity {

    private static final int REQUEST_EDITAR_VAGA = 1002;
    private TextView preVisualizacaoTextTitulo, preVisualizacaoTextDescricao;
    private TextView preVisualizacaoTextLocalizacao, preVisualizacaoTextSalario;
    private TextView preVisualizacaoTextRequisitos, preVisualizacaoTextNivelExperiencia;
    private TextView preVisualizacaoTextTipoContrato, preVisualizacaoTextAreaAtuacao;
    private MaterialButton preVisualizacaoBtnPublicar;
    private ImageButton preVisualizacaoBtnEditar, preVisualizacaoBtnExcluir;
    private Vaga vaga;
    private boolean modoEdicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vaga_pre_visualizacao_layout);

        vincularComponentes();
        configurarListeners();
        exibirDadosVaga();
    }

    private void vincularComponentes() {
        preVisualizacaoTextTitulo = findViewById(R.id.preVisualizacaoTextTitulo);
        preVisualizacaoTextDescricao = findViewById(R.id.preVisualizacaoTextDescricao);
        preVisualizacaoTextLocalizacao = findViewById(R.id.preVisualizacaoTextLocalizacao);
        preVisualizacaoTextSalario = findViewById(R.id.preVisualizacaoTextSalario);
        preVisualizacaoTextRequisitos = findViewById(R.id.preVisualizacaoTextRequisitos);
        preVisualizacaoTextNivelExperiencia = findViewById(R.id.preVisualizacaoTextNivelExperiencia);
        preVisualizacaoTextTipoContrato = findViewById(R.id.preVisualizacaoTextTipoContrato);
        preVisualizacaoTextAreaAtuacao = findViewById(R.id.preVisualizacaoTextAreaAtuacao);

        preVisualizacaoBtnPublicar = findViewById(R.id.btnPreVisualizacaoPublicar);
        preVisualizacaoBtnEditar = findViewById(R.id.btnPreVisualizacaoBEditar);
        preVisualizacaoBtnExcluir = findViewById(R.id.btnPreVisualizacaoExcluir);
    }

    private void configurarListeners() {
        preVisualizacaoBtnPublicar.setOnClickListener(v -> publicarVaga());
        preVisualizacaoBtnEditar.setOnClickListener(v -> editarVaga());
        preVisualizacaoBtnExcluir.setOnClickListener(v -> excluirVaga());
    }

    private void exibirDadosVaga() {
        if (getIntent() != null && getIntent().hasExtra(Constants.EXTRA_VAGA)) {
            vaga = (Vaga) getIntent().getSerializableExtra(Constants.EXTRA_VAGA);
            modoEdicao = getIntent().getBooleanExtra("modoEdicao", false);

            preVisualizacaoTextTitulo.setText(vaga.getTitulo());
            preVisualizacaoTextDescricao.setText(vaga.getDescricao());
            preVisualizacaoTextLocalizacao.setText(String.format("Local: %s", vaga.getLocalizacao()));
            preVisualizacaoTextSalario.setText(String.format("Salário: %s", vaga.getSalario()));
            preVisualizacaoTextRequisitos.setText(String.format("Requisitos: %s", vaga.getRequisitos()));
            preVisualizacaoTextNivelExperiencia.setText(String.format("Nível: %s", vaga.getNivelExperiencia()));
            preVisualizacaoTextTipoContrato.setText(String.format("Contrato: %s", vaga.getTipoContrato()));
            preVisualizacaoTextAreaAtuacao.setText(String.format("Área: %s", vaga.getAreaAtuacao()));
        }
    }

    private void publicarVaga() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.EXTRA_VAGA_PUBLICADA, vaga);
        resultIntent.putExtra("modoEdicao", modoEdicao);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void editarVaga() {
        Intent intent = new Intent(this, CriarVagaActivity.class);
        intent.putExtra(Constants.EXTRA_VAGA_EDITAR, vaga);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, REQUEST_EDITAR_VAGA);
    }

    private void excluirVaga() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir esta vaga?")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    enviarResultadoExclusao(); // Chama o método que envia o resultado
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void enviarResultadoExclusao() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("limparCampos", true); // Adicione esta linha
        setResult(Activity.RESULT_OK, resultIntent); // Use RESULT_OK para indicar que a ação foi realizada
        finish();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDITAR_VAGA && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra(Constants.EXTRA_VAGA_PUBLICADA)) {
                vaga = (Vaga) data.getSerializableExtra(Constants.EXTRA_VAGA_PUBLICADA);
                exibirDadosVaga();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.EXTRA_VAGA_PUBLICADA, vaga);
                setResult(Activity.RESULT_OK, resultIntent);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
