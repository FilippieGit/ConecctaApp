package com.example.cardstackview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecSenhaActivity extends AppCompatActivity {
    Button btnEsqSenha;
    ImageView imgEsqSenhabtnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.rec_senha_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Mostra um aviso na tela

        btnEsqSenha = findViewById(R.id.btnEsqSenha);

        btnEsqSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RecSenhaActivity.this, "Cheque o e-mail que lhe enviamos.", Toast.LENGTH_SHORT).show();
                finish(); // Isso volta para a tela anterior (ex: LoginActivity)
            }
        });

        //Função de voltar

        imgEsqSenhabtnVoltar = findViewById(R.id.imgEsqSenhabtnVoltar);

        imgEsqSenhabtnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Apenas volta para a tela anterior
            }
        });

    }
}