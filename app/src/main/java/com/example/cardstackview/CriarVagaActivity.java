package com.example.cardstackview;  // Substitua com o nome do seu pacote

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CriarVagaActivity extends AppCompatActivity {

    private EditText edtTituloVaga, edtDescricaoVaga, edtLocalizacao, edtSalario, edtRequisitos;
    private Button btnCriarVaga;

    // Lista para armazenar as vagas temporariamente
    private ArrayList<VagaActivity> listaVagas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_vaga_layout);  // Verifique se o nome está correto

        // Inicializando os componentes
        edtTituloVaga = findViewById(R.id.edtTituloVaga);
        edtDescricaoVaga = findViewById(R.id.edtDescricaoVaga);
        edtLocalizacao = findViewById(R.id.edtLocalizacao);
        edtSalario = findViewById(R.id.edtSalario);
        edtRequisitos = findViewById(R.id.edtRequisitos);
        btnCriarVaga = findViewById(R.id.btnCriarVaga);

        // Ação do botão
        btnCriarVaga.setOnClickListener(v -> {
            String titulo = edtTituloVaga.getText().toString().trim();
            String descricao = edtDescricaoVaga.getText().toString().trim();
            String localizacao = edtLocalizacao.getText().toString().trim();
            String salario = edtSalario.getText().toString().trim();
            String requisitos = edtRequisitos.getText().toString().trim();

            // Validação simples
            if (titulo.isEmpty() || descricao.isEmpty() || localizacao.isEmpty() || requisitos.isEmpty()) {
                Toast.makeText(CriarVagaActivity.this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            } else {
                // Criando a vaga e adicionando à lista
                VagaActivity novaVaga = new VagaActivity(titulo, descricao, localizacao, salario, requisitos);
                listaVagas.add(novaVaga); // Adiciona a vaga à lista

                // Criando o Intent para abrir a TelaPrincipalActivity
                Intent intent = new Intent(CriarVagaActivity.this, ModeloTelaPrincipalActivity.class);
                intent.putExtra("vagas", listaVagas); // Passa a lista de vagas para a tela principal

                // Enviando os dados e abrindo a TelaPrincipalActivity
                startActivity(intent);

                // Finaliza a atividade atual (não mantendo-a na pilha de atividades)
                finish();
            }
        });
    }
}
