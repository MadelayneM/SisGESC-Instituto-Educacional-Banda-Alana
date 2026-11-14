package com.example.bandaalana.administrador;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bandaalana.administrador.DTOs.AdminOpcao;
import com.example.bandaalana.administrador.DTOs.AdminTurma;
import com.example.bandaalana.administrador.DTOs.CadastroTurma;
import com.example.bandaalana.administrador.Repository.AdminRepository;
import com.example.bandaalana.administrador.Repository.RepositoryCallback;

import java.util.Calendar;
import java.util.List;

// Formulário de turma: cadastrar (sem extra) ou editar (com FormularioAdmin.EXTRA_NOME_TURMA).
//
// Para o back-end:
//   spCurso            AdminRepository.buscarOpcoesCursos       -> FormularioAdmin.preencherSpinner
//   spProfessor        AdminRepository.buscarOpcoesProfessores  (incluir uma opção "Sem professor")
//   spFuncaoProfessor  AdminRepository.buscarOpcoesTiposMonitor
//   btnCriarTurma      montar CadastroTurma -> criarTurma (novo) ou editarTurma (edição)
//   btnExcluir         AdminRepository.excluirTurma(nomeTurma)
//   Edição: AdminRepository.buscarCadastroTurma(nomeTurma) e preencher os campos.
//   Datas na tela são dd/mm/aaaa: converter com FormularioAdmin.dataParaBanco / dataParaTela.
public class activity_admin_nova_turma extends AppCompatActivity {

    // Nome da turma em edição, ou null quando é um cadastro novo
    private String nomeTurmaEdicao;

