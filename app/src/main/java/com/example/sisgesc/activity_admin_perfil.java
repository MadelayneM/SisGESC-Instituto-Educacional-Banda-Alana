package com.example.sisgesc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sisgesc.DTOs.AdminPerfil;
import com.example.sisgesc.Repository.AdminRepository;
import com.example.sisgesc.Repository.RepositoryCallback;

// Perfil do Administrador: dados do banco (AdminRepository.buscarPerfil) e botão Sair.
//
// Para o back-end:
//   btnEditarDados   abrir dialog_editar_dados_admin -> atualizarEmail / atualizarTelefone
//   btnAlterarSenha  abrir dialog_alterar_senha_admin -> alterarSenha(cpf, atual, nova)
//   imgPerfilAdmin   escolher imagem -> atualizarFoto (opcional)
public class activity_admin_perfil extends AppCompatActivity {

    private final AdminRepository repository = new AdminRepository();
    private Button btnEditarDados,btnAlterarSenha;

    private ImageView imgPerfilAdmin;

    private EditText edtSenhaAtual, edtNovaSenha, edtConfirmarSenha,edtEmail,edtDdd,edtNumeroTelefone;

    private Spinner spnTipoTelefone;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuAdmin.configurar(this, R.id.navPerfil);


        // 1. Botão "Editar dados" - Abre um Dialog ou outra Activity para editar nome, telefone, etc.
        Button btnEditarDados = findViewById(R.id.btnEditarDados); // Ajuste o ID conforme o seu XML
        btnEditarDados.setOnClickListener(v -> {
            // Exemplo: Chamar um método que exibe um AlertDialog customizado ou abre Activity de edição
            abrirDialogEditarDados();
        });

// 2. Botão "Alterar senha" - Abre um Dialog para digitar a senha antiga e a nova
        Button btnAlterarSenha = findViewById(R.id.btnAlterarSenha); // Ajuste o ID conforme o seu XML
        btnAlterarSenha.setOnClickListener(v -> {
            abrirDialogAlterarSenha();
        });

// 3. Clique na foto de perfil para atualizar a imagem (ImageView)
        ImageView imgPerfil = findViewById(R.id.imgPerfilAdmin
        ); // Ajuste o ID conforme o seu XML
        imgPerfil.setOnClickListener(v -> {
            abrirSeletorDeImagens();
        });

        // Sair: volta ao login e fecha todas as telas do Administrador
        findViewById(R.id.btnSairPerfil).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });




        repository.buscarPerfil(getIntent().getStringExtra(MainActivity.EXTRA_CPF), new RepositoryCallback<AdminPerfil>() {
            @Override
            public void onSucesso(AdminPerfil perfil) {
                mostrarPerfil(perfil);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(activity_admin_perfil.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void abrirSeletorDeImagens() {
        // Cria uma Intent para abrir a galeria de imagens do dispositivo
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        // Inicia a activity esperando um resultado (código de requisição 100)
        startActivityForResult(intent, 100);
    }

    private void abrirDialogAlterarSenha() {
        // 1. Inflar o layout do diálogo de alteração de senha
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_alterar_senha_admin, null);

        // 2. Vincular os campos de texto do XML (ajuste os IDs conforme os componentes do seu arquivo XML)
         EditText edtSenhaAtual = dialogView.findViewById(R.id.edtSenhaAtual);
         EditText edtNovaSenha = dialogView.findViewById(R.id.edtNovaSenha);
         EditText edtConfirmarSenha = dialogView.findViewById(R.id.edtConfirmarSenha);

        // 3. Construir e exibir o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alterar Senha")
                .setView(dialogView)
                .setPositiveButton("Atualizar", (dialog, which) -> {
                    // Validações básicas e chamada ao AdminRepository para atualizar no Supabase Auth
                    Toast.makeText(this, "Atualizando senha...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void abrirDialogEditarDados() {
        // 1. Inflar o layout exato que você criou (dialog_editar_dados_admin.xml)
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editar_dados_admin, null);

        // 2. Vincular os campos de texto do seu XML do diálogo (ajuste os IDs conforme os componentes reais do seu XML)
         EditText editNome = dialogView.findViewById(R.id.edtEmail);
        EditText edtDdd = dialogView.findViewById(R.id.edtDdd);
        EditText  edtNumeroTelefone = dialogView.findViewById(R.id.edtNumeroTelefone);
        Spinner spinTipoTelefone =dialogView.findViewById(R.id.spnTipoTelefone) ;



        // 3. Construir e exibir o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Dados do Perfil")
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    // Aqui você captura os valores digitados e chama o seu AdminRepository para salvar no Supabase
                    Toast.makeText(this, "Salvando dados...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();

    }


    private void mostrarPerfil(AdminPerfil perfil) {
        ((TextView) findViewById(R.id.txtNomePerfil)).setText(perfil.getNomeCompleto());
        ((TextView) findViewById(R.id.txtTipoPerfil)).setText(perfil.getCargo());
        ((TextView) findViewById(R.id.txtEmailPerfil)).setText(
                perfil.getEmail() == null ? "Não cadastrado" : perfil.getEmail());
        String telefone = perfil.getTelefoneFormatado();
        ((TextView) findViewById(R.id.txtTelefonePerfil)).setText(telefone == null ? "Não cadastrado" : telefone);
    }
}
