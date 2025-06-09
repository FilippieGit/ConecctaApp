package com.example.cardstackview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class EdicaoPerfilPessoaActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uid;

    private TextView perfilNome; //adiocionei essa parte CHAT

    private LinearLayout layoutCertificados, layoutExperiencias, layoutFormacoes;
    private Button btnAddCertificado, btnAddExperiencia, btnAddFormacao, btnSalvarPerfil;
    private ImageView imgVoltarPerfilPessoa;

    private EditText edtDescricao, edtTelefone, edtEmail, edtSetor;

    // Botão de editar foto agora é um Button comum (conforme XML)
    private Button btnEditPhoto;


    // Para armazenar o Base64 da imagem selecionada
    private String imagemPerfilBase64 = null;

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edicao_perfil_pessoa_layout);

        // 1) Init Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        // 2) Bind das views
        layoutCertificados = findViewById(R.id.layoutCertificados);
        layoutExperiencias = findViewById(R.id.layoutExperiencias);
        layoutFormacoes = findViewById(R.id.layoutFormacoes);


        perfilNome = findViewById(R.id.perfilNome); // Tambem ADICIONEI ESSA PARTE CHAT


        btnAddCertificado = findViewById(R.id.btnAddCertificado);
        btnAddExperiencia = findViewById(R.id.btnAddExperiencia);
        btnAddFormacao = findViewById(R.id.btnAddFormacao);
        btnSalvarPerfil = findViewById(R.id.btnSalvarPerfil);
        imgVoltarPerfilPessoa = findViewById(R.id.imgVoltarPerfilPessoa);

        edtDescricao = ((com.google.android.material.textfield.TextInputLayout) findViewById(R.id.textField))
                .getEditText();
        edtTelefone = ((com.google.android.material.textfield.TextInputLayout) findViewById(R.id.edicaoPerfilTelefone))
                .getEditText();
        edtEmail = ((com.google.android.material.textfield.TextInputLayout) findViewById(R.id.edicaoPerfilEmail))
                .getEditText();
        edtSetor = ((com.google.android.material.textfield.TextInputLayout) findViewById(R.id.edicaoPerfilArea))
                .getEditText();


        // 3) Listeners dos botões existentes
        btnAddCertificado.setOnClickListener(v -> mostrarDialogCertificado());
        btnAddExperiencia.setOnClickListener(v -> mostrarDialogExperiencia());
        btnAddFormacao.setOnClickListener(v -> mostrarDialogFormacao());
        btnSalvarPerfil.setOnClickListener(v -> saveProfile());
        imgVoltarPerfilPessoa.setOnClickListener(v -> finish());



        // Se quiser carregar dados existentes (incluindo imagem), chame loadProfile() aqui:
        loadProfile();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    // 1) Pegar InputStream da URI
                    InputStream is = getContentResolver().openInputStream(imageUri);
                    // 2) Converter para Bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();

                    // 3) Converter Bitmap para Base64 (PNG)
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] byteArray = baos.toByteArray();
                    String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    // 4) Guardar Base64 em variável membro
                    this.imagemPerfilBase64 = base64Image;

                    // 5) Exibir a imagem no CircleImageView


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Erro ao ler imagem: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadProfile() {




        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;




                    String nomeSalvo = doc.getString("nome");
                    perfilNome.setText(nomeSalvo != null ? nomeSalvo : "Não informado");

                    List<String> certs = (List<String>) doc.get("certificados");
                    List<String> exps = (List<String>) doc.get("experiencias");
                    List<String> forms = (List<String>) doc.get("formacoes");
                    if (certs != null) for (String s : certs) adicionarItem(layoutCertificados, s);
                    if (exps != null) for (String s : exps) adicionarItem(layoutExperiencias, s);
                    if (forms != null) for (String s : forms) adicionarItem(layoutFormacoes, s);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao carregar perfil: " + e.getMessage(),
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

        // Mapa com os dados textuais
        Map<String, String> dados = new HashMap<>();
        dados.put("uid", uid);
        dados.put("telefone", edtTelefone.getText().toString());
        dados.put("descricao", edtDescricao.getText().toString());
        dados.put("setor", edtSetor.getText().toString());
        dados.put("formacao_academica", String.join(" | ", forms));
        dados.put("experiencia_profissional", String.join(" | ", exps));
        dados.put("certificados", String.join(" | ", certs));

        // Envio via POST
        OkHttpClient client = new OkHttpClient();
        okhttp3.FormBody.Builder formBuilder = new okhttp3.FormBody.Builder();

        // Adiciona campos textuais
        for (Map.Entry<String, String> entry : dados.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        // Adiciona o Base64 da imagem, se houver sido selecionada
        if (imagemPerfilBase64 != null) {
            formBuilder.add("imagem_perfil", imagemPerfilBase64);
        }

        okhttp3.RequestBody body = formBuilder.build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Api.URL_UPDATE_USER) // seu endpoint PHP
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Perfil atualizado!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), PerfilActivity.class));
                    finish();
                });
            }
        });
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
            String texto = nome.getText().toString()
                    + " - " + instituicao.getText().toString()
                    + " (" + ano.getText().toString() + ")";
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
            String texto = cargo.getText().toString()
                    + " na " + empresa.getText().toString()
                    + " (" + periodo.getText().toString()
                    + ", " + local.getText().toString() + ")\n"
                    + descricao.getText().toString();
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
            String texto = curso.getText().toString()
                    + " - " + instituicao.getText().toString()
                    + " (" + anoInicio.getText().toString()
                    + "–" + anoConclusao.getText().toString() + ")\n"
                    + descricao.getText().toString();
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
