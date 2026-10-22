# 📦 Coorp - Sistema de Almoxarifado

Sistema corporativo simplificado para controle de almoxarifado, desenvolvido como atividade avaliativa da disciplina **Desenvolvimento de Sistemas Corporativos**, da UERN.

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 4.x**
- **Spring Data JPA** (Persistência)
- **Spring Security + JWT** (Autenticação e Autorização)
- **MariaDB** (Banco de Dados Relacional)
- **DynamicReports** (Geração de Relatórios em PDF)
- **HTML5, CSS3 e JavaScript** (Front-end)
- **Gradle** (Gerenciamento de Dependências)
- **Eclipse IDE** (Ambiente de Desenvolvimento)

## 🏗️ Arquitetura

O projeto segue o padrão **MVC (Model-View-Controller)**, organizado em três componentes:

1. **Model (Persistência):** entidades JPA (`Produto`, `Usuario`) e repositórios.
2. **Controller (Lógica de Negócios):** APIs REST (`ProdutoController`, `AuthController`, `RelatorioController`).
3. **View (Interface):** telas HTML, CSS e JavaScript que consomem a API.

## 🔧 Pré-requisitos para Execução

Antes de executar o projeto, certifique-se de ter instalado:

- **Java JDK 17** ou superior.
- **MariaDB** (ou MySQL).
- **Gradle:** o projeto já inclui o Gradle Wrapper, portanto não é necessário instalá-lo globalmente.
- Uma IDE de sua preferência (Eclipse, IntelliJ IDEA ou VS Code).

## 🛠️ Como Executar o Projeto

### 1. Configurar o Banco de Dados

Abra o terminal e acesse o MariaDB:

```bash
sudo mariadb -u root
```

Crie o banco de dados e o usuário:

```sql
CREATE DATABASE almoxarifado_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER 'coorp_user'@'localhost'
    IDENTIFIED BY 'SenhaSegura123';

GRANT ALL PRIVILEGES ON almoxarifado_db.*
    TO 'coorp_user'@'localhost';

FLUSH PRIVILEGES;

EXIT;
```

### 2. Configurar as Credenciais

