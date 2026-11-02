package com.example.layout_appdev.admin;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.layout_appdev.R;
import com.example.layout_appdev.admin.model.Membro;
import com.example.layout_appdev.admin.model.Turma;
import com.example.layout_appdev.admin.util.AdminUi;
import com.example.layout_appdev.admin.util.MockData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminNovaTurmaActivity extends AppCompatActivity {

    private static final String[] DIAS = {
            "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"
    };

    private EditText etNomeTurma, etHorario;
    private Spinner spProfessor, spDia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_nova_turma);
        AdminUi.aplicarInsets(this);
        AdminUi.configurarCabecalho(this, "Nova Turma");

        etNomeTurma = findViewById(R.id.etNomeTurma);
        etHorario = findViewById(R.id.etHorario);
        spProfessor = findViewById(R.id.spProfessor);
        spDia = findViewById(R.id.spDia);

        // Professores cadastrados na Equipe
        List<String> professores = new ArrayList<>();
        professores.add("Selecionar professor...");
        for (Membro m : MockData.equipe()) if (m.isProfessor()) professores.add(m.getNome());
        configurarSpinner(spProfessor, professores);

        List<String> dias = new ArrayList<>();
        dias.add("Selecionar dia...");
        for (String d : DIAS) dias.add(d);
        configurarSpinner(spDia, dias);

        etHorario.setOnClickListener(v -> new TimePickerDialog(this, (view, hora, minuto) -> {
            etHorario.setText(String.format(Locale.getDefault(), "%02d:%02d", hora, minuto));
            etHorario.setError(null);
        }, 14, 0, true).show());

        findViewById(R.id.btnCriarTurma).setOnClickListener(v -> criar());
    }

    private void configurarSpinner(Spinner spinner, List<String> itens) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itens);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void criar() {
        String nome = etNomeTurma.getText().toString().trim();
        if (nome.isEmpty()) {
            etNomeTurma.setError("Informe o nome da turma");
            etNomeTurma.requestFocus();
            return;
        }
        for (Turma t : MockData.turmas()) {
            if (t.getNome().equalsIgnoreCase(nome)) {
                etNomeTurma.setError("Já existe uma turma com esse nome");
                etNomeTurma.requestFocus();
                return;
            }
        }
        if (spProfessor.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o professor responsável", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spDia.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o dia da semana", Toast.LENGTH_SHORT).show();
            return;
        }
        String horario = etHorario.getText().toString();
        if (horario.isEmpty()) {
            etHorario.setError("Informe o horário");
            return;
        }

        // TODO (back-end): salvar a turma no banco de dados
        MockData.turmas().add(0, new Turma(nome,
                (String) spProfessor.getSelectedItem(),
                (String) spDia.getSelectedItem(),
                horario, 0));

        Toast.makeText(this, "Turma criada com sucesso", Toast.LENGTH_SHORT).show();
        finish();
    }
}
