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

public class Login_pessoa_fisica extends AppCompatActivity {

    Button btnPessoaLoginEntrar, btnPessoaLoginCriarConta, btnPessoaLoginEsqSenha;
    TextInputEditText txtPessoaLoginEmail, txtPessoaLoginSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_pessoa_fisica_layoyt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Apresentando as variáveis do java para o xml

        btnPessoaLoginEntrar = findViewById(R.id.btnPessoaLoginEntrar);
        btnPessoaLoginEsqSenha = findViewById(R.id.btnPessoaLoginEsqSenha);
        btnPessoaLoginCriarConta = findViewById(R.id.btnPessoaLoginCriarConta);

        txtPessoaLoginEmail = findViewById(R.id.txtPessoaLoginEmail);
        txtPessoaLoginSenha = findViewById(R.id.txtPessoaLoginSenha);

        //Verificar a senha e o e-mail


        btnPessoaLoginEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;

                email = txtPessoaLoginEmail.getText().toString().trim();
                password = txtPessoaLoginSenha.getText().toString().trim();

                if (email.equals("etecia") && password.equals("etecia")) {
                    startActivity(new Intent(getApplicationContext(),
                            MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Usuários ou senha inválidos",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Leva do "Login" para o "Esqueci a senha"


        btnPessoaLoginEsqSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), RecSenhaActivity.class));

            }
        });

        //Leva do "Login" para o "Cadastrar"

        btnPessoaLoginCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), CadPJuridicaAcitivy.class));
                finish();

            }
        });


    }
}