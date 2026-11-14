package com.example.bandaalana.administrador;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bandaalana.administrador.Adapters.AdminAlunoAdapter;
import com.example.bandaalana.administrador.DTOs.AdminAluno;
import com.example.bandaalana.administrador.Repository.AdminRepository;
import com.example.bandaalana.administrador.Repository.RepositoryCallback;

import java.util.List;

// Tela Alunos: lista do banco (AdminRepository.buscarAlunos) com a pesquisa por nome.
// "Novo" abre o formulário vazio; tocar num aluno abre o formulário de edição.
public class activity_admin_alunos extends AppCompatActivity {

    // Espera o usuário parar de digitar antes de pesquisar
    private static final long ESPERA_PESQUISA_MS = 400;

    private final AdminRepository repository = new AdminRepository();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable pesquisar = this::carregarAlunos;
    private final AdminAlunoAdapter adapter = new AdminAlunoAdapter(aluno ->
            FormularioAdmin.abrir(this, activity_admin_novo_aluno.class,
                    FormularioAdmin.EXTRA_CPF_ALUNO, aluno.getCpf()));

    private EditText etBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_alunos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminAlunos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuAdmin.configurar(this, R.id.navAlunos);

        RecyclerView rvAlunos = findViewById(R.id.rvAlunos);
        rvAlunos.setAdapter(adapter);

        findViewById(R.id.btnNovoAluno).setOnClickListener(v ->
                FormularioAdmin.abrir(this, activity_admin_novo_aluno.class, null, null));

        etBuscar = findViewById(R.id.etBuscar);
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(pesquisar);
                handler.postDelayed(pesquisar, ESPERA_PESQUISA_MS);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarAlunos();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(pesquisar);
        super.onDestroy();
    }

    private void carregarAlunos() {
        String texto = etBuscar.getText().toString();
        repository.buscarAlunos(texto, new RepositoryCallback<List<AdminAluno>>() {
            @Override
            public void onSucesso(List<AdminAluno> alunos) {
                // Ignora a resposta se o texto mudou enquanto a busca acontecia
                if (texto.equals(etBuscar.getText().toString())) adapter.atualizar(alunos);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(activity_admin_alunos.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }
}
