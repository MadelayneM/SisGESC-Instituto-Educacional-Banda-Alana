package com.example.sisgesc.Repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.sisgesc.DTOs.AlunoDadosEdicao;
import com.example.sisgesc.DTOs.AlunoDashboard;
import com.example.sisgesc.DTOs.AlunoFrequencia;
import com.example.sisgesc.DTOs.AlunoPerfil;
import com.example.sisgesc.DTOs.AlunoPerfilResponse;
import com.example.sisgesc.DTOs.AlunoTurma;
import com.example.sisgesc.DTOs.FormatoData;
import com.example.sisgesc.DTOs.MatriculaResponse;
import com.google.gson.Gson;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

// Confere a conversão usando JSON no mesmo formato do Supabase, com dados inventados
public class AlunoMapperTest {

    private static final String CPF = "11111111111";
    private final Gson gson = new Gson();

    private static final String MATRICULA_COMPLETA = "{"
            + "\"id_matricula\":1,\"ano\":2026,\"semestre\":2,"
            + "\"tb_aluno\":{\"tb_pessoa\":{\"primeiro_nome\":\"Maria\",\"sobrenome\":\"Silva\",\"imagem_perfil\":\"http://foto\"}},"
            + "\"tb_curso\":{\"nome_curso\":\"Flauta\"},"
            + "\"tb_frequencia\":["
            + "{\"data_aula\":\"2026-09-01\",\"presenca\":false,\"observacao_frequencia\":\"Doente\"},"
            + "{\"data_aula\":\"2026-08-25\",\"presenca\":true,\"observacao_frequencia\":null},"
            + "{\"data_aula\":\"2026-08-28\",\"presenca\":true,\"observacao_frequencia\":null}],"
            + "\"tb_turma\":{\"pk_nome_turma\":\"Musicalização 01\",\"qtd_alunos\":11,"
            + "\"data_da_aula\":\"2026-11-06\",\"horario_inicio\":\"14:00:00\",\"horario_fim\":\"15:30:00\","
            + "\"tb_turma_funcionario\":["
            + "{\"tb_tipo_monitor\":{\"tipo_monitor\":\"Monitor Auxiliar\"},\"tb_funcionario\":{\"tb_pessoa\":{\"primeiro_nome\":\"Ana\",\"sobrenome\":\"Souza\"}}},"
            + "{\"tb_tipo_monitor\":{\"tipo_monitor\":\"Monitor Multiplicador\"},\"tb_funcionario\":{\"tb_pessoa\":{\"primeiro_nome\":\"João\",\"sobrenome\":\"Silva\"}}}],"
            + "\"tb_matricula\":["
            + colega(CPF, 2026, 2, "Ativo", "Maria") + ","
            + colega("2", 2026, 2, "Ativo", "Carla") + ","
            + colega("3", 2026, 2, "Ativo", "Bruno") + ","
            + colega("4", 2026, 2, "Inativo", "Inativa") + ","
            + colega("5", 2026, 1, "Ativo", "OutroSemestre")
            + "]}}";

    private static String colega(String cpf, int ano, int semestre, String status, String nome) {
        return "{\"fk_cpf\":\"" + cpf + "\",\"ano\":" + ano + ",\"semestre\":" + semestre
                + ",\"status_matricula\":\"" + status + "\",\"tb_aluno\":{\"tb_pessoa\":{\"primeiro_nome\":\""
                + nome + "\",\"sobrenome\":\"X\"}}}";
    }

    private MatriculaResponse matricula(String json) {
        return gson.fromJson(json, MatriculaResponse.class);
    }

    @Test
    public void frequencia_calculaTotaisEOrdenaDaAulaMaisAntiga() {
        AlunoFrequencia f = AlunoMapper.paraFrequencia(matricula(MATRICULA_COMPLETA));

        assertEquals(67, f.getPercentual());
        assertEquals(2, f.getTotalPresencas());
        assertEquals(1, f.getTotalAusencias());
        assertEquals(3, f.getTotalAulas());
        assertEquals("2026-08-25", f.getHistorico().get(0).getDataAula());
        assertTrue(f.getHistorico().get(0).isPresente());
        assertFalse(f.getHistorico().get(2).isPresente());
        assertEquals("Doente", f.getHistorico().get(2).getObservacao());
    }

    @Test
    public void frequencia_semAulas_naoDividePorZero() {
        AlunoFrequencia f = AlunoMapper.paraFrequencia(matricula("{\"tb_frequencia\":[]}"));

        assertEquals(0, f.getPercentual());
        assertEquals(0, f.getTotalAulas());
    }

    @Test
    public void turma_preencheDadosESeparaProfessorDeMonitor() {
        AlunoTurma t = AlunoMapper.paraTurma(CPF, matricula(MATRICULA_COMPLETA));

        assertEquals("Musicalização 01", t.getNomeTurma());
        assertEquals("Flauta", t.getNomeCurso());
        assertEquals("João Silva", t.getProfessor());
        assertEquals("Ana Souza", t.getMonitor());
        assertEquals("2026-11-06", t.getDataAula());
        assertEquals("Sexta-feira", t.getDiaSemana());
        assertEquals("14:00", t.getHorarioInicio());
        assertEquals("15:30", t.getHorarioFim());
        assertEquals(Integer.valueOf(11), t.getQtdAlunos());
    }

