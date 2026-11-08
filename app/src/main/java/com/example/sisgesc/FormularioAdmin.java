package com.example.sisgesc;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sisgesc.DTOs.AdminOpcao;

import java.util.Arrays;
import java.util.List;

// Ferramentas comuns aos formulários do Administrador (novo aluno, novo membro e nova turma).
public final class FormularioAdmin {

    // Extras que dizem ao formulário qual registro abrir para edição.
    // Sem o extra, o formulário abre vazio para cadastrar.
    public static final String EXTRA_CPF_ALUNO = "cpf_aluno";
    public static final String EXTRA_CPF_MEMBRO = "cpf_membro";
    public static final String EXTRA_NOME_TURMA = "nome_turma";

    private FormularioAdmin() {
    }

    // Título do cabeçalho (view_form_header) e seta de voltar
    public static void configurarCabecalho(AppCompatActivity tela, String titulo) {
        ((TextView) tela.findViewById(R.id.tvTituloForm)).setText(titulo);
        tela.findViewById(R.id.btnVoltar).setOnClickListener(v -> tela.finish());
    }

    // Abre um formulário levando o CPF de quem entrou e, na edição, a chave do registro
    public static void abrir(AppCompatActivity telaAtual, Class<?> formulario, String extra, String valor) {
        Intent intent = new Intent(telaAtual, formulario);
        intent.putExtra(MainActivity.EXTRA_CPF, telaAtual.getIntent().getStringExtra(MainActivity.EXTRA_CPF));
        if (extra != null && valor != null) intent.putExtra(extra, valor);
        telaAtual.startActivity(intent);
    }

    // Spinner com valores fixos (os enums do banco, ex.: CadastroAluno.PERIODOS)
    public static void preencherSpinner(Spinner spinner, String[] valores) {
        preencherSpinner(spinner, Arrays.asList(valores));
    }

    // Spinner com itens que vêm do banco (AdminRepository.buscarOpcoes*).
    // Para ler a escolha: ((AdminOpcao) spinner.getSelectedItem()).getValor()
    public static <T> void preencherSpinner(Spinner spinner, List<T> itens) {
        ArrayAdapter<T> adapter = new ArrayAdapter<>(spinner.getContext(),
                R.layout.activity_item_admin_spinner, itens);
        adapter.setDropDownViewResource(R.layout.activity_item_admin_spinner);
        spinner.setAdapter(adapter);
    }

    // Seleciona no Spinner o item com este texto (valores fixos) ou valor (AdminOpcao). Usado na edição.
    public static void selecionar(Spinner spinner, String valor) {
        if (valor == null || spinner.getAdapter() == null) return;
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            Object item = spinner.getAdapter().getItem(i);
            String chave = item instanceof AdminOpcao ? ((AdminOpcao) item).getValor() : String.valueOf(item);
            if (valor.equals(chave)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    // "12/03/2014" (tela) -> "2014-03-12" (banco). Devolve null se a data estiver incompleta.
    public static String dataParaBanco(String dataTela) {
        if (dataTela == null) return null;
        String[] partes = dataTela.trim().split("/");
        if (partes.length != 3 || partes[2].length() != 4) return null;
        return partes[2] + "-" + doisDigitos(partes[1]) + "-" + doisDigitos(partes[0]);
    }

    // "2014-03-12" (banco) -> "12/03/2014" (tela)
    public static String dataParaTela(String dataBanco) {
        if (dataBanco == null || dataBanco.length() < 10) return "";
        return dataBanco.substring(8, 10) + "/" + dataBanco.substring(5, 7) + "/" + dataBanco.substring(0, 4);
    }

    // "123.456.789-00" -> "12345678900" (formato que o Repository espera)
    public static String somenteDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }

    // Horário do banco "14:00:00" -> "14:00"
    public static String horarioParaTela(String horario) {
        return horario == null || horario.length() < 5 ? "" : horario.substring(0, 5);
    }

    private static String doisDigitos(String numero) {
        return numero.length() == 1 ? "0" + numero : numero;
    }
}
