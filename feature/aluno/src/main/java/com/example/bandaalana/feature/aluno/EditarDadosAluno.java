package com.example.bandaalana.feature.aluno;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bandaalana.feature.aluno.DTOs.AlunoDadosEdicao;
import com.example.bandaalana.feature.aluno.Repository.AlunoRepository;
import com.example.bandaalana.feature.aluno.Repository.RepositoryCallback;

// Tela "Editar dados": o aluno altera e-mail, telefone e endereço.
// Aberta pelo botão "Editar dados" do Perfil, que manda o CPF no Intent.
public class EditarDadosAluno extends AppCompatActivity {

    private AlunoRepository repository;
    private String cpf;

    // Valores que estavam salvos. Servem para o telefone saber qual linha do banco alterar.
    private AlunoDadosEdicao dadosAtuais;

    private EditText edtEmail, edtDdd, edtNumeroTelefone, edtCep, edtLogradouro, edtBairro,
            edtCidade, edtUf, edtNumeroEndereco, edtComplemento;
    private Spinner spnTipoTelefone;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.editar_dados_aluno);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cpf = SessaoAluno.cpfOuVoltarAoLogin(this);
        if (cpf == null) {
            return;
        }
        repository = new AlunoRepository();

        edtEmail = findViewById(R.id.edtEmail);
        edtDdd = findViewById(R.id.edtDdd);
        edtNumeroTelefone = findViewById(R.id.edtNumeroTelefone);
        spnTipoTelefone = findViewById(R.id.spnTipoTelefone);
        edtCep = findViewById(R.id.edtCep);
        edtLogradouro = findViewById(R.id.edtLogradouro);
        edtBairro = findViewById(R.id.edtBairro);
        edtCidade = findViewById(R.id.edtCidade);
        edtUf = findViewById(R.id.edtUf);
        edtNumeroEndereco = findViewById(R.id.edtNumeroEndereco);
        edtComplemento = findViewById(R.id.edtComplemento);
        btnSalvar = findViewById(R.id.btnSalvarDados);

        ArrayAdapter<String> tipos = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, AlunoDadosEdicao.TIPOS_TELEFONE);
        tipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipoTelefone.setAdapter(tipos);

        findViewById(R.id.btnCancelarEdicao).setOnClickListener(v -> finish());
        btnSalvar.setOnClickListener(v -> salvar());

        carregarDados();
    }

    private void carregarDados() {
        repository.buscarDadosEdicao(cpf, new RepositoryCallback<AlunoDadosEdicao>() {
            @Override
            public void onSucesso(AlunoDadosEdicao dados) {
                if (SessaoAluno.telaFechada(EditarDadosAluno.this)) return;
                dadosAtuais = dados;
                preencher(dados);
                btnSalvar.setEnabled(true);
            }

            @Override
            public void onErro(String mensagem) {
                if (SessaoAluno.telaFechada(EditarDadosAluno.this)) return;
                Toast.makeText(EditarDadosAluno.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void preencher(AlunoDadosEdicao dados) {
        edtEmail.setText(dados.getEmail());
        edtDdd.setText(dados.getDdd());
        edtNumeroTelefone.setText(dados.getNumeroTelefone());
        edtCep.setText(dados.getCep());
        edtLogradouro.setText(dados.getLogradouro());
        edtBairro.setText(dados.getBairro());
        edtCidade.setText(dados.getCidade());
        edtUf.setText(dados.getUf());
        edtNumeroEndereco.setText(dados.getNumeroEndereco());
        edtComplemento.setText(dados.getComplemento());

        String[] tipos = AlunoDadosEdicao.TIPOS_TELEFONE;
        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].equalsIgnoreCase(dados.getTipoTelefone())) {
                spnTipoTelefone.setSelection(i);
            }
        }
    }

    // Salva em três etapas, uma de cada vez: e-mail, telefone e endereço.
    // Uma seção deixada totalmente em branco é ignorada; se ficar pela metade, o Repository avisa o que falta.
    private void salvar() {
        btnSalvar.setEnabled(false);
        salvarEmail();
    }

    private void salvarEmail() {
        String email = texto(edtEmail);
        if (email.isEmpty()) {
            salvarTelefone();
            return;
        }
        repository.atualizarEmail(cpf, email, etapa(this::salvarTelefone));
    }

    private void salvarTelefone() {
        String ddd = texto(edtDdd);
        String numero = texto(edtNumeroTelefone);
        if (ddd.isEmpty() && numero.isEmpty()) {
            salvarEndereco();
            return;
        }
        String tipo = (String) spnTipoTelefone.getSelectedItem();
        String dddAtual = dadosAtuais.getDdd();
        String numeroAtual = dadosAtuais.getNumeroTelefone();
        repository.atualizarTelefone(cpf, dddAtual, numeroAtual, ddd, numero, tipo, etapa(this::salvarEndereco));
    }

    private void salvarEndereco() {
        String cep = texto(edtCep);
        String logradouro = texto(edtLogradouro);
        String bairro = texto(edtBairro);
        String cidade = texto(edtCidade);
        String uf = texto(edtUf).toUpperCase();
        String numero = texto(edtNumeroEndereco);
        String complemento = texto(edtComplemento);

        boolean tudoVazio = cep.isEmpty() && logradouro.isEmpty() && bairro.isEmpty()
                && cidade.isEmpty() && uf.isEmpty() && numero.isEmpty() && complemento.isEmpty();
        if (tudoVazio) {
            terminar();
            return;
        }
        repository.atualizarEndereco(cpf, cep, logradouro, bairro, cidade, uf, numero,
                complemento.isEmpty() ? null : complemento, etapa(this::terminar));
    }

    private void terminar() {
        Toast.makeText(this, "Dados salvos!", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Callback igual para as três etapas: se der certo, segue para a próxima; se der erro, para e avisa
    private RepositoryCallback<Void> etapa(Runnable proxima) {
        return new RepositoryCallback<Void>() {
            @Override
            public void onSucesso(Void resultado) {
                if (SessaoAluno.telaFechada(EditarDadosAluno.this)) return;
                proxima.run();
            }

            @Override
            public void onErro(String mensagem) {
                if (SessaoAluno.telaFechada(EditarDadosAluno.this)) return;
                btnSalvar.setEnabled(true);
                Toast.makeText(EditarDadosAluno.this, mensagem, Toast.LENGTH_LONG).show();
            }
        };
    }

    private String texto(EditText campo) {
        return campo.getText().toString().trim();
    }
}
