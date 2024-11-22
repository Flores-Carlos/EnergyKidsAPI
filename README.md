# Energy Kids API - Global Solutions

## Sobre o Projeto
A **Energy Kids API** é uma solução desenvolvida como parte do projeto **Global Solutions** com foco em **sustentabilidade energética**. A aplicação permite:
- Gerenciar usuários, dispositivos, consumos de energia e dicas de economia.
- Estimar o consumo mensal de energia elétrica com base em dispositivos cadastrados.
- Enviar dicas de economia personalizadas para ajudar na redução do consumo energético.

Este projeto foi desenvolvido pelos integrantes da equipe **Turma 2TDSPX**:
- **Carlos Gabriel de Freitas Flores Ferreira** (RM97528)
- **Kaique Gabriel Toschi** (RM551165)
- **Vinicius Ariel Monteiro Teixeira** (RM98836)

---

## Entregáveis

### Código Fonte
O código completo da API está disponível neste repositório.

### Links de Deploys em Nuvem + Acessos
- **API Base URL**: [https://energy-kids.herokuapp.com](https://energy-kids-api-415c22a2d7d8.herokuapp.com)
    - **Usuário**: admin
    - **Senha**: admin123

### Vídeo Pitch
- **Link do Pitch**: [Placeholder - Substitua pelo link do vídeo no YouTube ou equivalente]

### Vídeo Demonstrativo
- **Link da Demonstração**: [Placeholder - Substitua pelo link do vídeo no YouTube ou equivalente]

---

## Estrutura do Projeto

- **`src/main/java`**: Contém o código-fonte principal da API.
    - **Controllers**: Gerencia os endpoints REST.
    - **Models**: Representa as entidades do banco de dados.
    - **Repositories**: Interface para operações com o banco de dados usando Spring Data JPA.
    - **Services**: Implementa regras de negócio e integração com RabbitMQ.
    - **Configurations**: Configuração do Spring Security, RabbitMQ, etc.

- **`src/main/resources`**:
    - **`application.properties`**: Configurações de banco de dados, segurança e outras propriedades da aplicação.

---

## Instruções para Testes

### Requisitos:
- **Postman** ou ferramenta similar para chamadas REST.
- Acesso ao banco de dados Oracle configurado na nuvem.


### Configuração:
1. Clone este repositório:
   ```bash
   git clone https://github.com/Flores-Carlos/EnergyKidsAPI
   ```
2. Configure as variáveis de ambiente no arquivo `application.properties`:
   ```properties
   spring.datasource.url=jdbc:oracle:thin:@//oracle.fiap.com.br:1521/orcl
   spring.datasource.username=rm97528
   spring.datasource.password=290301
   ```
3. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```

### Endpoints Principais:
- **Usuários**:
    - `POST /usuarios`: Cadastro de usuário.
    - `GET /usuarios`: Listar usuários com paginação.
    - `PUT /usuarios/{id}`: Atualizar usuário.
    - `DELETE /usuarios/{id}`: Excluir usuário.

- **Dispositivos**:
    - `POST /dispositivos`: Cadastro de dispositivo vinculado a um usuário.
    - `GET /dispositivos`: Listar dispositivos com paginação.
    - `PUT /dispositivos/{id}`: Atualizar dispositivo.
    - `DELETE /dispositivos/{id}`: Excluir dispositivo.

- **Consumos**:
    - `POST /consumos`: Registro de consumo associado a um dispositivo.
    - `GET /consumos`: Listar consumos com paginação.
    - `PUT /consumos/{id}`: Atualizar consumo.
    - `DELETE /consumos/{id}`: Excluir consumo.

- **Dicas de Economia**:
    - `POST /dicas`: Cadastro de dica de economia.
    - `GET /dicas`: Listar dicas com paginação.
    - `PUT /dicas/{id}`: Atualizar dica.
    - `DELETE /dicas/{id}`: Excluir dica.

---