    // 1. Declare uma variável global no topo da sua Activity
    private CadastroTurma turmaCarregadaParaEdicao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_nova_turma);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nomeTurmaEdicao = getIntent().getStringExtra(FormularioAdmin.EXTRA_NOME_TURMA);
        boolean edicao = nomeTurmaEdicao != null;

        FormularioAdmin.configurarCabecalho(this, edicao ? "Editar turma" : "Nova turma");

        if (edicao) {
            // O nome é a chave da turma no banco e não pode mudar
            findViewById(R.id.etNomeTurma).setEnabled(false);
            ((Button) findViewById(R.id.btnCriarTurma)).setText("Salvar alterações");
            findViewById(R.id.btnExcluir).setVisibility(View.VISIBLE);
        }

        AdminRepository repo = new AdminRepository();

        Spinner spCurso = findViewById(R.id.spCurso);
        Spinner spProfessor = findViewById(R.id.spProfessor);
        Spinner spFuncaoProfessor = findViewById(R.id.spFuncaoProfessor);


        // 1. Carregar spCurso
        repo.buscarOpcoesCursos(new RepositoryCallback<List<AdminOpcao>>() {


            @Override
            public void onSucesso(List<AdminOpcao> resultado) {
                List<AdminOpcao> opcoesCursos = (List<AdminOpcao>) resultado;
                FormularioAdmin.preencherSpinner(spCurso, opcoesCursos);

            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(activity_admin_nova_turma.this, "Erro: " + mensagem, Toast.LENGTH_SHORT).show();

            }

        });
        // 2. Carregar spProfessor
        repo.buscarOpcoesProfessores(new RepositoryCallback<List<AdminOpcao>>() {
            @Override
            public void onSucesso(List<AdminOpcao> opcoesProfessores) {
                FormularioAdmin.preencherSpinner(spProfessor, opcoesProfessores);

                // Próximo passo (ex: carregar spFuncaoProfessor)
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(activity_admin_nova_turma.this, "Erro ao carregar professores: " + mensagem, Toast.LENGTH_SHORT).show();
            }


        });

        // 3. Carregar spFuncaoProfessor
        repo.buscarOpcoesTiposMonitor(new RepositoryCallback<List<AdminOpcao>>() {
            @Override
            public void onSucesso(List<AdminOpcao> opcoesFuncoes) {
                FormularioAdmin.preencherSpinner(spFuncaoProfessor, opcoesFuncoes);

                // Se for edição, buscar os dados da turma após carregar todos os spinners
                if (edicao) {
                    repo.buscarCadastroTurma(nomeTurmaEdicao, new RepositoryCallback<CadastroTurma>() {
                        @Override
                        public void onSucesso(CadastroTurma turma) {
                            // ADICIONE ESTA LINHA PARA GUARDAR A TURMA AQUI:
                            turmaCarregadaParaEdicao = turma;
                            ((EditText) findViewById(R.id.etNomeTurma)).setText(turma.getNomeTurma());
                            ((EditText) findViewById(R.id.etVagas)).setText(String.valueOf(turma.getVagas()));

                            if (turma.getDataAula() != null) {
                                ((EditText) findViewById(R.id.etDataAula)).setText(FormularioAdmin.dataParaTela(turma.getDataAula()));
                            }

                            ((EditText) findViewById(R.id.etHorarioInicio)).setText(turma.getHorarioInicio());
                            ((EditText) findViewById(R.id.etHorarioFim)).setText(turma.getHorarioFim());
                        }

                        @Override
                        public void onErro(String mensagem) {
                            Toast.makeText(activity_admin_nova_turma.this, "Erro ao carregar turma: " + mensagem, Toast.LENGTH_SHORT).show();

                        }


                    });
                }
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(activity_admin_nova_turma.this, "Erro ao carregar funções: " + mensagem, Toast.LENGTH_SHORT).show();

            }


        });


        //Configurar o DatePicker para etDataAula

        EditText etDataAula = findViewById(R.id.etDataAula);
        etDataAula.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            int ano = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                // Formata para o padrão visual dd/MM/yyyy
                String dataFormatada = String.format("%02d/%02d/%d", dayOfMonth, (month + 1), year);
                etDataAula.setText(dataFormatada);
            }, ano, mes, dia);

            datePickerDialog.show();
        });


        //
        EditText etHorarioInicio = findViewById(R.id.etHorarioInicio);
        etHorarioInicio.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            int hora = calendario.get(Calendar.HOUR_OF_DAY);
            int minuto = calendario.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                String horaFormatada = String.format("%02d:%02d", hourOfDay, minute);
                etHorarioInicio.setText(horaFormatada);
            }, hora, minuto, true); // true para formato de 24 horas

            timePickerDialog.show();
        });

        EditText etHorarioFim = findViewById(R.id.etHorarioFim);
        etHorarioFim.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            int hora = calendario.get(Calendar.HOUR_OF_DAY);
            int minuto = calendario.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                String horaFormatada = String.format("%02d:%02d", hourOfDay, minute);
                etHorarioFim.setText(horaFormatada);
            }, hora, minuto, true);

            timePickerDialog.show();
        });


        // ==========================================
