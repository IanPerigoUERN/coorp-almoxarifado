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

## 📋 Funcionalidades Implementadas

- ☑️ Autenticação e autorização com JWT.
- ☑️ Cadastro, edição, exclusão e listagem de produtos (CRUD).
- ☑️ Controle de permissões: apenas usuários com perfil `ADMIN` podem excluir produtos.
- ☑️ Consulta avançada com filtros por nome, status de estoque e faixa de preço.
- ☑️ Geração de relatórios em PDF com a lista de produtos.
- ☑️ Interface responsiva e amigável.

