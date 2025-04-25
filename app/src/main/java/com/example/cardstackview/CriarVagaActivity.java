package com.example.cardstackview;  // Substitua com o nome do seu pacote

// CriarVagaActivity.java
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CriarVagaActivity extends AppCompatActivity {

    private TextInputEditText edtTituloVaga, edtDescricaoVaga, edtLocalizacao, edtSalario, edtRequisitos;
    private MaterialButton btnCriarVaga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_vaga_layout);

        inicializarComponentes();
        configurarListeners();
    }

    private void inicializarComponentes() {
        edtTituloVaga = findViewById(R.id.edtTituloVaga);
        edtDescricaoVaga = findViewById(R.id.edtDescricaoVaga);
        edtLocalizacao = findViewById(R.id.edtLocalizacao);
        edtSalario = findViewById(R.id.edtSalario);
        edtRequisitos = findViewById(R.id.edtRequisitos);
        btnCriarVaga = findViewById(R.id.btnCriarVaga);

        // Botão de voltar
        findViewById(R.id.imgVoltar).setOnClickListener(v -> finish());
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

        if (validarCampos(titulo, descricao, localizacao, requisitos)) {
            Vaga novaVaga = new Vaga(titulo, descricao, localizacao, salario, requisitos);
            retornarVagaParaFragment(novaVaga);
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

    private void retornarVagaParaFragment(Vaga vaga) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("nova_vaga", vaga);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
