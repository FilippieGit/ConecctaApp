    package com.example.cardstackview;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    import com.google.android.material.button.MaterialButton;
    import com.google.android.material.chip.Chip;
    import com.google.android.material.chip.ChipGroup;
    import com.google.android.material.textfield.TextInputEditText;

    import java.util.ArrayList;
    import java.util.List;

    public class CriarVagaActivity extends AppCompatActivity {

        private ChipGroup chipGroupHabilidades;
        private TextView tvExpandir;
        private long userId; // Adicione esta variável

        private TextInputEditText edtTituloVaga, edtDescricaoVaga, edtLocalizacao,
                edtSalario, edtRequisitos, edtBeneficios;
        private Spinner spinnerNivelExperiencia, spinnerTipoContrato, spinnerAreaAtuacao;

        private final String[] habilidadesArray = {
                // Técnicas de programação
                "Java", "Kotlin", "Android SDK", "Firebase", "Git", "SQL", "REST APIs", "Docker", "CI/CD",
                // Design e experiência do usuário
                "UI/UX Design", "Adobe XD", "Figma", "Photoshop", "Prototipagem",
                // Metodologias ágeis e gestão
                "Scrum", "Kanban", "Gestão de Projetos", "JIRA", "Trello",
                // Comunicação e trabalho em equipe
                "Comunicação Efetiva", "Trabalho em Equipe", "Liderança", "Negociação",
                // Outras habilidades técnicas
                "Cloud Computing (AWS, Azure, GCP)", "Testes Automatizados", "Análise de Dados", "Machine Learning",
                // Competências pessoais
                "Resolução de Problemas", "Pensamento Crítico", "Adaptabilidade", "Criatividade",
                // Idiomas
                "Inglês Avançado", "Espanhol Básico", "Francês Intermediário"
        };

        private final int maxLinesCollapsed = 2;
        private boolean expanded = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.criar_vaga_layout);

            // Obter o ID do usuário da intent
            userId = getIntent().getLongExtra("USER_ID", -1);
            if (userId == -1) {
                Toast.makeText(this, "Erro: Usuário não identificado", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            inicializarComponentes();
            carregarHabilidades();
            configurarExpansaoChipGroup();
        }

        private void inicializarComponentes() {
            // Views
            chipGroupHabilidades = findViewById(R.id.chipGroupHabilidades);
            tvExpandir = findViewById(R.id.tvExpandir);

            edtTituloVaga = findViewById(R.id.edtTituloVaga);
            edtDescricaoVaga = findViewById(R.id.edtDescricaoVaga);
            edtLocalizacao = findViewById(R.id.edtLocalizacao);
            edtSalario = findViewById(R.id.edtSalario);
            edtRequisitos = findViewById(R.id.edtRequisitos);
            edtBeneficios = findViewById(R.id.edtBeneficios);

            spinnerNivelExperiencia = findViewById(R.id.spinnerNivelExperiencia);
            spinnerTipoContrato = findViewById(R.id.spinnerTipoContrato);
            spinnerAreaAtuacao = findViewById(R.id.spinnerAreaAtuacao);

            configurarSpinners();

            MaterialButton btnPreVisualizar = findViewById(R.id.btnCriarVaga);
            btnPreVisualizar.setOnClickListener(v -> enviarParaPreVisualizacao());

            findViewById(R.id.imgVoltar).setOnClickListener(v -> finish());
        }

        private void configurarSpinners() {
            ArrayAdapter<CharSequence> nivelAdapter = ArrayAdapter.createFromResource(this,
                    R.array.niveis_experiencia, android.R.layout.simple_spinner_item);
            nivelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerNivelExperiencia.setAdapter(nivelAdapter);

            ArrayAdapter<CharSequence> contratoAdapter = ArrayAdapter.createFromResource(this,
                    R.array.tipos_contrato, android.R.layout.simple_spinner_item);
            contratoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipoContrato.setAdapter(contratoAdapter);

            ArrayAdapter<CharSequence> areaAdapter = ArrayAdapter.createFromResource(this,
                    R.array.areas_atuacao, android.R.layout.simple_spinner_item);
            areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAreaAtuacao.setAdapter(areaAdapter);
        }

        private void carregarHabilidades() {
            chipGroupHabilidades.removeAllViews();
            for (String habilidade : habilidadesArray) {
                Chip chip = new Chip(this);
                chip.setText(habilidade);
                chip.setCheckable(true);
                chip.setChipBackgroundColorResource(R.color.chip_background);
                chipGroupHabilidades.addView(chip);
            }
            // Ajusta altura inicial após adicionar chips
            chipGroupHabilidades.post(() -> setChipGroupHeight(expanded));
        }

        private void configurarExpansaoChipGroup() {
            tvExpandir.setOnClickListener(v -> {
                expanded = !expanded;
                setChipGroupHeight(expanded);
            });
        }

        private void setChipGroupHeight(boolean expanded) {
            if (expanded) {
                chipGroupHabilidades.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                tvExpandir.setText("Mostrar menos");
            } else {
                int chipHeight = 0;
                if (chipGroupHabilidades.getChildCount() > 0) {
                    View chip = chipGroupHabilidades.getChildAt(0);
                    chipHeight = chip.getHeight() + chipGroupHabilidades.getChipSpacingVertical();
                }
                if (chipHeight == 0) {
                    chipHeight = (int) (48 * getResources().getDisplayMetrics().density);
                }
                int height = chipHeight * maxLinesCollapsed + chipGroupHabilidades.getPaddingTop() + chipGroupHabilidades.getPaddingBottom();
                chipGroupHabilidades.getLayoutParams().height = height;
                tvExpandir.setText("Mostrar mais");
            }
            chipGroupHabilidades.requestLayout();
        }

        private void enviarParaPreVisualizacao() {
            // Obter valores dos campos
            String titulo = edtTituloVaga.getText().toString().trim();
            String descricao = edtDescricaoVaga.getText().toString().trim();
            String localizacao = edtLocalizacao.getText().toString().trim();
            String salario = edtSalario.getText().toString().trim();
            String requisitos = edtRequisitos.getText().toString().trim();
            String beneficios = edtBeneficios.getText().toString().trim();
            String nivel = spinnerNivelExperiencia.getSelectedItem().toString();
            String contrato = spinnerTipoContrato.getSelectedItem().toString();
            String area = spinnerAreaAtuacao.getSelectedItem().toString();

            // Validar campos obrigatórios
            if (titulo.isEmpty() || descricao.isEmpty() || localizacao.isEmpty() ||
                    requisitos.isEmpty() || beneficios.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obter habilidades selecionadas
            List<String> habilidadesDesejaveis = obterHabilidadesSelecionadas();

            // Verificar se ao menos uma habilidade foi selecionada
            if (habilidadesDesejaveis.isEmpty()) {
                Toast.makeText(this, "Selecione ao menos uma habilidade desejável", Toast.LENGTH_SHORT).show();
                return;
            }

            // Criar objeto Vaga
            int empresaId = 1; // ID da empresa (ajustar conforme necessário)
            String ramo = "Tecnologia"; // Definir ramo ou adicionar campo no formulário
            String vinculo = contrato;

            String habilidadesStr = String.join(", ", habilidadesDesejaveis);
            Vagas vaga = new Vagas(
                    titulo, descricao, localizacao, salario, requisitos,
                    nivel, contrato, area, beneficios, vinculo, ramo,
                    userId, // Passando o userId como id_empresa (ou criar campo separado se necessário)
                    habilidadesStr
            );

            // Enviar para pré-visualização
            Intent intent = new Intent(this, VagaPreVisualizacaoActivity.class);
            intent.putExtra(Constants.EXTRA_VAGA, vaga);
            intent.putExtra("USER_ID", userId); // Passando o userId novamente
            startActivity(intent);
        }


        private List<String> obterHabilidadesSelecionadas() {
            List<String> habilidades = new ArrayList<>();
            for (int i = 0; i < chipGroupHabilidades.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupHabilidades.getChildAt(i);
                if (chip.isChecked()) {
                    habilidades.add(chip.getText().toString());
                }
            }
            return habilidades;
        }
    }
