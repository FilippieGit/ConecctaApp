package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetalheVagaActivity extends AppCompatActivity {

    private ImageView imageLogoDetalhe;
    private TextView textTituloDetalhe, textDescricaoDetalhe, textLocalizacaoDetalhe;
    private TextView textSalarioDetalhe, textRequisitosDetalhe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_vaga_layout);

        // Inicializando os componentes
        imageLogoDetalhe = findViewById(R.id.imageLogoDetalhe);
        textTituloDetalhe = findViewById(R.id.textTituloDetalhe);
        textDescricaoDetalhe = findViewById(R.id.textDescricaoDetalhe);
        textLocalizacaoDetalhe = findViewById(R.id.textLocalizacaoDetalhe);
        textSalarioDetalhe = findViewById(R.id.textSalarioDetalhe);
        textRequisitosDetalhe = findViewById(R.id.textRequisitosDetalhe);

        // Pegando os dados da Intent
        Intent intent = getIntent();
        if (intent != null) {
            String titulo = intent.getStringExtra("titulo");
            String descricao = intent.getStringExtra("descricao");
            String localizacao = intent.getStringExtra("localizacao");
            String salario = intent.getStringExtra("salario");
            String requisitos = intent.getStringExtra("requisitos");
            int imagemResId = intent.getIntExtra("imagem", R.drawable.logo); // valor padrão

            // Verificando e aplicando valores de forma mais robusta
            if (!TextUtils.isEmpty(titulo)) {
                textTituloDetalhe.setText(titulo);
            } else {
                textTituloDetalhe.setText("Título não informado");
            }

            if (!TextUtils.isEmpty(descricao)) {
                textDescricaoDetalhe.setText(descricao);
            } else {
                textDescricaoDetalhe.setText("Descrição não disponível");
            }

            if (!TextUtils.isEmpty(localizacao)) {
                textLocalizacaoDetalhe.setText("Localização: " + localizacao);
            } else {
                textLocalizacaoDetalhe.setText("Localização não informada");
            }

            if (!TextUtils.isEmpty(salario)) {
                textSalarioDetalhe.setText("Salário: " + salario);
            } else {
                textSalarioDetalhe.setText("Salário não informado");
            }

            if (!TextUtils.isEmpty(requisitos)) {
                textRequisitosDetalhe.setText("Requisitos: " + requisitos);
            } else {
                textRequisitosDetalhe.setText("Requisitos não informados");
            }

            imageLogoDetalhe.setImageResource(imagemResId);
        } else {
            // Se a Intent estiver vazia ou não existir, exibe uma mensagem de erro
            textTituloDetalhe.setText("Erro ao carregar os dados");
            textDescricaoDetalhe.setText("Tente novamente mais tarde.");
        }
    }
}
