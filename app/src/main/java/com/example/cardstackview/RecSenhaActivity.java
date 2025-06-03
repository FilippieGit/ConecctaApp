package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RecSenhaActivity extends AppCompatActivity {

    Button btnEsqSenha, btnCriarContEsqSenha;
    ImageView imgEsqSenhabtnVoltar;
    TextInputEditText txtEmailRecSenha;

    FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();

        txtEmailRecSenha = findViewById(R.id.txtNome); // Este campo é o e-mail
        btnEsqSenha = findViewById(R.id.btnEsqSenha);
        imgEsqSenhabtnVoltar = findViewById(R.id.imgEsqSenhabtnVoltar);
        btnCriarContEsqSenha = findViewById(R.id.btnCriarContEsqSenha);

        btnEsqSenha.setOnClickListener(view -> {
            String email = txtEmailRecSenha.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, digite seu e-mail.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "" +
                                    "Verifique seu e-mail para redefinir a senha.", Toast.LENGTH_LONG).show();
                            finish(); // volta para a tela anterior
                        } else {
                            Toast.makeText(this, "Erro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        imgEsqSenhabtnVoltar.setOnClickListener(view -> finish());

        btnCriarContEsqSenha.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SelecaoActivity.class));
            finish();
        });
    }
}
