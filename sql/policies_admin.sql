-- Policies para o módulo Administrador cadastrar, editar e excluir pelo app.
--
-- JÁ FORAM APLICADAS no banco do grupo em 07/11/2026. Só é preciso rodar de novo
-- se o banco for recriado ou se as policies forem apagadas. Sinal de que faltam:
-- o AdminRepository devolve "o banco não permitiu a gravação (falta policy de RLS)".
--
-- O app usa a chave pública, então o papel no banco é "anon".
-- Rodar no Supabase: SQL Editor > New query > colar tudo > Run.
-- Pode rodar mais de uma vez: cada policy é apagada antes de ser criada de novo.
-- As policies que já existem (leitura, frequência, e-mail...) continuam valendo.

do $$
declare
  tabela text;
begin
  foreach tabela in array array[
    'tb_pessoa', 'tb_aluno', 'tb_responsavel', 'tb_aluno_responsavel',
    'tb_matricula', 'tb_frequencia', 'tb_funcionario', 'tb_funcionario_cargo',
    'tb_turma', 'tb_turma_funcionario', 'tb_email', 'tb_telefone', 'tb_endereco'
  ]
  loop
    execute format('drop policy if exists admin_inserir on public.%I', tabela);
    execute format('drop policy if exists admin_alterar on public.%I', tabela);
    execute format('drop policy if exists admin_excluir on public.%I', tabela);

    execute format('create policy admin_inserir on public.%I for insert to anon with check (true)', tabela);
    execute format('create policy admin_alterar on public.%I for update to anon using (true) with check (true)', tabela);
    execute format('create policy admin_excluir on public.%I for delete to anon using (true)', tabela);
  end loop;
end $$;
