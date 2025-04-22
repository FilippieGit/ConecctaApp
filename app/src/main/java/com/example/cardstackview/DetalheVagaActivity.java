package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetalheVagaActivity extends AppCompatActivity {

    private ImageView imageLogoDetalhe;
    private TextView textTituloDetalhe;
    private TextView textDescricaoDetalhe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_vaga_layout);

        imageLogoDetalhe = findViewById(R.id.imageLogoDetalhe);
        textTituloDetalhe = findViewById(R.id.textTituloDetalhe);
        textDescricaoDetalhe = findViewById(R.id.textDescricaoDetalhe);

        Intent intent = getIntent();
        if (intent != null) {
            String titulo = intent.getStringExtra("titulo");
            int imagemResId = intent.getIntExtra("imagem", R.drawable.logo);

            textTituloDetalhe.setText(titulo);
            imageLogoDetalhe.setImageResource(imagemResId);

            // Texto fictício apenas para exemplo
            textDescricaoDetalhe.setText("Essa vaga é uma excelente oportunidade para profissionais que desejam crescer em um ambiente inovador.");
        }
    }
}