    @Test
    public void turma_colegasExcluemOProprioAlunoInativosEOutrosSemestres() {
        AlunoTurma t = AlunoMapper.paraTurma(CPF, matricula(MATRICULA_COMPLETA));

        assertEquals(Arrays.asList("Bruno X", "Carla X"), t.getColegas());
    }

    @Test
    public void turma_matriculaSemTurma_naoQuebra() {
        AlunoTurma t = AlunoMapper.paraTurma(CPF, matricula("{\"tb_turma\":null}"));

        assertNull(t.getNomeTurma());
        assertTrue(t.getColegas().isEmpty());
    }

    @Test
    public void dashboard_juntaAlunoTurmaEFrequencia() {
        AlunoDashboard d = AlunoMapper.paraDashboard(CPF, matricula(MATRICULA_COMPLETA));

        assertEquals("Maria", d.getPrimeiroNome());
        assertEquals("Maria Silva", d.getNomeCompleto());
        assertEquals("http://foto", d.getFotoUrl());
        assertEquals("Musicalização 01", d.getTurma().getNomeTurma());
        assertEquals(2, d.getFrequencia().getTotalPresencas());
    }

    @Test
    public void perfil_pegaPrimeiroTelefoneEResponsavel() {
        String json = "{\"tb_pessoa\":{\"primeiro_nome\":\"Maria\",\"sobrenome\":\"Silva\",\"imagem_perfil\":null,"
                + "\"tb_email\":{\"pk_email\":\"maria@teste.org\"},"
                + "\"tb_telefone\":[{\"pk_ddd\":\"11 \",\"pk_numero\":\"912345678\",\"tipo_telefone\":\"Celular\"}]},"
                + "\"tb_aluno_responsavel\":[{\"grau_parentesco\":\"Mae\",\"tb_responsavel\":{\"tb_pessoa\":"
                + "{\"primeiro_nome\":\"Ana\",\"sobrenome\":\"Silva\",\"tb_telefone\":"
                + "[{\"pk_ddd\":\"11\",\"pk_numero\":\"987654321\",\"tipo_telefone\":\"Celular\"}]}}}]}";

        AlunoPerfil p = AlunoMapper.paraPerfil(gson.fromJson(json, AlunoPerfilResponse.class));

        assertEquals("Maria Silva", p.getNomeCompleto());
        assertEquals("maria@teste.org", p.getEmail());
        assertEquals("(11) 912345678", p.getTelefone());
        assertEquals("Ana Silva", p.getNomeResponsavel());
        assertEquals("(11) 987654321", p.getTelefoneResponsavel());
        assertNull(p.getFotoUrl());
    }

    @Test
    public void dadosEdicao_preencheEmailTelefoneEEndereco() {
        String json = "{\"tb_pessoa\":{\"tb_email\":{\"pk_email\":\"maria@teste.org\"},"
                + "\"tb_telefone\":[{\"pk_ddd\":\"11 \",\"pk_numero\":\"912345678\",\"tipo_telefone\":\"Celular\"}],"
                + "\"tb_endereco\":[{\"pk_cep\":\"01001000\",\"numero_endereco\":12,\"complemento\":\"Casa\","
                + "\"tb_cep\":{\"logradouro\":\"Rua A\",\"bairro\":\"Centro\",\"cidade\":\"São Paulo\",\"uf\":\"SP\"}}]}}";

        AlunoDadosEdicao d = AlunoMapper.paraDadosEdicao(gson.fromJson(json, AlunoPerfilResponse.class));

        assertEquals("maria@teste.org", d.getEmail());
        assertEquals("11", d.getDdd());
        assertEquals("912345678", d.getNumeroTelefone());
        assertEquals("01001000", d.getCep());
        assertEquals("Rua A", d.getLogradouro());
        assertEquals("SP", d.getUf());
        assertEquals("12", d.getNumeroEndereco());
    }

    @Test
    public void dadosEdicao_semCadastro_naoQuebra() {
        String json = "{\"tb_pessoa\":{\"tb_email\":null,\"tb_telefone\":[],\"tb_endereco\":[]}}";

        AlunoDadosEdicao d = AlunoMapper.paraDadosEdicao(gson.fromJson(json, AlunoPerfilResponse.class));

        assertNull(d.getEmail());
        assertNull(d.getDdd());
        assertNull(d.getCep());
    }

    @Test
    public void formatoData_mostraDiaESemana() {
        assertEquals("25/08 — Terça", FormatoData.diaComSemana("2026-08-25"));
        assertEquals("29/08 — Sábado", FormatoData.diaComSemana("2026-08-29"));
    }

    @Test
    public void formatoData_mostraHojeOntemOuData() throws Exception {
        Date hoje = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse("2026-11-06");

        assertEquals("Hoje", FormatoData.relativa("2026-11-06T13:45:00", hoje));
        assertEquals("Ontem", FormatoData.relativa("2026-11-05T08:00:00", hoje));
        assertEquals("04/11", FormatoData.relativa("2026-11-04T08:00:00", hoje));
    }

    @Test
    public void perfil_semTelefoneEResponsavel_naoQuebra() {
        String json = "{\"tb_pessoa\":{\"primeiro_nome\":\"Maria\",\"sobrenome\":null,\"tb_telefone\":[]},"
                + "\"tb_aluno_responsavel\":[]}";

        AlunoPerfil p = AlunoMapper.paraPerfil(gson.fromJson(json, AlunoPerfilResponse.class));

        assertEquals("Maria", p.getNomeCompleto());
        assertNull(p.getEmail());
        assertNull(p.getTelefone());
        assertNull(p.getNomeResponsavel());
    }
}
