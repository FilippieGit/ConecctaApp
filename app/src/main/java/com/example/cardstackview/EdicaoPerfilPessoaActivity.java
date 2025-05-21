package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.ImageView;
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

public class EdicaoPerfilPessoaActivity extends AppCompatActivity {

    LinearLayout layoutCertificados, layoutExperiencias, layoutFormacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edicao_perfil_pessoa_layout);

        layoutCertificados = findViewById(R.id.layoutCertificados);
        layoutExperiencias = findViewById(R.id.layoutExperiencias);
        layoutFormacoes = findViewById(R.id.layoutFormacoes);

        Button btnAddCertificado = findViewById(R.id.btnAddCertificado);
        Button btnAddExperiencia = findViewById(R.id.btnAddExperiencia);
        Button btnAddFormacao = findViewById(R.id.btnAddFormacao);

        btnAddCertificado.setOnClickListener(v -> mostrarDialogCertificado());
        btnAddExperiencia.setOnClickListener(v -> mostrarDialogExperiencia());
        btnAddFormacao.setOnClickListener(v -> mostrarDialogFormacao());

        ImageView imgVoltarPerfilPessoa = findViewById(R.id.imgVoltarPerfilPessoa);
        imgVoltarPerfilPessoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PerfilActivity.class));
                finish(); // Encerra a tela atual para não empilhar
            }
        });

    }

    private void mostrarDialogCertificado() {
        View view = getLayoutInflater().inflate(R.layout.dialog_certificado, null);
        EditText nome = view.findViewById(R.id.editTextNomeCertificado);
        EditText instituicao = view.findViewById(R.id.editTextInstituicao);
        EditText ano = view.findViewById(R.id.editTextAno);
        Button btnSalvar = view.findViewById(R.id.buttonSalvarCertificado);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnSalvar.setOnClickListener(v -> {
            String texto = nome.getText().toString() + " - " + instituicao.getText().toString() + " (" + ano.getText().toString() + ")";
            adicionarItem(layoutCertificados, texto);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void mostrarDialogExperiencia() {
        View view = getLayoutInflater().inflate(R.layout.dialog_experiencia, null);
        EditText cargo = view.findViewById(R.id.editTextCargo);
        EditText empresa = view.findViewById(R.id.editTextEmpresa);
        EditText periodo = view.findViewById(R.id.editTextPeriodo);
        EditText local = view.findViewById(R.id.editTextLocal);
        EditText descricao = view.findViewById(R.id.editTextDescricao);
        Button btnSalvar = view.findViewById(R.id.buttonSalvarExperiencia);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnSalvar.setOnClickListener(v -> {
            String texto = cargo.getText().toString() + " na " + empresa.getText().toString() + " (" + periodo.getText().toString() + ", " + local.getText().toString() + ")\n" + descricao.getText().toString();
            adicionarItem(layoutExperiencias, texto);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void mostrarDialogFormacao() {
        View view = getLayoutInflater().inflate(R.layout.dialog_formacao, null);
        EditText curso = view.findViewById(R.id.editTextCurso);
        EditText instituicao = view.findViewById(R.id.editTextInstituicao);
        EditText anoInicio = view.findViewById(R.id.editTextAnoInicio);
        EditText anoConclusao = view.findViewById(R.id.editTextAnoConclusao);
        EditText descricao = view.findViewById(R.id.editTextDescricao);
        Button btnSalvar = view.findViewById(R.id.buttonSalvarFormacao);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnSalvar.setOnClickListener(v -> {
            String texto = curso.getText().toString() + " - " + instituicao.getText().toString() +
                    " (" + anoInicio.getText().toString() + "–" + anoConclusao.getText().toString() + ")\n" +
                    descricao.getText().toString();
            adicionarItem(layoutFormacoes, texto);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void adicionarItem(LinearLayout layout, String texto) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(0, 10, 0, 10);

        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        tv.setTextSize(16);

        Button btnRemover = new Button(this);
        btnRemover.setText("Remover");
        btnRemover.setOnClickListener(v -> layout.removeView(itemLayout));

        itemLayout.addView(tv);
        itemLayout.addView(btnRemover);

        layout.addView(itemLayout);
    }

}
