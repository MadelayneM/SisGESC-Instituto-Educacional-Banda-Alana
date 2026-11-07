# SisGESC: contexto do projeto

Projeto de faculdade, em grupo. App Android (Java, Android Studio) de gestão para a Escola Banda Alana: alunos, professores, turmas, matrículas e frequência.
Quem está usando o Claude Code é estudante e minha parte é a **camada de dados** (conexão com o Supabase, DTOs e Repositories). Explique cada mudança em linguagem simples e evite comandos ou conceitos avançados sem explicar.

## Divisão do trabalho
- Minha parte: SupabaseClient, SupabaseService, DTOs e Repositories. Não mexo em layouts nem em Activities.
- O resto do grupo faz layouts, cliques de botão e navegação entre telas. Eles só vão chamar os métodos dos meus Repositories.
- O grupo decidiu usar os **layouts antigos**. Os Repositories não podem depender de nenhum layout, ID de view ou Activity.
- Perfis: Administrador, Professor e Aluno. A tela de login é única e leva cada perfil ao seu conjunto de telas.
- Plano: Repositories divididos em 3 tipos (ADM, Aluno, Professor), cada um com todos os métodos que os layouts daquele perfil precisam. O Dashboard de cada perfil instancia os repositórios que precisar.
- O login é feito por **CPF** (11 dígitos, sem pontos nem traço).

## Arquitetura
- Conexão: Retrofit + Gson chamando a API REST do Supabase (PostgREST). Usar só a chave pública (publishable/anon). Nunca colocar chave secreta no código nem no Git.
- Camadas: `data/model` (DTOs), `data/remote` (SupabaseClient e SupabaseService, que deve ser `interface`), `data/repository`.
- Os Repositories devolvem o resultado por callback genérico `RepositoryCallback<T>` com `onSucesso(T)` e `onErro(String)`.
- Para dados que vêm de várias tabelas, usar uma consulta única com embedding do PostgREST (parâmetro `select`) e converter a resposta num objeto simples para a tela.
- Para PATCH, usar o header `Prefer: return=representation` e tratar lista vazia como "nada foi alterado". Isso também detecta bloqueio silencioso por RLS.
- O interceptor do SupabaseClient não deve fixar `Content-Type` (quebra o upload de arquivos). Ele só adiciona `apikey` e `Authorization`.
- A BASE_URL deve terminar com `/` e o app precisa da permissão INTERNET no AndroidManifest.

## Banco (Supabase/PostgreSQL), resumo
- tb_pessoa(pk_cpf, primeiro_nome, sobrenome, data_nasc, imagem_perfil): base de todo mundo
- tb_telefone(fk_cpf, pk_numero, pk_ddd, tipo_telefone enum)
- tb_cep(pk_cep, logradouro, bairro, cidade, uf); tb_endereco(pk_cpf, pk_cep, numero_endereco, complemento)
- tb_aluno(fk_cpf PK/FK de tb_pessoa, nome_escola, periodo_escola enum)
- tb_responsavel(fk_cpf); tb_aluno_responsavel(fk_cpf_aluno, fk_cpf_responsavel, grau_parentesco enum, autorizado_busca)
- tb_funcionario(fk_cpf_func); tb_cargo(pk_cod_cargo, cargo enum); tb_funcionario_cargo(fk_cpf_funcionario, fk_cod_cargo, data_entrada, data_saida); tb_tipo_monitor(pk_cod_tipo, tipo_monitor enum, descricao_tipo_monitor)
- tb_aviso(pk_id_aviso, fk_cpf_coordenador, titulo_aviso, descricao_aviso, data_envio)
- tb_curso(pk_cod_curso, nome_curso, descricao_curso, duracao_curso, idade_min, idade_max)
- tb_turma(pk_nome_turma, pk_horario, fk_cod_curso, qtd_alunos, horario_inicio, horario_fim)
- tb_matricula(id_matricula, fk_cpf para tb_aluno, ano, semestre, fk_nome_turma, fk_cod_curso, data_matricula, periodo enum, status_matricula enum, **senha_login**)
- tb_turma_funcionario(fk_nome_turma, fk_cpf_funcionario, fk_cod_tipo)
- tb_frequencia(id_frequencia, data_aula, presenca boolean, observacao_frequencia, id_matricula)
- tb_avaliacao(fk_id_matricula, data_avaliacao, fk_cpf_funcionario, descricao_avaliacao, tipo_avaliacao)
- tb_autorizacao_imagem: o grupo considera desnecessária; ignorar por enquanto.

A senha de login do aluno fica em `tb_matricula.senha_login` (texto), localizada por `fk_cpf`.

## Problemas e dúvidas conhecidos
- No login, a busca por CPF voltava vazia (HTTP 200 com lista vazia) mesmo com CPF existente. Suspeita principal: **RLS ligado sem policy de SELECT para anon**. Ainda não confirmado.
- O schema não tem coluna de e-mail, mas a tela de Perfil mostra e-mail.
- tb_funcionario não tem senha, então Professor e ADM ainda não conseguem logar com o schema atual. Precisa ser decidido com a colega que mantém o banco.
- A distinção ADM x Professor depende de tb_funcionario_cargo / tb_cargo.
- Segurança: se SELECT/UPDATE em tb_matricula for liberado para a chave pública, as senhas ficam expostas. Aceitável para o trabalho da faculdade, mas o ideal seria Supabase Auth ou uma função RPC de login.
- Editar telefone ficou pendente porque os valores do enum `tipo_telefone` não são conhecidos.

## Estado atual
- Tentativa anterior de AlunoPerfilRepository (nunca compilada nem testada) gerou erros em vermelho. Ela usava `AlunoPerfilResponse` (DTO aninhado que espelha o JSON), `AlunoPerfil` (objeto simples para a tela), `RepositoryCallback<T>`, e os métodos `buscarPerfil`, `alterarSenha` (valida a senha atual no próprio PATCH) e `atualizarFoto` (upload no bucket público `fotos_perfil` e salva a URL em tb_pessoa.imagem_perfil).
- Próximo passo: ler o projeto real, ajustar/criar esses arquivos para compilarem, e seguir com os repositórios das outras telas, um perfil por vez.
