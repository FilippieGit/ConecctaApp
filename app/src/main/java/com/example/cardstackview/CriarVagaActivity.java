package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CriarVagaActivity extends AppCompatActivity {

    private TextInputEditText edtTituloVaga, edtDescricaoVaga, edtLocalizacao, edtSalario, edtRequisitos, edtBeneficios;
    private Spinner spinnerNivelExperiencia, spinnerTipoContrato, spinnerAreaAtuacao;
    private ChipGroup chipGroupHabilidades;

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
        edtBeneficios = findViewById(R.id.edtBeneficios);
        chipGroupHabilidades = findViewById(R.id.chipGroupHabilidades);

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
        String beneficios = edtBeneficios.getText().toString().trim();
        String nivel = spinnerNivelExperiencia.getSelectedItem().toString();
        String contrato = spinnerTipoContrato.getSelectedItem().toString();
        String area = spinnerAreaAtuacao.getSelectedItem().toString();

        // Habilidades desejáveis (pega textos dos chips)
        List<String> habilidadesDesejaveis = new ArrayList<>();
        for (int i = 0; i < chipGroupHabilidades.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupHabilidades.getChildAt(i);
            if (chip.isChecked() || chip.isSelected()) {
                habilidadesDesejaveis.add(chip.getText().toString());
            }
        }

        if (titulo.isEmpty() || descricao.isEmpty() || localizacao.isEmpty() || requisitos.isEmpty() || beneficios.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        int empresaId = 1; // Ajuste conforme seu app

        Vagas vaga = new Vagas(
                titulo, descricao, localizacao, salario, requisitos, nivel, contrato, area, beneficios, empresaId
        );
        vaga.setHabilidadesDesejaveis(habilidadesDesejaveis);

        Intent intent = new Intent(this, VagaPreVisualizacaoActivity.class);
        intent.putExtra(Constants.EXTRA_VAGA, vaga);
        startActivity(intent);
    }
}
