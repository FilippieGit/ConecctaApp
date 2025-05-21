package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CriarVagaActivity extends AppCompatActivity {

    private TextInputEditText edtTituloVaga, edtDescricaoVaga, edtLocalizacao, edtSalario, edtRequisitos;
    private Spinner spinnerNivelExperiencia, spinnerTipoContrato, spinnerAreaAtuacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_vaga_layout);
        inicializarComponentes();
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

        ArrayAdapter<CharSequence> nivelAdapter = ArrayAdapter.createFromResource(this,
                R.array.niveis_experiencia, android.R.layout.simple_spinner_item);
        nivelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivelExperiencia.setAdapter(nivelAdapter);

        ArrayAdapter<CharSequence> contratoAdapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_contrato, android.R.layout.simple_spinner_item);
        contratoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoContrato.setAdapter(contratoAdapter);

        ArrayAdapter<CharSequence> areaAdapter = ArrayAdapter.createFromResource(this,
                R.array.areas_atuacao, android.R.layout.simple_spinner_item);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAreaAtuacao.setAdapter(areaAdapter);

        MaterialButton btnPreVisualizar = findViewById(R.id.btnCriarVaga);
        btnPreVisualizar.setOnClickListener(v -> enviarParaPreVisualizacao());

        findViewById(R.id.imgVoltar).setOnClickListener(v -> finish());
    }

    private void enviarParaPreVisualizacao() {
        String titulo = edtTituloVaga.getText().toString().trim();
        String descricao = edtDescricaoVaga.getText().toString().trim();
        String localizacao = edtLocalizacao.getText().toString().trim();
        String salario = edtSalario.getText().toString().trim();
        String requisitos = edtRequisitos.getText().toString().trim();
        String nivel = spinnerNivelExperiencia.getSelectedItem().toString();
        String contrato = spinnerTipoContrato.getSelectedItem().toString();
        String area = spinnerAreaAtuacao.getSelectedItem().toString();

        if (titulo.isEmpty() || descricao.isEmpty() || localizacao.isEmpty() || requisitos.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Exemplo: empresa_id fixo, ajuste conforme seu app
        int empresaId = 1;

        Vagas vaga = new Vagas(titulo, descricao, localizacao, salario, requisitos, nivel, contrato, area, empresaId);

        Intent intent = new Intent(this, VagaPreVisualizacaoActivity.class);
        intent.putExtra(Constants.EXTRA_VAGA, vaga);
        startActivity(intent);
    }
}