// ➔ CÓDIGO DO BOTÃO SALVAR (CRIAR OU EDITAR)
// ==========================================
        Button btnCriarTurma = findViewById(R.id.btnCriarTurma);
        btnCriarTurma.setOnClickListener(v -> {
            String nomeTurma = ((EditText) findViewById(R.id.etNomeTurma)).getText().toString().trim();
            String horarioInicioTela = ((EditText) findViewById(R.id.etHorarioInicio)).getText().toString().trim();
            String horarioFimTela = ((EditText) findViewById(R.id.etHorarioFim)).getText().toString().trim();
            String dataTela = ((EditText) findViewById(R.id.etDataAula)).getText().toString().trim();

            int vagas;
            try {
                vagas = Integer.parseInt(((EditText) findViewById(R.id.etVagas)).getText().toString().trim());
            } catch (NumberFormatException e) {
                vagas = 0;
            }

            String dataBanco = FormularioAdmin.dataParaBanco(dataTela);

            AdminOpcao cursoSelecionado = (AdminOpcao) spCurso.getSelectedItem();
            AdminOpcao professorSelecionado = (AdminOpcao) spProfessor.getSelectedItem();
            AdminOpcao funcaoSelecionada = (AdminOpcao) spFuncaoProfessor.getSelectedItem();

            String cpfProf = (professorSelecionado != null) ? professorSelecionado.getValor() : null;
            String funcaoMon = (funcaoSelecionada != null) ? funcaoSelecionada.getValor() : null;
            String codCurso = (cursoSelecionado != null) ? cursoSelecionado.getValor() : null;

            // AQUI ENTRA A LÓGICA CORRETA DE EDIÇÃO VS CRIAÇÃO
            if (edicao) {
                // Se for edição, usa a instância que foi carregada do banco
                if (turmaCarregadaParaEdicao == null) {
                    Toast.makeText(activity_admin_nova_turma.this, "Aguarde o carregamento da turma...", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Atualiza os dados da instância existente (para não perder o status de isCarregadoDoBanco)
                turmaCarregadaParaEdicao.setNomeTurma(nomeTurma);
                turmaCarregadaParaEdicao.setVagas(vagas);
                turmaCarregadaParaEdicao.setDataAula(dataBanco);
                turmaCarregadaParaEdicao.setHorarioInicio(horarioInicioTela);
                turmaCarregadaParaEdicao.setHorarioFim(horarioFimTela);
                if (codCurso != null) turmaCarregadaParaEdicao.setCodigoCurso(codCurso);
                turmaCarregadaParaEdicao.setProfessor(cpfProf, funcaoMon);

                repo.editarTurma(turmaCarregadaParaEdicao, new RepositoryCallback<Void>() {
                    @Override
                    public void onSucesso(Void resultado) {
                        Toast.makeText(activity_admin_nova_turma.this, "Turma atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onErro(String mensagem) {
                        Toast.makeText(activity_admin_nova_turma.this, "Erro ao atualizar: " + mensagem, Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                // Se for NOVO cadastro (Criação)
                CadastroTurma cadastro = new CadastroTurma();
                cadastro.setNomeTurma(nomeTurma);
                cadastro.setVagas(vagas);
                cadastro.setDataAula(dataBanco);
                cadastro.setHorarioInicio(horarioInicioTela);
                cadastro.setHorarioFim(horarioFimTela);
                if (codCurso != null) cadastro.setCodigoCurso(codCurso);
                cadastro.setProfessor(cpfProf, funcaoMon);

                repo.criarTurma(cadastro, new RepositoryCallback<Void>() {
                    @Override
                    public void onSucesso(Void resultado) {
                        Toast.makeText(activity_admin_nova_turma.this, "Turma criada com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onErro(String mensagem) {
                        Toast.makeText(activity_admin_nova_turma.this, "Erro ao criar turma: " + mensagem, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        // ==========================================
// ➔ PASSO 4: BOTÃO EXCLUIR COM CONFIRMAÇÃO
// ==========================================
        Button btnExcluir = findViewById(R.id.btnExcluir);
        btnExcluir.setOnClickListener(v -> {
            // Validação de segurança para garantir que temos o objeto da turma carregado
            if (turmaCarregadaParaEdicao == null || turmaCarregadaParaEdicao.getNomeTurma() == null) {
                Toast.makeText(activity_admin_nova_turma.this, "Turma não identificada para exclusão.", Toast.LENGTH_SHORT).show();
                return;
            }

            String nomeTurmaParaExcluir = turmaCarregadaParaEdicao.getNomeTurma();

            // Cria um alerta de confirmação antes de excluir
            new AlertDialog.Builder(this)
                    .setTitle("Excluir Turma")
                    .setMessage("Tem certeza que deseja excluir a turma \"" + nomeTurmaParaExcluir + "\"? Esta ação não poderá ser desfeita.")
                    .setPositiveButton("Sim, excluir", (dialog, which) -> {
                        // Chama o método de exclusão do repositório
                        repo.excluirTurma(nomeTurmaParaExcluir, new RepositoryCallback<Void>() {
                            @Override
                            public void onSucesso(Void resultado) {
                                Toast.makeText(activity_admin_nova_turma.this, "Turma excluída com sucesso!", Toast.LENGTH_SHORT).show();
                                finish(); // Fecha a tela e retorna à lista anterior
                            }

                            @Override
                            public void onErro(String mensagem) {
                                // Exibe o erro do repositório (ex: se houver alunos matriculados na turma)
                                Toast.makeText(activity_admin_nova_turma.this, "Erro ao excluir: " + mensagem, Toast.LENGTH_LONG).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

    } // Fim do onCreate


    }