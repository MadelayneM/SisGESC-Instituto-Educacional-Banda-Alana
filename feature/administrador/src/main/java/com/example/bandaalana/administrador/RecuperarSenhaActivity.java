package com.example.bandaalana.administrador;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bandaalana.administrador.Repository.AuthRepository;
import com.example.bandaalana.administrador.Repository.RepositoryCallback;

public class RecuperarSenhaActivity extends AppCompatActivity {

    private EditText edtCpf;
    private AppCompatButton btnEnviarRecuperacao;
    private LinearLayout cardSucesso;
    private TextView txtMensagemSucesso;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_recuperar_senha);

        // ATENÇÃO: Troque pelo nome exato do seu arquivo XML, se for diferente
        setContentView(R.layout.activity_recuperar_senha);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtCpf = findViewById(R.id.edtCpf);
        btnEnviarRecuperacao = findViewById(R.id.btnEnviarRecuperacao);
        cardSucesso = findViewById(R.id.cardSucesso);
        txtMensagemSucesso = findViewById(R.id.txtMensagemSucesso);


        //authRepository = new AuthRepository(); //
        findViewById(R.id.btnVoltarLogin).setOnClickListener(v -> finish());



        //btnEnviarRecuperacao.setOnClickListener(v -> verificarCpfEAbrirRedefinicao());


    }


    /*private void verificarCpfEAbrirRedefinicao() {
        String cpf = edtCpf.getText().toString().replaceAll("\\D", "");

        if (cpf.length() != 11) {
            edtCpf.setError("Digite os 11 números do CPF cadastrado");
            return;
        }

        cardSucesso.setVisibility(View.GONE);
        btnEnviarRecuperacao.setEnabled(false);

        authRepository.verificarCpfRecuperacao(cpf, new RepositoryCallback<Boolean>() {
            @Override
            public void onSucesso(Boolean existe) {
                btnEnviarRecuperacao.setEnabled(true);
                abrirDialogNovaSenha(cpf);
            }

            @Override
            public void onErro(String mensagem) {
                btnEnviarRecuperacao.setEnabled(true);
                edtCpf.setError(mensagem);
                Toast.makeText(RecuperarSenhaActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }*/

    /*private void abrirDialogNovaSenha(String cpf) {
        View view = getLayoutInflater().inflate(R.layout.dialog_redefinir_senha, null);

        EditText edtNovaSenha = view.findViewById(R.id.edtNovaSenhaDialog);
        EditText edtConfirmarSenha = view.findViewById(R.id.edtConfirmarSenhaDialog);
        AppCompatButton btnCancelar = view.findViewById(R.id.btnCancelarDialogSenha);
        AppCompatButton btnSalvar = view.findViewById(R.id.btnSalvarDialogSenha);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnSalvar.setOnClickListener(v -> {
            String novaSenha = edtNovaSenha.getText().toString();
            String confirmar = edtConfirmarSenha.getText().toString();

            if (novaSenha.length() < 4) {
                edtNovaSenha.setError("A senha deve ter pelo menos 4 caracteres");
                return;
            }
            if (!novaSenha.equals(confirmar)) {
                edtConfirmarSenha.setError("As senhas não coincidem");
                return;
            }

            btnSalvar.setEnabled(false);
            authRepository.redefinirSenhaPorCpf(cpf, novaSenha, new RepositoryCallback<String>() {
                @Override
                public void onSucesso(String mensagem) {
                    dialog.dismiss();
                    cardSucesso.setVisibility(View.VISIBLE);
                    txtMensagemSucesso.setText("Senha alterada com sucesso! Você já pode voltar e entrar com a nova senha.");
                    edtCpf.setText("");
                    Toast.makeText(RecuperarSenhaActivity.this, mensagem, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onErro(String mensagem) {
                    btnSalvar.setEnabled(true);
                    Toast.makeText(RecuperarSenhaActivity.this, mensagem, Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }*/
}