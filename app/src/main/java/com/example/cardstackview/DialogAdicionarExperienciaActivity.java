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

public class DialogAdicionarExperienciaActivity extends AppCompatActivity {

    private List<Experiencia> listaExperiencias;
    private ExperienciaAdapter adapterExp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.experiencia_activity_layout); // <-- Use um layout correto para a Activity!

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_experiencia_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaExperiencias = new ArrayList<>();
        adapterExp = new ExperienciaAdapter(listaExperiencias);

        RecyclerView recyclerExp = findViewById(R.id.recyclerExperiencias);
        recyclerExp.setLayoutManager(new LinearLayoutManager(this));
        recyclerExp.setAdapter(adapterExp);

        Button btnAddExp = findViewById(R.id.btnAdicionarExperiencia);
        btnAddExp.setOnClickListener(v -> abrirDialogAdicionarExperiencia());
    }

    private void abrirDialogAdicionarExperiencia() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adicionar_experiencia_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText cargo = dialogView.findViewById(R.id.editTextCargo);
        EditText empresa = dialogView.findViewById(R.id.editTextEmpresa);
        EditText periodo = dialogView.findViewById(R.id.editTextPeriodo);
        EditText local = dialogView.findViewById(R.id.editTextLocal);
        EditText descricao = dialogView.findViewById(R.id.editTextDescricao);
        Button btnSalvar = dialogView.findViewById(R.id.buttonSalvarExperiencia);

        btnSalvar.setOnClickListener(v2 -> {
            Experiencia exp = new Experiencia(
                    cargo.getText().toString(),
                    empresa.getText().toString(),
                    periodo.getText().toString(),
                    local.getText().toString(),
                    descricao.getText().toString()
            );
            listaExperiencias.add(exp);
            adapterExp.notifyItemInserted(listaExperiencias.size() - 1);
            dialog.dismiss();
        });

        dialog.show();
    }
}
