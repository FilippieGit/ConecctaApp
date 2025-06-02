package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;

public class SelecaoActivity extends AppCompatActivity {

    MaterialButton btnEmpresa;
    MaterialButton btnCandidato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.selecao_layout);

        // Aplica o inset nos elementos da view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialização dos botões
        btnEmpresa = findViewById(R.id.btnEmpresa);
        btnCandidato = findViewById(R.id.btnCandidato);

        // Clique no botão Empresa
        btnEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SelecaoActivity", "Botão Empresa clicado");
                Intent intent = new Intent(SelecaoActivity.this, LoginPessoaJuridica.class);//Trocar para LoginPessoaJuridica para ir rapido TelaEmpresaActivity
                startActivity(intent);
            }
        });

        // Clique no botão Candidato
        btnCandidato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para a tela de login de pessoa física
                startActivity(new Intent(SelecaoActivity.this, LoginPessoaFisica.class));//Trocar para LoginPessoaFisica
                finish();  // Finaliza a atividade atual
            }
        });
    }
}
