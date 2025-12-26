# Sobre o projeto

Projeto de autenticação desenvolvido com Spring Boot, que expõe endpoints RESTful para autenticação de usuários utilizando JWT. O projeto utiliza o banco de dados relacional H2 para persistência, além de Spring Security para proteger os endpoints e validar credenciais. Inclui práticas de testes automatizados com JUnit e Mockito, gerenciamento de dependências e build com Maven, e está configurado para integração contínua (CI), permitindo validar automaticamente a funcionalidade da aplicação.

## Endpoints disponíveis

- **Autenticação / Registro**

    - `POST http://localhost:8080/auth/login` – Realiza login do usuário
        - **Request Body:**
          ```json
          {
            "login": "usuario",
            "password": "senha123"
          }
          ```
        - **Response Body:**
          ```json
          {
            "token": "jwt_token_aqui"
          }
          ```

    - `POST http://localhost:8080/auth/register` – Cria um novo usuário
        - **Request Body:**
          ```json
          {
            "login": "novoUsuario",
            "password": "senha123",
            "role": "USER"
          }
          ```
        - **Response:** HTTP 201 Created se sucesso, HTTP 409 Conflict se o usuário já existir

## Tecnologias utilizadas

- Java 17
- Spring Boot 4
- Spring Security
- JPA / Hibernate
- JWT (JSON Web Token)
- Lombok
- Maven
- Banco de Dados H2 (em memória)
- JUnit / Mockito
- Integração contínua (CI) com GitHub Actions

## Como executar o projeto

### Pré-requisitos
- Java 17 ou superior
- Maven 3.8 ou superior

```bash
# clonar repositório
git clone https://github.com/DaviBrazz/autenticacao-api-springboot.git

# entrar na pasta do projeto
cd autenticacao-api-springboot

# executar o projeto
mvn spring-boot:run

```
## Autor: `Davi Braz`