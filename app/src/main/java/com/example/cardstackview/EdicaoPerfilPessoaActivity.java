package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EdicaoPerfilPessoaActivity extends AppCompatActivity {

    ImageView imgVoltarPerfilPessoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edicao_perfil_pessoa_layout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.perfil), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // ✅ Agora sim, depois do setContentView:
        Button btnAdicionar = findViewById(R.id.btnAdicionarCertificado);

        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(EdicaoPerfilPessoaActivity.this);
                View dialogView = inflater.inflate(R.layout.dialog_adicionar_certificado_layout, null);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EdicaoPerfilPessoaActivity.this);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(true);

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                Button btnSalvar = dialogView.findViewById(R.id.buttonSalvarCertificado);
                EditText editNome = dialogView.findViewById(R.id.editTextNomeCertificado);
                EditText editInstituicao = dialogView.findViewById(R.id.editTextInstituicao);
                EditText editAno = dialogView.findViewById(R.id.editTextAno);

                btnSalvar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nomeCertificado = editNome.getText().toString();
                        String instituicao = editInstituicao.getText().toString();
                        String ano = editAno.getText().toString();

                        TextView textCertificados = findViewById(R.id.perfilCertificados);
                        textCertificados.setText(nomeCertificado + " - " + instituicao + " (" + ano + ")");

                        alertDialog.dismiss();
                    }
                });
        //Função de voltar

        imgVoltarPerfilPessoa = findViewById(R.id.imgVoltarPerfilPessoa);

        imgVoltarPerfilPessoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PerfilActivity.class));
                finish(); // Apenas volta para a tela anterior
            }
        });
    }
}
