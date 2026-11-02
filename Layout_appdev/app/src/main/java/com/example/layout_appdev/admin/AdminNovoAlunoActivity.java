package com.example.layout_appdev.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.model.Aluno;
import com.example.layout_appdev.admin.model.Aluno.Status;
import com.example.layout_appdev.admin.model.Turma;
import com.example.layout_appdev.admin.util.AdminUi;
import com.example.layout_appdev.admin.util.MaskWatcher;
import com.example.layout_appdev.admin.util.MockData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminNovoAlunoActivity extends AppCompatActivity {

    private ImageView ivFoto;
    private EditText etNome, etNascimento, etCpf, etTelefone, etEmail,
            etResponsavel, etTelefoneResp, etInstrumento;
    private Spinner spTurma;
    private TextView statusAtivo, statusPendente, statusInativo;

    private String fotoUri = null;
    private Status status = Status.ATIVO;
    private int nascAno = -1, nascMes, nascDia;

    // Escolher foto da galeria
    private final ActivityResultLauncher<String> escolherFoto = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    fotoUri = uri.toString();
                    ivFoto.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_novo_aluno);
        AdminUi.aplicarInsets(this);
        AdminUi.configurarCabecalho(this, "Novo Aluno");

        ivFoto = findViewById(R.id.ivFoto);
        etNome = findViewById(R.id.etNome);
        etNascimento = findViewById(R.id.etNascimento);
        etCpf = findViewById(R.id.etCpf);
        etTelefone = findViewById(R.id.etTelefone);
        etEmail = findViewById(R.id.etEmail);
        etResponsavel = findViewById(R.id.etResponsavel);
        etTelefoneResp = findViewById(R.id.etTelefoneResp);
        etInstrumento = findViewById(R.id.etInstrumento);
        spTurma = findViewById(R.id.spTurma);
        statusAtivo = findViewById(R.id.statusAtivo);
        statusPendente = findViewById(R.id.statusPendente);
        statusInativo = findViewById(R.id.statusInativo);

        // Máscaras
        etCpf.addTextChangedListener(new MaskWatcher(etCpf, "###.###.###-##"));
        etTelefone.addTextChangedListener(new MaskWatcher(etTelefone, "(##) ####-####", "(##) #####-####"));
        etTelefoneResp.addTextChangedListener(new MaskWatcher(etTelefoneResp, "(##) ####-####", "(##) #####-####"));

        // Turmas disponíveis
        List<String> turmas = new ArrayList<>();
        turmas.add("Selecionar turma...");
        for (Turma t : MockData.turmas()) turmas.add(t.getNome());
        ArrayAdapter<String> adapterTurmas =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, turmas);
        adapterTurmas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTurma.setAdapter(adapterTurmas);

        // Status da matrícula
        selecionarStatus(Status.ATIVO);
        statusAtivo.setOnClickListener(v -> selecionarStatus(Status.ATIVO));
        statusPendente.setOnClickListener(v -> selecionarStatus(Status.PENDENTE));
        statusInativo.setOnClickListener(v -> selecionarStatus(Status.INATIVO));

        // Foto e data
        findViewById(R.id.flFoto).setOnClickListener(v -> escolherFoto.launch("image/*"));
        etNascimento.setOnClickListener(v -> abrirCalendario());

        findViewById(R.id.btnCadastrar).setOnClickListener(v -> cadastrar());
    }

    private void selecionarStatus(Status novo) {
        status = novo;
        statusAtivo.setSelected(novo == Status.ATIVO);
        statusPendente.setSelected(novo == Status.PENDENTE);
        statusInativo.setSelected(novo == Status.INATIVO);
    }

    private void abrirCalendario() {
        Calendar c = Calendar.getInstance();
        int ano = nascAno > 0 ? nascAno : c.get(Calendar.YEAR) - 10;
        int mes = nascAno > 0 ? nascMes : c.get(Calendar.MONTH);
        int dia = nascAno > 0 ? nascDia : c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            nascAno = y;
            nascMes = m;
            nascDia = d;
            etNascimento.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y));
            etNascimento.setError(null);
        }, ano, mes, dia);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private int idade() {
        Calendar hoje = Calendar.getInstance();
        int idade = hoje.get(Calendar.YEAR) - nascAno;
        if (hoje.get(Calendar.MONTH) < nascMes
                || (hoje.get(Calendar.MONTH) == nascMes && hoje.get(Calendar.DAY_OF_MONTH) < nascDia)) {
            idade--;
        }
        return idade;
    }

    private boolean erro(EditText campo, String mensagem) {
        campo.setError(mensagem);
        campo.requestFocus();
        return false;
    }

    private boolean validar() {
        String nome = etNome.getText().toString().trim();
        if (nome.isEmpty()) return erro(etNome, "Informe o nome completo");
        if (nascAno < 0) return erro(etNascimento, "Informe a data de nascimento");

        String cpf = AdminUi.soDigitos(etCpf.getText().toString());
        if (!cpf.isEmpty() && cpf.length() != 11) return erro(etCpf, "CPF incompleto");

        String tel = AdminUi.soDigitos(etTelefone.getText().toString());
        if (!tel.isEmpty() && tel.length() < 10) return erro(etTelefone, "Telefone incompleto");

        String email = etEmail.getText().toString().trim();
        if (!email.isEmpty() && !AdminUi.emailValido(email)) return erro(etEmail, "E-mail inválido");

        // Menores de 18 anos precisam de responsável
        if (idade() < 18) {
            if (etResponsavel.getText().toString().trim().isEmpty()) {
                return erro(etResponsavel, "Informe o responsável (aluno menor de idade)");
            }
        }
        String telResp = AdminUi.soDigitos(etTelefoneResp.getText().toString());
        if (!telResp.isEmpty() && telResp.length() < 10) return erro(etTelefoneResp, "Telefone incompleto");

        if (spTurma.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione a turma", Toast.LENGTH_SHORT).show();
            spTurma.requestFocus();
            return false;
        }
        return true;
    }

    private void cadastrar() {
        if (!validar()) return;

        String turma = (String) spTurma.getSelectedItem();
        Aluno aluno = new Aluno(etNome.getText().toString().trim(), turma, status);
        aluno.setNascimento(etNascimento.getText().toString());
        aluno.setCpf(etCpf.getText().toString());
        aluno.setTelefone(etTelefone.getText().toString());
        aluno.setEmail(etEmail.getText().toString().trim());
        aluno.setResponsavel(etResponsavel.getText().toString().trim());
        aluno.setTelefoneResponsavel(etTelefoneResp.getText().toString());
        aluno.setInstrumento(etInstrumento.getText().toString().trim());
        aluno.setFotoUri(fotoUri);

        // TODO (back-end): salvar o aluno (e a foto) no banco de dados
        MockData.alunos().add(0, aluno);

        // Atualiza a contagem de alunos da turma
        if (status != Status.INATIVO) {
            for (Turma t : MockData.turmas()) {
                if (t.getNome().equals(turma)) t.setQtdAlunos(t.getQtdAlunos() + 1);
            }
        }

        Toast.makeText(this, "Aluno cadastrado com sucesso", Toast.LENGTH_SHORT).show();
        finish();
    }
}
