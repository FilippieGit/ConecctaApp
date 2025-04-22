package com.example.cardstackview;  // Substitua com o nome do seu pacote

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CriarVagaActivity extends AppCompatActivity {

    private EditText edtTituloVaga, edtDescricaoVaga;
    private Button btnCriarVaga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_vaga_layout);  // Certifique-se de que o layout esteja no diretório correto

        // Inicializando os componentes
        edtTituloVaga = findViewById(R.id.edtTituloVaga);
        edtDescricaoVaga = findViewById(R.id.edtDescricaoVaga);
        btnCriarVaga = findViewById(R.id.btnCriarVaga);

        // Configurando o botão de criação da vaga
        btnCriarVaga.setOnClickListener(v -> {
            String titulo = edtTituloVaga.getText().toString();
            String descricao = edtDescricaoVaga.getText().toString();

            if (titulo.isEmpty() || descricao.isEmpty()) {
                Toast.makeText(CriarVagaActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            } else {
                // Envia os dados de volta para TelaPrincipalActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("titulo", titulo);
                resultIntent.putExtra("descricao", descricao);

                // Define o resultado da atividade
                setResult(RESULT_OK, resultIntent);

                // Fecha a tela de criação da vaga e volta para a TelaPrincipalActivity
                finish();
            }
        });
    }
}
