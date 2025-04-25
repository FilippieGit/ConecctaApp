package com.example.cardstackview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CriarVagaActivity extends AppCompatActivity {

    private TextInputEditText edtTituloVaga, edtDescricaoVaga, edtLocalizacao, edtSalario, edtRequisitos;
    private Spinner spinnerNivelExperiencia, spinnerTipoContrato, spinnerAreaAtuacao;
    private MaterialButton btnCriarVaga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_vaga_layout);

        inicializarComponentes();
        configurarSpinners(); // Configurar os Spinners
        configurarListeners();
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

        // Botão de voltar
        findViewById(R.id.imgVoltar).setOnClickListener(v -> finish());
    }

    // Método para configurar os Spinners
    private void configurarSpinners() {
        // Nível de Experiência
        ArrayAdapter<CharSequence> adapterNivelExperiencia = ArrayAdapter.createFromResource(this,
                R.array.niveis_experiencia, android.R.layout.simple_spinner_item);
        adapterNivelExperiencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivelExperiencia.setAdapter(adapterNivelExperiencia);

        // Tipo de Contrato
        ArrayAdapter<CharSequence> adapterTipoContrato = ArrayAdapter.createFromResource(this,
                R.array.tipos_contrato, android.R.layout.simple_spinner_item);
        adapterTipoContrato.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoContrato.setAdapter(adapterTipoContrato);

        // Área de Atuação
        ArrayAdapter<CharSequence> adapterAreaAtuacao = ArrayAdapter.createFromResource(this,
                R.array.areas_atuacao, android.R.layout.simple_spinner_item);
        adapterAreaAtuacao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAreaAtuacao.setAdapter(adapterAreaAtuacao);
    }


    private void configurarListeners() {
        btnCriarVaga.setOnClickListener(v -> validarECriarVaga());
    }

    private void validarECriarVaga() {
        String titulo = edtTituloVaga.getText().toString().trim();
        String descricao = edtDescricaoVaga.getText().toString().trim();
        String localizacao = edtLocalizacao.getText().toString().trim();
        String salario = edtSalario.getText().toString().trim();
        String requisitos = edtRequisitos.getText().toString().trim();

        String nivelExperiencia = spinnerNivelExperiencia.getSelectedItem().toString();
        String tipoContrato = spinnerTipoContrato.getSelectedItem().toString();
        String areaAtuacao = spinnerAreaAtuacao.getSelectedItem().toString();

        if (validarCampos(titulo, descricao, localizacao, requisitos)) {
            Vaga vaga = new Vaga(titulo, descricao, localizacao, salario, requisitos, nivelExperiencia, tipoContrato, areaAtuacao);
            abrirTelaDetalhes(vaga);
        }
    }

    private boolean validarCampos(String titulo, String descricao, String localizacao, String requisitos) {
        if (titulo.isEmpty()) {
            mostrarErro("Título é obrigatório!");
            return false;
        }
        if (descricao.isEmpty()) {
            mostrarErro("Descrição é obrigatória!");
            return false;
        }
        if (localizacao.isEmpty()) {
            mostrarErro("Localização é obrigatória!");
            return false;
        }
        if (requisitos.isEmpty()) {
            mostrarErro("Requisitos são obrigatórios!");
            return false;
        }
        return true;
    }

    private void mostrarErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void abrirTelaDetalhes(Vaga vaga) {
        Intent intent = new Intent(this, DetalheVagaActivity.class);
        intent.putExtra("vaga", vaga);
        startActivity(intent);
    }
}
