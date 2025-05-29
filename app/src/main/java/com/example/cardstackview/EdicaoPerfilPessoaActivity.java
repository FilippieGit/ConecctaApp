package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdicaoPerfilPessoaActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uid;

    private LinearLayout layoutCertificados, layoutExperiencias, layoutFormacoes;
    private Button btnAddCertificado, btnAddExperiencia, btnAddFormacao, btnSalvarPerfil;
    private ImageView imgVoltarPerfilPessoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edicao_perfil_pessoa_layout);

        // 1) Init Firebase
        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();
        uid   = mAuth.getCurrentUser().getUid();

        // 2) Bind views
        layoutCertificados    = findViewById(R.id.layoutCertificados);
        layoutExperiencias    = findViewById(R.id.layoutExperiencias);
        layoutFormacoes       = findViewById(R.id.layoutFormacoes);

        btnAddCertificado     = findViewById(R.id.btnAddCertificado);
        btnAddExperiencia     = findViewById(R.id.btnAddExperiencia);
        btnAddFormacao        = findViewById(R.id.btnAddFormacao);
        btnSalvarPerfil       = findViewById(R.id.btnSalvarPerfil);
        imgVoltarPerfilPessoa = findViewById(R.id.imgVoltarPerfilPessoa);

        // 3) Listeners
        btnAddCertificado.setOnClickListener(v -> mostrarDialogCertificado());
        btnAddExperiencia.setOnClickListener(v -> mostrarDialogExperiencia());
        btnAddFormacao.setOnClickListener(v -> mostrarDialogFormacao());
        btnSalvarPerfil.setOnClickListener(v -> saveProfile());
        imgVoltarPerfilPessoa.setOnClickListener(v -> finish());

        // 4) Load existing data

    }

    private void loadProfile() {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    List<String> certs = (List<String>) doc.get("certificados");
                    List<String> exps  = (List<String>) doc.get("experiencias");
                    List<String> forms = (List<String>) doc.get("formacoes");
                    if (certs  != null) for (String s : certs) adicionarItem(layoutCertificados, s);
                    if (exps   != null) for (String s : exps) adicionarItem(layoutExperiencias, s);
                    if (forms  != null) for (String s : forms) adicionarItem(layoutFormacoes, s);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao carregar perfil: "+e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    private void saveProfile() {
        List<String> certs = new ArrayList<>();
        for (int i = 0; i < layoutCertificados.getChildCount(); i++) {
            LinearLayout item = (LinearLayout) layoutCertificados.getChildAt(i);
            TextView tv = (TextView) item.getChildAt(0);
            certs.add(tv.getText().toString());
        }
        List<String> exps = new ArrayList<>();
        for (int i = 0; i < layoutExperiencias.getChildCount(); i++) {
            LinearLayout item = (LinearLayout) layoutExperiencias.getChildAt(i);
            TextView tv = (TextView) item.getChildAt(0);
            exps.add(tv.getText().toString());
        }
        List<String> forms = new ArrayList<>();
        for (int i = 0; i < layoutFormacoes.getChildCount(); i++) {
            LinearLayout item = (LinearLayout) layoutFormacoes.getChildAt(i);
            TextView tv = (TextView) item.getChildAt(0);
            forms.add(tv.getText().toString());
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("certificados", certs);
        updates.put("experiencias", exps);
        updates.put("formacoes", forms);

        db.collection("users")
                .document(uid)
                .update(updates)
                .addOnSuccessListener(a ->
                        Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao salvar perfil: "+e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    private void mostrarDialogCertificado() {
        View view = getLayoutInflater().inflate(R.layout.dialog_adicionar_certificado_layout, null);
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
        View view = getLayoutInflater().inflate(R.layout.dialog_adicionar_experiencia_layout, null);
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
        View view = getLayoutInflater().inflate(R.layout.dialog_adicionar_formacao_layout, null);
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
