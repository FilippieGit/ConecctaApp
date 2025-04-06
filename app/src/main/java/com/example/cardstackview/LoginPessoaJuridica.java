package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class LoginPessoaJuridica extends AppCompatActivity {

    Button btnempresaentrar, btncadastrar, btnesquecisenha;

    TextInputEditText txtPessoaEmpresaEmail, txtPessoaEmpresaSenha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_pessoa_j_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnempresaentrar = findViewById(R.id.btnPessoaEmpresaEntrar);
        btncadastrar = findViewById(R.id.btnPessoaEmpresaCriarConta);
        btnesquecisenha = findViewById(R.id.btnPessoaEmpresaEsqSenha);

        txtPessoaEmpresaEmail = findViewById(R.id.txtPessoaEmpresaEmail);
        txtPessoaEmpresaSenha = findViewById(R.id.txtPessoaEmpresaSenha);






        btnempresaentrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, senha;

                email = txtPessoaEmpresaEmail.getText().toString().trim();
                senha = txtPessoaEmpresaSenha.getText().toString().trim();

                if (email.equals("empresa")&& senha.equals("empresa")){

                    startActivity(new Intent(getApplicationContext(),
                            teste.class));
                    finish();

                }else {
                    Toast.makeText(getApplicationContext(),
                            "Usuários ou senha inválidos",
                            Toast.LENGTH_SHORT).show();
                }





            }
        });



    }
}