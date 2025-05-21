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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EdicaoPerfilPessoaActivity extends AppCompatActivity {

    ImageView imgVoltarPerfilPessoa;

    RecyclerView recyclerCertificados, recyclerFormacoes, recyclerExperiencias;
    List<Certificado> listaCertificados = new ArrayList<>();
    List<Formacao> listaFormacoes = new ArrayList<>();
    List<Experiencia> listaExperiencias = new ArrayList<>();
    CertificadoAdapter certificadoAdapter;
    FormacaoAdapter formacaoAdapter;
    ExperienciaAdapter experienciaAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edicao_perfil_pessoa_layout);

        recyclerCertificados = findViewById(R.id.recyclerCertificados);
        recyclerFormacoes = findViewById(R.id.recyclerFormacoes);
        recyclerExperiencias = findViewById(R.id.recyclerExperiencias);

        recyclerCertificados.setLayoutManager(new LinearLayoutManager(this));
        recyclerFormacoes.setLayoutManager(new LinearLayoutManager(this));
        recyclerExperiencias.setLayoutManager(new LinearLayoutManager(this));

        certificadoAdapter = new CertificadoAdapter(listaCertificados);
        formacaoAdapter = new FormacaoAdapter(listaFormacoes);
        experienciaAdapter = new ExperienciaAdapter(listaExperiencias);

        recyclerCertificados.setAdapter(certificadoAdapter);
        recyclerFormacoes.setAdapter(formacaoAdapter);
        recyclerExperiencias.setAdapter(experienciaAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.perfil), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Botão de adicionar certificado
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
                EditText editDuracao = dialogView.findViewById(R.id.editTextDuracao);
                EditText editDescricao = dialogView.findViewById(R.id.editTextDescricao);

                btnSalvar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nomeCertificado = editNome.getText().toString();
                        String instituicao = editInstituicao.getText().toString();
                        String ano = editAno.getText().toString();
                        String duracao = editDuracao.getText().toString();
                        String descricao = editDescricao.getText().toString();

                        listaCertificados.add(new Certificado(nomeCertificado, instituicao, ano, duracao, descricao));
                        certificadoAdapter.notifyItemInserted(listaCertificados.size() - 1);

                        alertDialog.dismiss();
                    }
                });
            }
        });

        Button btnFormacao = findViewById(R.id.btnAdicionarFormacao);
        btnFormacao.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_adicionar_formacao_layout, null);
            AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
            dialog.show();

            EditText curso = dialogView.findViewById(R.id.editTextCurso);
            EditText inst = dialogView.findViewById(R.id.editTextInstituicao);
            EditText anoInicio = dialogView.findViewById(R.id.editTextAnoInicio);
            EditText anoConclusao = dialogView.findViewById(R.id.editTextAnoConclusao);
            EditText desc = dialogView.findViewById(R.id.editTextDescricao);
            Button salvar = dialogView.findViewById(R.id.buttonSalvarFormacao);

            salvar.setOnClickListener(v2 -> {
                listaFormacoes.add(new Formacao(
                        curso.getText().toString(),
                        inst.getText().toString(),
                        anoInicio.getText().toString(),
                        anoConclusao.getText().toString(),
                        desc.getText().toString()
                ));
                formacaoAdapter.notifyItemInserted(listaFormacoes.size() - 1);
                dialog.dismiss();
            });
        });

        Button btnExperiencia = findViewById(R.id.btnAdicionarExperiencia);
        btnExperiencia.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_adicionar_experiencia_layout, null);
            AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
            dialog.show();

            EditText cargo = dialogView.findViewById(R.id.editTextCargo);
            EditText empresa = dialogView.findViewById(R.id.editTextEmpresa);
            EditText periodo = dialogView.findViewById(R.id.editTextPeriodo);
            EditText local = dialogView.findViewById(R.id.editTextLocal);
            EditText desc = dialogView.findViewById(R.id.editTextDescricao);
            Button salvar = dialogView.findViewById(R.id.buttonSalvarExperiencia);

            salvar.setOnClickListener(v2 -> {
                listaExperiencias.add(new Experiencia(
                        cargo.getText().toString(),
                        empresa.getText().toString(),
                        periodo.getText().toString(),
                        local.getText().toString(),
                        desc.getText().toString()
                ));
                experienciaAdapter.notifyItemInserted(listaExperiencias.size() - 1);
                dialog.dismiss();
            });
        });



        // ✅ Correto: função de voltar fora do listener anterior
        imgVoltarPerfilPessoa = findViewById(R.id.imgVoltarPerfilPessoa);
        imgVoltarPerfilPessoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PerfilActivity.class));
                finish();
            }
        });
    }
}