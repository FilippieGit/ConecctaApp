package com.example.cardstackview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DialogAdicionarCertificadoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dialog_adicionar_certificado_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<Certificado> listaCertificados = new ArrayList<>();
        CertificadoAdapter adapterCert = new CertificadoAdapter(listaCertificados);
        RecyclerView recyclerCert = findViewById(R.id.recyclerCertificados);
        recyclerCert.setLayoutManager(new LinearLayoutManager(this));
        recyclerCert.setAdapter(adapterCert);

        Button btnAddCert = findViewById(R.id.btnAdicionarCertificado);
        btnAddCert.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adicionar_certificado_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            EditText nome = dialogView.findViewById(R.id.editTextNomeCertificado);
            EditText inst = dialogView.findViewById(R.id.editTextInstituicao);
            EditText ano = dialogView.findViewById(R.id.editTextAno);
            EditText duracao = dialogView.findViewById(R.id.editTextDuracao);
            EditText descricao = dialogView.findViewById(R.id.editTextDescricao);
            Button btnSalvar = dialogView.findViewById(R.id.buttonSalvarCertificado);

            btnSalvar.setOnClickListener(v2 -> {
                Certificado cert = new Certificado(
                        nome.getText().toString(),
                        inst.getText().toString(),
                        ano.getText().toString(),
                        duracao.getText().toString(),
                        descricao.getText().toString()
                );
                listaCertificados.add(cert);
                adapterCert.notifyItemInserted(listaCertificados.size() - 1);
                dialog.dismiss();
            });
            dialog.show();
        });

    }
}