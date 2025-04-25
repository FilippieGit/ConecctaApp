package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CriarVagaActivity extends AppCompatActivity {

    private static final int REQUEST_PREVIEW = 1001;
    private TextInputEditText edtTituloVaga, edtDescricaoVaga, edtLocalizacao, edtSalario, edtRequisitos;
    private Spinner spinnerNivelExperiencia, spinnerTipoContrato, spinnerAreaAtuacao;
    private MaterialButton btnCriarVaga;
    private Vaga vagaParaEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_vaga_layout);

        inicializarComponentes();
        configurarSpinners();
        configurarListeners();

        // Adicione esta verificação para o caso de edição via onNewIntent
        if (getIntent() != null) {
            if (getIntent().hasExtra(Constants.EXTRA_VAGA_EDITAR)) {
                vagaParaEditar = (Vaga) getIntent().getSerializableExtra(Constants.EXTRA_VAGA_EDITAR);
                preencherCamposParaEdicao(vagaParaEditar);
            }
        }
    }

    // Adicione este método para lidar com novas instâncias via singleTop
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.hasExtra(Constants.EXTRA_VAGA_EDITAR)) {
            vagaParaEditar = (Vaga) intent.getSerializableExtra(Constants.EXTRA_VAGA_EDITAR);
            preencherCamposParaEdicao(vagaParaEditar);
        }
    }

    private void inicializarComponentes() {
        edtTituloVaga = findViewById(R.id.edtTituloVaga);
        edtDescricaoVaga = findViewById(R.id.edtDescricaoVaga);
        edtLocalizacao = findViewById(R.id.edtLocalizacao);
        edtSalario = findViewById(R.id.edtSalario);
        edtRequisitos = findViewById(R.id.edtRequisitos);

        spinnerNivelExperiencia = findViewById(R.id.spinnerNivelExperiencia);
        spinnerTipoContrato = findViewById(R.id.spinnerTipoContrato);
        spinnerAreaAtuacao = findViewById(R.id.spinnerAreaAtuacao);

        btnCriarVaga = findViewById(R.id.btnCriarVaga);
        findViewById(R.id.imgVoltar).setOnClickListener(v -> finish());
    }

    private void configurarSpinners() {
        ArrayAdapter<CharSequence> adapterNivel = ArrayAdapter.createFromResource(this,
                R.array.niveis_experiencia, android.R.layout.simple_spinner_item);
        spinnerNivelExperiencia.setAdapter(adapterNivel);

        ArrayAdapter<CharSequence> adapterContrato = ArrayAdapter.createFromResource(this,
                R.array.tipos_contrato, android.R.layout.simple_spinner_item);
        spinnerTipoContrato.setAdapter(adapterContrato);

        ArrayAdapter<CharSequence> adapterArea = ArrayAdapter.createFromResource(this,
                R.array.areas_atuacao, android.R.layout.simple_spinner_item);
        spinnerAreaAtuacao.setAdapter(adapterArea);
    }

    private void configurarListeners() {
        btnCriarVaga.setOnClickListener(v -> validarECriarVaga());
    }

    private void validarECriarVaga() {
        // Adicione validação básica dos campos
        if (edtTituloVaga.getText().toString().isEmpty()) {
            edtTituloVaga.setError("Título é obrigatório");
            return;
        }

        Vaga vaga = new Vaga(
                edtTituloVaga.getText().toString(),
                edtDescricaoVaga.getText().toString(),
                edtLocalizacao.getText().toString(),
                edtSalario.getText().toString(),
                edtRequisitos.getText().toString(),
                spinnerNivelExperiencia.getSelectedItem().toString(),
                spinnerTipoContrato.getSelectedItem().toString(),
                spinnerAreaAtuacao.getSelectedItem().toString()
        );

        // Adicione flag de edição se necessário
//        //if (vagaParaEditar != null) {
//            //vaga.setId(vagaParaEditar.getId()); // Mantenha o mesmo ID para edição
//        //}

        Intent intent = new Intent(this, VagaPreVisualizacaoActivity.class);
        intent.putExtra(Constants.EXTRA_VAGA, vaga);
        intent.putExtra("modoEdicao", vagaParaEditar != null);
        startActivityForResult(intent, REQUEST_PREVIEW);
    }

    private void preencherCamposParaEdicao(Vaga vaga) {
        edtTituloVaga.setText(vaga.getTitulo());
        edtDescricaoVaga.setText(vaga.getDescricao());
        edtLocalizacao.setText(vaga.getLocalizacao());
        edtSalario.setText(vaga.getSalario());
        edtRequisitos.setText(vaga.getRequisitos());

        setarSpinner(spinnerNivelExperiencia, vaga.getNivelExperiencia());
        setarSpinner(spinnerTipoContrato, vaga.getTipoContrato());
        setarSpinner(spinnerAreaAtuacao, vaga.getAreaAtuacao());
    }

    private void setarSpinner(Spinner spinner, String valor) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            int posicao = adapter.getPosition(valor);
            if (posicao >= 0) spinner.setSelection(posicao);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PREVIEW && resultCode == RESULT_OK) {
            // Adicione verificação de nulos
            if (data != null && data.hasExtra(Constants.EXTRA_VAGA_PUBLICADA)) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.EXTRA_VAGA_PUBLICADA,
                        data.getSerializableExtra(Constants.EXTRA_VAGA_PUBLICADA));
                setResult(RESULT_OK, resultIntent);
            }
            finish();
        }
    }

    // Adicione este método para evitar duplicações no backstack
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
