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

    private TextInputEditText edtTituloVaga, edtDescricaoVaga, edtLocalizacao,
            edtSalario, edtRequisitos, edtBeneficios;
    private Spinner spinnerNivelExperiencia, spinnerTipoContrato, spinnerAreaAtuacao;
    private ChipGroup chipGroupHabilidades;
    private String[] habilidadesArray = {"Java", "Kotlin", "Android SDK", "Firebase", "UI/UX", "Git"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_vaga_layout);
        inicializarComponentes();
        carregarHabilidades();
    }

    private void inicializarComponentes() {
        // Campos de texto
        edtTituloVaga = findViewById(R.id.edtTituloVaga);
        edtDescricaoVaga = findViewById(R.id.edtDescricaoVaga);
        edtLocalizacao = findViewById(R.id.edtLocalizacao);
        edtSalario = findViewById(R.id.edtSalario);
        edtRequisitos = findViewById(R.id.edtRequisitos);
        edtBeneficios = findViewById(R.id.edtBeneficios);

        // Spinners
        spinnerNivelExperiencia = findViewById(R.id.spinnerNivelExperiencia);
        spinnerTipoContrato = findViewById(R.id.spinnerTipoContrato);
        spinnerAreaAtuacao = findViewById(R.id.spinnerAreaAtuacao);

        // ChipGroup para habilidades
        chipGroupHabilidades = findViewById(R.id.chipGroupHabilidades);

        // Configurar adaptadores para os Spinners
        configurarSpinners();

        // Botões
        MaterialButton btnPreVisualizar = findViewById(R.id.btnCriarVaga);
        btnPreVisualizar.setOnClickListener(v -> enviarParaPreVisualizacao());

        findViewById(R.id.imgVoltar).setOnClickListener(v -> finish());
    }

    private void configurarSpinners() {
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
    }

    private void carregarHabilidades() {
        for (String habilidade : habilidadesArray) {
            Chip chip = new Chip(this);
            chip.setText(habilidade);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.chip_background);
            chipGroupHabilidades.addView(chip);
        }
    }

    private void enviarParaPreVisualizacao() {
        // Obter valores dos campos
        String titulo = edtTituloVaga.getText().toString().trim();
        String descricao = edtDescricaoVaga.getText().toString().trim();
        String localizacao = edtLocalizacao.getText().toString().trim();
        String salario = edtSalario.getText().toString().trim();
        String requisitos = edtRequisitos.getText().toString().trim();
        String beneficios = edtBeneficios.getText().toString().trim();
        String nivel = spinnerNivelExperiencia.getSelectedItem().toString();
        String contrato = spinnerTipoContrato.getSelectedItem().toString();
        String area = spinnerAreaAtuacao.getSelectedItem().toString();

        // Validar campos obrigatórios
        if (titulo.isEmpty() || descricao.isEmpty() || localizacao.isEmpty() ||
                requisitos.isEmpty() || beneficios.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obter habilidades selecionadas
        List<String> habilidadesDesejaveis = obterHabilidadesSelecionadas();

        // Criar objeto Vaga
        int empresaId = 1; // ID da empresa (ajustar conforme necessário)
        String ramo = "Tecnologia"; // Definir ramo ou adicionar campo no formulário
        String vinculo = contrato; // Usar tipo de contrato como vínculo

        Vagas vaga = new Vagas(
                titulo, descricao, localizacao, salario, requisitos,
                nivel, contrato, area, beneficios, vinculo, ramo,
                empresaId, habilidadesDesejaveis
        );

        // Enviar para pré-visualização
        Intent intent = new Intent(this, VagaPreVisualizacaoActivity.class);
        intent.putExtra(Constants.EXTRA_VAGA, vaga);
        startActivity(intent);
    }

    private List<String> obterHabilidadesSelecionadas() {
        List<String> habilidades = new ArrayList<>();
        for (int i = 0; i < chipGroupHabilidades.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupHabilidades.getChildAt(i);
            if (chip.isChecked()) {
                habilidades.add(chip.getText().toString());
            }
        }
        return habilidades;
    }
}