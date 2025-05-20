package com.example.cardstackview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class DialogAdicionarFormacaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dialog_adicionar_formacao_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<Formacao> listaFormacoes = new ArrayList<>();
        FormacaoAdapter adapterForm = new FormacaoAdapter(listaFormacoes);
        RecyclerView recyclerForm = findViewById(R.id.recyclerFormacoes);
        recyclerForm.setLayoutManager(new LinearLayoutManager(this));
        recyclerForm.setAdapter(adapterForm);

        Button btnAddForm = findViewById(R.id.btnAdicionarFormacao);
        btnAddForm.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adicionar_formacao_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            EditText curso = dialogView.findViewById(R.id.editTextCurso);
            EditText instituicao = dialogView.findViewById(R.id.editTextInstituicao);
            EditText anoInicio = dialogView.findViewById(R.id.editTextAnoInicio);
            EditText anoConclusao = dialogView.findViewById(R.id.editTextAnoConclusao);
            EditText descricao = dialogView.findViewById(R.id.editTextDescricao);
            Button btnSalvar = dialogView.findViewById(R.id.buttonSalvarFormacao);

            btnSalvar.setOnClickListener(v2 -> {
                Formacao form = new Formacao(
                        curso.getText().toString(),
                        instituicao.getText().toString(),
                        anoInicio.getText().toString(),
                        anoConclusao.getText().toString(),
                        descricao.getText().toString()
                );
                listaFormacoes.add(form);
                adapterForm.notifyItemInserted(listaFormacoes.size() - 1);
                dialog.dismiss();
            });

            dialog.show();
        });


    }
}