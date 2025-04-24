package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PerfilActivity extends AppCompatActivity {

    ImageView imgPerfilPbtnVoltar;

    Button btnPEditarPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.perfil_pessoa_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Função de voltar

        imgPerfilPbtnVoltar = findViewById(R.id.imgPerfilPbtnVoltar);

        imgPerfilPbtnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish(); // Apenas volta para a tela anterior
            }
        });

        //Função de voltar

        btnPEditarPerfil = findViewById(R.id.btnPEditarPerfil);

        btnPEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), EdicaoPerfilPessoaActivity.class));
                finish();

            }
        });
    }
}