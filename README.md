# SisGESC – ERP de Gestão Educacional

Sistema de gestão escolar para a **Escola Banda Alana**, que oferece educação musical gratuita para crianças e adolescentes. O SisGESC centraliza alunos, responsáveis, funcionários, cursos, turmas, matrículas e frequência em um único aplicativo Android. Os dados ficam em um banco **PostgreSQL na nuvem, hospedado no Supabase**, então o app funciona em qualquer aparelho com internet, sem precisar instalar servidor ou banco local.

[![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white)](https://www.java.com)
[![Android](https://img.shields.io/badge/Android-Java%20%2B%20XML-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Supabase](https://img.shields.io/badge/Supabase-nuvem-3FCF8E?logo=supabase&logoColor=white)](https://supabase.com)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-na%20nuvem-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org)

<p align="center">
  <img src="docs/img/login.png" width="22%" alt="Seleção de perfil">
  <img src="docs/img/admin-dashboard.png" width="22%" alt="Painel do administrador">
  <img src="docs/img/professor-chamada.png" width="22%" alt="Chamada do professor">
  <img src="docs/img/aluno-frequencia.png" width="22%" alt="Frequência do aluno">
</p>

---

## Sobre o projeto

A Escola Banda Alana acompanhava seus alunos por meio de planilhas, Google Drive, papel e WhatsApp. Isso gerava erros, retrabalho e dificuldade para consultar os dados.

O SisGESC resolve esse problema reunindo tudo em um ambiente seguro e organizado:

- **Centralização:** alunos, turmas, matrículas e frequência em um só lugar
- **Menos retrabalho:** fim dos processos manuais e repetitivos
- **Acesso rápido:** consulta imediata para coordenação, equipe e gestão
- **Controle:** visão unificada de turmas, cursos, alunos e histórico

O sistema tem três perfis de acesso:

| Perfil | O que faz |
|--------|-----------|
| **Administrador / Gestor** | Cadastra e gerencia alunos, funcionários, cursos e turmas; acompanha indicadores e frequência |
| **Professor / Monitor** | Vê suas turmas, consulta alunos (somente leitura) e faz a chamada |
| **Aluno / Responsável** | Consulta dados, turma, aulas, avisos e histórico de presença, e atualiza seus dados de contato |

## Telas do app

**Acesso (todos os perfis)**

<p>
  <img width="18%" alt="Splashcrenn-Logo" src="https://github.com/user-attachments/assets/fbd482a9-d2a9-4731-9065-6fad1f8701fb" />
  <img width="18%" alt="selecaoPerfil-telaIninical" src="https://github.com/user-attachments/assets/cc1990a8-1448-4e74-b2a3-173d9581be9e" />
  <img width="18%" alt="loginAluno" src="https://github.com/user-attachments/assets/cceba369-ade2-4ff3-bf4f-ee8cd0387826" />
  <img width="18%" alt="loginProfessor" src="https://github.com/user-attachments/assets/c54c264f-950b-4d3e-a1a0-f475961a5091" />
  <img width="18%" alt="loginGESTOR" src="https://github.com/user-attachments/assets/608b565a-a11b-47f8-adf3-365907775365" />
  <img width="18%" alt="solicitarRedefinicaSenha-Adm" src="https://github.com/user-attachments/assets/84680a2e-8167-4b0e-830c-ae8df0054c56" />
  <img width="18%" alt="redefinirSenha-adm" src="https://github.com/user-attachments/assets/025c0d3b-0e35-4611-b7c3-cbf65b59339e" />
</p>

**Administrador / Gestor**

<p>
  <img width="18%" alt="Dashboard" src="https://github.com/user-attachments/assets/d90e859a-a42a-4ce3-bde4-f08472063ac4" />
<img width="18%" alt="editarAluno" src="https://github.com/user-attachments/assets/b9c17854-06d9-4a24-9ac2-ef3135b38016" />
<img width="18%" alt="EdicaoAvisos" src="https://github.com/user-attachments/assets/96aad6af-e659-454f-9a2a-c4795ecd827e" />
<img width="18%" alt="ConsultarFrequencia" src="https://github.com/user-attachments/assets/4457cb43-b078-4000-b593-88016792efdd" />
<img width="18%" alt="CadastrarAluno3" src="https://github.com/user-attachments/assets/e97944c0-c02c-4892-a081-d5df50074fd4" />
<img width="18%" alt="CadastrarAluno" src="https://github.com/user-attachments/assets/521af67a-c111-4305-9df7-d464fe73a6eb" />
<img width="18%" alt="CadastraAluno2" src="https://github.com/user-attachments/assets/6bd666a5-7726-41eb-810e-9fe99a741abf" />
<img width="18%" alt="alterarSenhajpeg" src="https://github.com/user-attachments/assets/bf9646be-3504-4c07-a868-f722206c93a5" />
<img width="18%" alt="telaTurmas" src="https://github.com/user-attachments/assets/4b946f41-6dec-4496-a0bc-b1546617946c" />
<img width="18%" alt="telaEquipe" src="https://github.com/user-attachments/assets/2bf29932-56fe-40d7-8344-5b70e298ed7a" />
<img width="18%" alt="telaAlunos" src="https://github.com/user-attachments/assets/3aa59816-5525-415e-81fb-6040d76c7cfd" />
<img width="18%" alt="perfil-gestor" src="https://github.com/user-attachments/assets/8280f46e-a831-436a-a4cc-7c1bb34f6d72" />
<img width="18%" alt="novoMembro" src="https://github.com/user-attachments/assets/52679ced-fa31-409a-8181-faf62fc0ebdb" />
<img width="18%" alt="novaTurma" src="https://github.com/user-attachments/assets/91d6029b-646a-4af4-b75b-3f3f86981f67" />
<img width="18%" alt="ListaAvisos" src="https://github.com/user-attachments/assets/27b2a30d-b8c2-441d-b71c-fd68059c3c39" />
<img width="18%" alt="GerenciarAvisos" src="https://github.com/user-attachments/assets/50b85d6a-b1ae-4d1d-aff5-b3716f031dda" />
<img width="18%" alt="editarTurmas" src="https://github.com/user-attachments/assets/4e644c48-903c-4531-a244-074572998429" />
<img width="18%" alt="editarMembro" src="https://github.com/user-attachments/assets/432290e7-0fd4-4c65-97a3-de80882ea7ff" />
<img width="18%" alt="editarDados" src="https://github.com/user-attachments/assets/2e38fd11-210b-4d74-b052-907a1552cb1f" />

</p>

**Professor / Monitor**

<p>
  <img width="18%" alt="enviarAVISO-daturma" src="https://github.com/user-attachments/assets/492763be-4985-462c-a92d-82497b2a1842" />
  <img width="18%" alt="EnviarAviso" src="https://github.com/user-attachments/assets/b4d993d5-9416-4ea9-93f7-b73cb91d65dc" />
  <img width="18%" alt="editarDados" src="https://github.com/user-attachments/assets/55078477-2b23-41e0-9d1c-65a01d252768" />
  <img width="18%" alt="alteraSENHA" src="https://github.com/user-attachments/assets/dbff7627-59b6-466e-a07a-741cc7bc716c" />
  <img width="18%" alt="telaAlunos-navegacaodoInicio" src="https://github.com/user-attachments/assets/8248e6b5-9304-4b95-926a-03310090a56b" />
  <img width="18%" alt="tela-alunos" src="https://github.com/user-attachments/assets/57362563-c130-4d25-8114-21019846643a" />
  <img width="18%" alt="perfil-professor" src="https://github.com/user-attachments/assets/f721ec75-0785-44bc-ab02-dbdea6708034" />
  <img width="18%" alt="MinhasTurmas" src="https://github.com/user-attachments/assets/81255f37-99c5-4ec9-8478-bc5b95c01856" />
  <img width="18%" alt="Inicio-Professor" src="https://github.com/user-attachments/assets/d7df9cff-beef-4cd0-a9d3-5be46008e0ce" />
  <img width="18%" alt="historicodeFrequecia" src="https://github.com/user-attachments/assets/361a65fd-671b-471d-8dca-23502da321d1" />
  <img width="18%" alt="fazerChamada" src="https://github.com/user-attachments/assets/ca7f29d6-79b3-490d-8038-449f827fbf6e" />
</p>

**Aluno / Responsável**

<p>
<img width="720" height="1600" alt="AlteraSenha" src="https://github.com/user-attachments/assets/dea0f9c1-3c9c-4a49-8127-4c9350c769d5" />
<img width="720" height="1600" alt="MinhaTurma" src="https://github.com/user-attachments/assets/d9a85558-62b7-46da-b152-2fb8e7d7912f" />
<img width="720" height="1600" alt="MinhaFrequecia" src="https://github.com/user-attachments/assets/6e770772-91b9-44fa-a25b-ea28e5ea1cda" />
<img width="720" height="1600" alt="meuPerfil" src="https://github.com/user-attachments/assets/deca95aa-5252-4bc6-a759-32ede9eabcd5" />
<img width="720" height="1600" alt="editarDados" src="https://github.com/user-attachments/assets/8a636d20-ec73-4209-953d-82878eb5aa79" />
<img width="720" height="1600" alt="DashboardAluno" src="https://github.com/user-attachments/assets/21d54b2d-0a21-4e0c-86ce-312c132f9925" />

</p>

## Funcionalidades

- [x] Login por perfil (Aluno/Responsável, Professor/Monitor e Gestor/Administrador)
- [x] Cadastro, consulta, alteração e exclusão de alunos, funcionários, cursos e turmas
- [x] Chamada com presença/ausência e observação opcional por aluno
- [x] Consulta de frequência e histórico de aulas pelo aluno/responsável
- [x] Avisos da coordenação exibidos no dashboard do aluno
- [x] Atualização de dados pessoais e de contato
- [x] Recuperação de senha
- [x] Controle de acesso por perfil (cada usuário só vê o que é da sua função)

## Casos de uso

| Ator | Casos de uso |
|------|--------------|
| **Responsável** | Consultar dados, atualizar dados, consultar aulas, consultar presença |
| **Administrador** | Manter aluno, manter funcionário, manter curso, manter turma |
| **Monitor Professor** | Consultar dados, fazer chamada |
| **Monitor Auxiliar** | Consultar dados |

## Regras de negócio

| Código | Regra |
|--------|-------|
| RN01 | Todo aluno, responsável ou funcionário possui um registro único em `tb_pessoa`, com o **CPF** como chave primária |
| RN02 | O aluno só pode ser matriculado em turma cuja faixa etária (`idade_min` e `idade_max`) inclua a sua idade |
| RN03 | Cada aluno pode estar matriculado em no máximo **2 cursos** ao mesmo tempo |
| RN04 | **2 faltas não justificadas** disparam alerta administrativo e o aluno fica sujeito à perda da vaga após análise pedagógica |
| RN05 | Toda turma deve ter no mínimo **1 monitor ou professor** associado |
| RN06 | Todo aluno menor de idade deve ter ao menos **1 responsável legal** vinculado (`tb_aluno_responsavel`) |

## Arquitetura

O SisGESC é dividido em três camadas. O app Android conversa diretamente com o **Supabase**, que expõe o banco PostgreSQL na nuvem por uma API REST.

```mermaid
flowchart TB
    subgraph APRESENTACAO["1. Apresentação"]
        A["App Android<br/>Java + XML"]
        A1["Módulo Administrador"]
        A2["Módulo Professor"]
        A3["Módulo Aluno"]
        A --- A1 & A2 & A3
    end
    subgraph NEGOCIO["2. Lógica de negócio (no app)"]
        B["Telas → Repository → Mapper → DTOs<br/>Retrofit"]
    end
    subgraph NUVEM["3. Dados na nuvem (Supabase)"]
        S["API REST (PostgREST)"]
        C[("PostgreSQL<br/>21 tabelas")]
        F["Storage<br/>fotos de perfil"]
        S --- C
        S --- F
    end
    A --> B
    B -- "HTTPS / JSON" --> S
```

- **Apresentação:** app Android em Java e XML, com três módulos (Administrador, Professor e Aluno).
- **Lógica de negócio:** dentro do app, cada módulo tem *Repository* (regras e acesso aos dados), *Mapper* (converte as respostas do banco) e *DTOs* (dados que as telas mostram). As chamadas HTTP são feitas com **Retrofit**.
- **Dados:** **Supabase**, que fornece o banco **PostgreSQL na nuvem** (tabelas `tb_*`), a API REST para acessá-lo e o *Storage* para as fotos de perfil.

## Tecnologias

- **App:** Java, XML Layouts, Android Studio, Retrofit
- **Banco de dados:** PostgreSQL na nuvem, hospedado no **Supabase** (API REST e Storage de fotos)
- **Integrações:** API de CEP para preencher endereços
- **Modelagem:** UML, DER (modelo conceitual, lógico e físico), dbdiagram.io
- **Distribuição:** instalação direta do APK (sem publicação na Play Store)

## Banco de dados

O banco é **PostgreSQL**, hospedado na nuvem pelo **Supabase**. Ele tem **21 tabelas** organizadas em módulos:

| Módulo | Tabelas |
|--------|---------|
| **Pessoa** | `tb_pessoa`, `tb_telefone`, `tb_cep`, `tb_endereco`, `tb_email` |
| **Aluno e Responsável** | `tb_aluno`, `tb_responsavel`, `tb_aluno_responsavel` |
| **Funcionários** | `tb_funcionario`, `tb_tipo_monitor`, `tb_cargo`, `tb_funcionario_cargo`, `tb_aviso` |
| **Curso e Turma** | `tb_curso`, `tb_turma`, `tb_matricula`, `tb_turma_funcionario` |
| **Frequência e Avaliação** | `tb_frequencia`, `tb_avaliacao` |
| **Autorização de Imagem** | `tb_autorizacao_imagem` |
| **Login** | `tb_login` |

Resumo dos relacionamentos principais:

```mermaid
erDiagram
    TB_PESSOA ||--o| TB_ALUNO : "é"
    TB_PESSOA ||--o| TB_RESPONSAVEL : "é"
    TB_PESSOA ||--o| TB_FUNCIONARIO : "é"
    TB_PESSOA ||--o| TB_LOGIN : "acessa"
    TB_ALUNO ||--o{ TB_ALUNO_RESPONSAVEL : "possui"
    TB_RESPONSAVEL ||--o{ TB_ALUNO_RESPONSAVEL : "responde por"
    TB_ALUNO ||--o{ TB_MATRICULA : "realiza"
    TB_CURSO ||--o{ TB_TURMA : "oferece"
    TB_TURMA ||--o{ TB_MATRICULA : "recebe"
    TB_MATRICULA ||--o{ TB_FREQUENCIA : "registra"
    TB_MATRICULA ||--o{ TB_AVALIACAO : "recebe"
    TB_FUNCIONARIO ||--o{ TB_TURMA_FUNCIONARIO : "atua em"
    TB_TURMA ||--o{ TB_TURMA_FUNCIONARIO : "tem equipe"
```

Modelo lógico completo: [dbdiagram.io](https://dbdiagram.io/d/BandaAlana_DER_OFICIAL-6aefb5f1f1d298b5739d4a77)

## Como rodar

Existem duas formas de executar o app: instalando o APK ou abrindo o projeto no Android Studio.

> **Não é preciso instalar banco de dados nem servidor.** O PostgreSQL está na nuvem (Supabase), então basta o aparelho ou o emulador ter **conexão com a internet**.

### Opção 1: instalar o APK

1. Baixe o arquivo `.apk` do projeto no celular Android.
2. Abra o arquivo. O Android vai pedir permissão para **instalar apps de fontes desconhecidas**, então autorize.
3. Conclua a instalação e abra o app **SisGESC**.

### Opção 2: abrir pelo Android Studio

**Pré-requisitos**

- [Android Studio](https://developer.android.com/studio) instalado
- JDK 17 ou 21 (o Android Studio já traz um JDK embutido)

**Passos**

1. Clone o repositório:

   ```bash
   git clone https://github.com/SEU_USUARIO/NOME_DO_REPOSITORIO.git
   ```

2. No Android Studio, vá em **File → Open** e selecione a pasta do projeto.
3. Aguarde o Gradle sincronizar as dependências.
4. Escolha onde executar o app:

   - **Emulador do Android Studio:** em **Device Manager**, crie ou selecione um dispositivo virtual.
   - **Celular Android:** ative as **Opções do desenvolvedor** e a **Depuração USB** no aparelho, conecte-o ao computador por cabo USB e autorize a conexão.

5. Clique em **Run ▶** para instalar e abrir o app.

## Fora do escopo

Por ser uma instituição de aulas **100% gratuitas**, o app **não terá**:

- Módulos financeiros (boletos, PIX, cartão, controle de caixa)
- Loja virtual ou venda de produtos e serviços
- Mensalidades, taxas de matrícula ou cobranças

## Segurança e LGPD

- Tratamento seguro de dados sensíveis de menores, responsáveis e autorizações de imagem (Lei nº 13.709/2018)
- Senhas armazenadas como **hash** (`senha_hash`)
- Controle de acesso por perfil (RBAC) e autenticação de usuários
- Banco PostgreSQL hospedado na nuvem (Supabase)
- Integridade referencial com chaves estrangeiras
- Backups periódicos do banco de dados

## Documentação

O documento completo do projeto (visão, casos de uso, requisitos, diagramas UML e DER, modelo físico, arquitetura, glossário e protótipos de interface) está em [`docs/`](docs).

## Equipe

| Função | Integrantes |
|--------|-------------|
| **Líder** | Madelayne Mendoza Huanca |
| **Analistas** | Izabel Linhares dos Santos, Vinicius Pereira de Oliveira |
| **Desenvolvedores** | Lucas Fonseca Shiraoka, Izabel Linhares dos Santos, Hanan Ibrahim El Khatib Sleiman, Beatriz Morais |
| **Testadores** | Rebeca Vieira de Souza, Kauan Marques Turra |
| **Projetistas de BD** | Laura Cristine Silva, Izabel Linhares dos Santos, Geovanna Silva Laurentino, Madelayne Mendoza Huanca |
| **Documentadoras** | Geovanna Silva Laurentino, Júlia Fidelis de Santana |

Projeto desenvolvido no curso de **Análise e Desenvolvimento de Sistemas** da **Universidade Cidade de São Paulo (UNICID)**, sob orientação do professor **Jadir Custodio Mendonça Junior**.