Abra o arquivo `src/main/resources/application.properties` e configure as credenciais do banco de dados:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/almoxarifado_db
spring.datasource.username=coorp_user
spring.datasource.password=SenhaSegura123
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8081
```

**Observação:** substitua a senha de exemplo por uma senha segura no seu ambiente.

### 3. Executar a Aplicação

**Via Eclipse:**

1. Importe o projeto como um projeto Gradle.
2. Localize a classe `CoorpApplication.java`.
3. Clique com o botão direito na classe.
4. Selecione **Run As → Spring Boot App**.

**Via Terminal (Gradle Wrapper):**

Na raiz do projeto, execute:

```bash
./gradlew bootRun
```

### 4. Acessar o Sistema

Abra o navegador e acesse:

[http://localhost:8081](http://localhost:8081)

**Credenciais padrão** (criadas automaticamente na primeira execução):

| Perfil | Login | Senha |
|---|---|---|
| Administrador | `admin` | `admin123` |
| Usuário comum | `comum` | `123456` |

> **Atenção:** essas credenciais são destinadas ao ambiente de desenvolvimento. Altere-as antes de disponibilizar o sistema em produção.


## 🧪 Testes e Validação

Siga o roteiro abaixo para validar as funcionalidades do sistema, incluindo as regras de autenticação e autorização.

### 1. Teste de Login e Autorização de Usuários

O sistema possui dois usuários pré-cadastrados automaticamente no banco de dados:

| Perfil | Login | Senha | Permissões |
|---|---|---|---|
| **Administrador** | `admin` | `admin123` | Acesso total, incluindo exclusão de produtos |
| **Usuário comum** | `comum` | `123456` | Acesso restrito, sem permissão para excluir produtos |

**Passo a passo:**

1. Acesse [http://localhost:8081](http://localhost:8081). Você deverá ser redirecionado para a tela de login.
2. Entre com o usuário `admin` e a senha `admin123`. Você deverá ser redirecionado para a tela de cadastro de produtos.
3. Clique em **Sair**, no canto superior direito. Você deverá retornar à tela de login.
4. Entre com o usuário `comum` e a senha `123456`. Você deverá conseguir acessar a tela de cadastro normalmente.

### 2. Teste de Cadastro e Edição de Produtos

1. Faça login com `admin` ou `comum` e preencha o formulário na tela inicial com os seguintes dados:

   - **Nome:** `Caneta Esferográfica`
   - **Descrição:** `Material de escritório`
   - **Quantidade atual:** `50`
   - **Quantidade mínima:** `10`
   - **Preço:** `2.50`

2. Clique em **Cadastrar Produto**.
3. Verifique se o produto aparece na tabela com o status **✅ OK**.
4. Cadastre outro produto com quantidade abaixo do mínimo, utilizando os seguintes valores:
   - **Quantidade atual:** `5`
   - **Quantidade mínima:** `10`

   O status esperado é **⚠️ Baixo**.

5. **Teste de edição:** clique no botão **Editar** de um produto, altere a quantidade e selecione **Salvar Alterações**. O valor atualizado deverá aparecer na tabela.

### 3. Teste de Exclusão e Regras de Autorização

Este teste valida a regra de negócio implementada no `SecurityConfig`: **somente usuários com a role `ADMIN` podem excluir produtos**.

#### Cenário A — Usuário comum (acesso negado)

1. Faça login com `comum` / `123456`.
2. Na tabela de produtos, clique no botão **Excluir** de qualquer produto.
3. Verifique o resultado esperado:
   - O sistema deverá exibir um alerta de erro, como "Erro ao excluir o produto".
   - O produto deverá permanecer na tabela.
   - No console do navegador (pressione `F12`), a requisição HTTP `DELETE` deverá retornar o status **403 (Forbidden)**.

#### Cenário B — Administrador (acesso permitido)

1. Clique em **Sair** e faça login com `admin` / `admin123`.
2. Na tabela de produtos, clique no botão **Excluir** de qualquer produto.
3. Verifique se o produto é removido da tabela e se o sistema exibe uma mensagem de sucesso.

### 4. Teste de Consulta Avançada

1. Na tela principal, clique no botão **🔍 Consultar Produtos**, localizado no cabeçalho.
2. Teste os filtros individualmente e em conjunto:

   - **Filtro por nome:** digite `caneta` e clique em **Buscar**. A tabela deverá exibir apenas os produtos cujos nomes contenham esse termo.
   - **Filtro por status:** selecione **Estoque Baixo** e clique em **Buscar**. A tabela deverá exibir apenas os produtos que atendam ao critério de estoque baixo definido pelo sistema.
   - **Filtro por preço:** informe o preço mínimo `1.00` e o máximo `5.00` e clique em **Buscar**.
   - **Combinação de filtros:** informe o nome, selecione o status e defina a faixa de preço. Os resultados deverão respeitar todos os critérios selecionados.

3. Clique em **Limpar Filtros**. Verifique se a tabela é limpa e se o contador retorna a zero, conforme o comportamento implementado na aplicação.

### 5. Teste de Geração de Relatório PDF

1. Na tela de consultas, clique no botão **📄 Gerar Relatório PDF**.
2. Verifique se o navegador inicia o download do arquivo `relatorio_produtos.pdf`.
3. Abra o arquivo e confira se o relatório contém os produtos cadastrados e as seguintes colunas:
   - Nome
   - Descrição
   - Quantidade
   - Preço

#### Teste de segurança da rota

1. Abra uma janela anônima do navegador, sem uma sessão autenticada.
2. Tente acessar diretamente a seguinte URL:

   `http://localhost:8081/api/relatorios/produtos-dynamic`

3. **Resultado esperado:** a requisição deverá ser bloqueada, retornando **403 (Forbidden)**, caso a rota exija autenticação e não exista um token JWT válido.

   **Observação:** o código HTTP efetivamente retornado depende da configuração do Spring Security. Uma aplicação pode retornar `401 (Unauthorized)` para requisições sem autenticação e `403 (Forbidden)` para usuários autenticados sem permissão suficiente.



## 📋 Funcionalidades Implementadas

- ☑️ Autenticação e autorização com JWT.
- ☑️ Cadastro, edição, exclusão e listagem de produtos (CRUD).
- ☑️ Controle de permissões: apenas usuários com perfil `ADMIN` podem excluir produtos.
- ☑️ Consulta avançada com filtros por nome, status de estoque e faixa de preço.
- ☑️ Geração de relatórios em PDF com a lista de produtos.
- ☑️ Interface responsiva e amigável.

