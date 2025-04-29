package com.example.cardstackview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CriarVagaActivity extends AppCompatActivity {

    private static final int REQUEST_PREVIEW = 1001;
    private TextInputEditText edtTituloVaga, edtDescricaoVaga, edtLocalizacao, edtSalario, edtRequisitos;
    private Spinner spinnerNivelExperiencia, spinnerTipoContrato, spinnerAreaAtuacao;
    private MaterialButton btnCriarVaga;
    private Vaga vagaParaEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_vaga_layout);

        inicializarComponentes();
        configurarSpinners();
        configurarListeners();

        if (getIntent() != null) {
            if (getIntent().hasExtra(Constants.EXTRA_VAGA_EDITAR)) {
                vagaParaEditar = (Vaga) getIntent().getSerializableExtra(Constants.EXTRA_VAGA_EDITAR);
                preencherCamposParaEdicao(vagaParaEditar);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.hasExtra(Constants.EXTRA_VAGA_EDITAR)) {
            vagaParaEditar = (Vaga) intent.getSerializableExtra(Constants.EXTRA_VAGA_EDITAR);
            preencherCamposParaEdicao(vagaParaEditar);
        }
    }

    private void inicializarComponentes() {
        edtTituloVaga = findViewById(R.id.edtTituloVaga);
        edtDescricaoVaga = findViewById(R.id.edtDescricaoVaga);
        edtLocalizacao = findViewById(R.id.edtLocalizacao);
        edtSalario = findViewById(R.id.edtSalario);
        edtRequisitos = findViewById(R.id.edtRequisitos);

        spinnerNivelExperiencia = findViewById(R.id.spinnerNivelExperiencia);
        spinnerTipoContrato = findViewById(R.id.spinnerTipoContrato);
        spinnerAreaAtuacao = findViewById(R.id.spinnerAreaAtuacao);

        btnCriarVaga = findViewById(R.id.btnCriarVaga);
        findViewById(R.id.imgVoltar).setOnClickListener(v -> finish());
    }

    private void configurarSpinners() {
        ArrayAdapter<CharSequence> adapterNivel = ArrayAdapter.createFromResource(this,
                R.array.niveis_experiencia, android.R.layout.simple_spinner_item);
        spinnerNivelExperiencia.setAdapter(adapterNivel);

        ArrayAdapter<CharSequence> adapterContrato = ArrayAdapter.createFromResource(this,
                R.array.tipos_contrato, android.R.layout.simple_spinner_item);
        spinnerTipoContrato.setAdapter(adapterContrato);

        ArrayAdapter<CharSequence> adapterArea = ArrayAdapter.createFromResource(this,
                R.array.areas_atuacao, android.R.layout.simple_spinner_item);
        spinnerAreaAtuacao.setAdapter(adapterArea);
    }

    private void configurarListeners() {
        btnCriarVaga.setOnClickListener(v -> {

            // Pegando os valores dos campos
            String titulo = edtTituloVaga.getText().toString().trim();
            String descricao = edtDescricaoVaga.getText().toString().trim();
            String localizacao = edtLocalizacao.getText().toString().trim();
            String salario = edtSalario.getText().toString().trim();
            String requisitos = edtRequisitos.getText().toString().trim();
            String nivel = spinnerNivelExperiencia.getSelectedItem().toString();
            String contrato = spinnerTipoContrato.getSelectedItem().toString();
            String area = spinnerAreaAtuacao.getSelectedItem().toString();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = user.getUid();

            // Criando o mapa de dados
            Map<String, Object> vaga = new HashMap<>();
            vaga.put("titulo", titulo);
            vaga.put("descricao", descricao);
            vaga.put("localizacao", localizacao);
            vaga.put("salario", salario);
            vaga.put("requisitos", requisitos);
            vaga.put("nivelExperiencia", nivel);
            vaga.put("tipoContrato", contrato);
            vaga.put("areaAtuacao", area);
            vaga.put("uidEmpresa", uid);

            // Salvando no Firestore
            FirebaseFirestore.getInstance().collection("vagas")
                    .add(vaga)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Vaga salva com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erro ao salvar vaga: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }

    private void preencherCamposParaEdicao(Vaga vaga) {
        edtTituloVaga.setText(vaga.getTitulo());
        edtDescricaoVaga.setText(vaga.getDescricao());
        edtLocalizacao.setText(vaga.getLocalizacao());
        edtSalario.setText(vaga.getSalario());
        edtRequisitos.setText(vaga.getRequisitos());

        setarSpinner(spinnerNivelExperiencia, vaga.getNivelExperiencia());
        setarSpinner(spinnerTipoContrato, vaga.getTipoContrato());
        setarSpinner(spinnerAreaAtuacao, vaga.getAreaAtuacao());
    }

    private void setarSpinner(Spinner spinner, String valor) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            int posicao = adapter.getPosition(valor);
            if (posicao >= 0) spinner.setSelection(posicao);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PREVIEW && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra(Constants.EXTRA_VAGA_PUBLICADA)) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.EXTRA_VAGA_PUBLICADA,
                        data.getSerializableExtra(Constants.EXTRA_VAGA_PUBLICADA));
                setResult(RESULT_OK, resultIntent);
            }
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
