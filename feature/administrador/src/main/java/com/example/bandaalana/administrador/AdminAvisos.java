package com.example.bandaalana.administrador;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bandaalana.administrador.Adapters.AdminAvisoAdapter;
import com.example.bandaalana.administrador.DTOs.Aviso;
import com.example.bandaalana.administrador.Repository.AdminRepository;
import com.example.bandaalana.administrador.Repository.RepositoryCallback;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdminAvisos extends AppCompatActivity {

    private final AdminRepository repository = new AdminRepository();
    private View cardNovoAviso;
    private View btnAbrirNovo;
    private EditText etTitulo, etDescricao;
    private MaterialButton btnPublicar;
    private AdminAvisoAdapter adapter;
    private String cpfCoordenador;

    // Guarda o aviso em edição (null quando estiver criando um novo)
    private Aviso avisoEmEdicao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_avisos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminAvisos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cpfCoordenador = getIntent().getStringExtra(LoginAdminActivity.EXTRA_CPF);

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());
        cardNovoAviso = findViewById(R.id.cardNovoAviso);
        btnAbrirNovo = findViewById(R.id.btnAbrirNovoAviso);
        etTitulo = findViewById(R.id.etTituloAviso);
        etDescricao = findViewById(R.id.etDescricaoAviso);
        btnPublicar = findViewById(R.id.btnPublicarAviso);

        // Abre o formulário vazio ao clicar em "+ Novo"
        if (btnAbrirNovo != null) {
            btnAbrirNovo.setOnClickListener(v -> abrirFormularioNovo());
        }

        // Fecha o formulário ao clicar em "Cancelar"
        findViewById(R.id.btnCancelarAviso).setOnClickListener(v -> fecharFormulario());

        RecyclerView rvAvisos = findViewById(R.id.rvAvisosAdmin);
        adapter = new AdminAvisoAdapter(this::confirmarExclusao, this::abrirFormularioEdicao);
        rvAvisos.setAdapter(adapter);

        btnPublicar.setOnClickListener(v -> salvar());
        carregarAvisos();
    }

    private void abrirFormularioNovo() {
        avisoEmEdicao = null;
        etTitulo.setText("");
        etDescricao.setText("");
        btnPublicar.setText("+ Publicar Aviso");
        cardNovoAviso.setVisibility(View.VISIBLE);
        if (btnAbrirNovo != null) btnAbrirNovo.setVisibility(View.GONE);
        etTitulo.requestFocus();
    }

    private void abrirFormularioEdicao(Aviso aviso) {
        avisoEmEdicao = aviso;
        etTitulo.setText(aviso.getTitulo());
        etDescricao.setText(aviso.getDescricao());
        btnPublicar.setText("Salvar Alterações");
        cardNovoAviso.setVisibility(View.VISIBLE);
        if (btnAbrirNovo != null) btnAbrirNovo.setVisibility(View.GONE);
        etTitulo.requestFocus();
    }

    private void fecharFormulario() {
        avisoEmEdicao = null;
        etTitulo.setText("");
        etDescricao.setText("");
        btnPublicar.setText("+ Publicar Aviso");
        cardNovoAviso.setVisibility(View.GONE);
        if (btnAbrirNovo != null) btnAbrirNovo.setVisibility(View.VISIBLE);

        View viewAtual = getCurrentFocus();
        if (viewAtual != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewAtual.getWindowToken(), 0);
        }
    }

    private void carregarAvisos() {
        repository.buscarAvisos(new RepositoryCallback<List<Aviso>>() {
            @Override
            public void onSucesso(List<Aviso> avisos) {
                adapter.atualizar(avisos);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(AdminAvisos.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void salvar() {
        String titulo = etTitulo.getText().toString().trim();
        String descricao = etDescricao.getText().toString().trim();

        if (titulo.isEmpty()) {
            etTitulo.setError("Digite o título");
            etTitulo.requestFocus();
            return;
        }
        if (descricao.isEmpty()) {
            etDescricao.setError("Digite a descrição");
            etDescricao.requestFocus();
            return;
        }

        btnPublicar.setEnabled(false);

        if (avisoEmEdicao != null) {
            // Edita aviso existente
            repository.atualizarAviso(avisoEmEdicao.getId(), titulo, descricao, new RepositoryCallback<Void>() {
                @Override
                public void onSucesso(Void resultado) {
                    btnPublicar.setEnabled(true);
                    fecharFormulario();
                    Toast.makeText(AdminAvisos.this, "Aviso atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    carregarAvisos();
                }

                @Override
                public void onErro(String mensagem) {
                    btnPublicar.setEnabled(true);
                    Toast.makeText(AdminAvisos.this, "Erro ao atualizar: " + mensagem, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // Publica novo aviso
            repository.publicarAviso(cpfCoordenador, titulo, descricao, new RepositoryCallback<Void>() {
                @Override
                public void onSucesso(Void resultado) {
                    btnPublicar.setEnabled(true);
                    fecharFormulario();
                    Toast.makeText(AdminAvisos.this, "Aviso publicado com sucesso!", Toast.LENGTH_SHORT).show();
                    carregarAvisos();
                }

                @Override
                public void onErro(String mensagem) {
                    btnPublicar.setEnabled(true);
                    Toast.makeText(AdminAvisos.this, "Erro ao publicar: " + mensagem, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void confirmarExclusao(Aviso aviso) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Aviso")
                .setMessage("Tem certeza que deseja excluir o aviso \"" + aviso.getTitulo() + "\"?")
                .setPositiveButton("Sim, excluir", (dialog, which) -> {
                    repository.deletarAviso(aviso.getId(), new RepositoryCallback<Void>() {
                        @Override
                        public void onSucesso(Void resultado) {
                            Toast.makeText(AdminAvisos.this, "Aviso excluído com sucesso!", Toast.LENGTH_SHORT).show();
                            carregarAvisos();
                        }

                        @Override
                        public void onErro(String mensagem) {
                            Toast.makeText(AdminAvisos.this, "Erro ao excluir: " + mensagem, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
