# CreatorStage Studio - Backend API 🚀

Este diretório contém a API do back-end do **CreatorStage Studio** desenvolvida com **Java 21**, **Spring Boot 3** e **PostgreSQL**. A API gerencia os canais, as ideias de conteúdo (Kanban/Lista), o Moodboard (referências visuais/links), os roteiros de gravação e as integrações assíncronas com o Scraper em Python.

---

## 🛠️ Tecnologias e Dependências

* **Java 21** & **Spring Boot 3**
* **Spring Data JPA**: Abstração e persistência do banco de dados.
* **Spring Security & JWT**: Autenticação stateless baseada em Tokens JSON Web.
* **PostgreSQL**: Banco de dados relacional oficial.
* **Lombok**: Redução de código boilerplate (Getters, Setters, Builders).
* **RestTemplate**: Comunicação HTTP síncrona com o microserviço Scraper.
* **Spring Async**: Execução assíncrona (`@Async`) da raspagem de canais em segundo plano.

---

## 📂 Estrutura do Projeto (Pacotes)

* `com.yt.projetos.controller`: Endpoints REST expostos pela API.
* `com.yt.projetos.model`: Entidades JPA representando as tabelas do banco de dados.
* `com.yt.projetos.repository`: Interfaces de persistência Spring Data JPA.
* `com.yt.projetos.service`: Regras de negócio e integrações externas (ex: chamada de scraper, auth).
* `com.yt.projetos.config`: Configurações do Spring Security, CORS, JWT e Beans.

---

## ⚙️ Configurações e Variáveis de Ambiente

O arquivo de configuração principal está em `src/main/resources/application.properties` (ou `.yml`). Variáveis essenciais:

| Variável | Descrição | Padrão |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | URL de conexão do banco PostgreSQL | `jdbc:postgresql://localhost:5432/yt_platform` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco de dados | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco de dados | `postgres` |
| `SCRAPER_API_URL` | URL de chamada da API do Scraper | `http://localhost:8000/scrape` |
| `JWT_SECRET` | Chave de criptografia dos Tokens JWT | *Gerada automaticamente ou estática* |

---

## 🚀 Como Executar

### 1. Requisitos
* JDK 21+ instalado.
* Banco de dados PostgreSQL ativo (com base de dados criada como `yt_platform`).
* Maven (incluso através do wrapper `./mvnw`).

### 2. Rodando Localmente
Use o wrapper do Maven na raiz do diretório `backend`:
```bash
./mvnw spring-boot:run
```
*(No Windows, utilize `mvnw.cmd spring-boot:run`)*

A API estará disponível por padrão em: **`http://localhost:8080`**

### 3. Rodando com Docker
O container do backend é orquestrado através do Docker Compose localizado na raiz geral do projeto:
```bash
docker compose up -d --build backend
```

---

## 📡 Endpoints Principais

A API está protegida por Bearer Token JWT. O cabeçalho `Authorization: Bearer <TOKEN>` é exigido para a maioria das requisições.

* **Autenticação:**
  * `POST /api/auth/register` - Registro de novo usuário.
  * `POST /api/auth/login` - Login e obtenção do Token JWT.
* **Canais:**
  * `GET /api/channels` - Listar canais do usuário.
  * `POST /api/channels` - Cadastrar novo canal.
  * `DELETE /api/channels/{id}` - Excluir canal (requer confirmação de senha).
* **Ideias de Vídeo:**
  * `GET /api/channels/{channelId}/ideas` - Listar ideias de um canal.
  * `POST /api/channels/{channelId}/ideas` - Criar nova ideia.
  * `PUT /api/ideas/{id}` - Atualizar dados/etapa (Kanban).
* **Referências e Moodboard:**
  * `POST /api/ideas/{ideaId}/references` - Salvar link/imagem de referência.
  * `DELETE /api/references/{id}` - Remover referência.
* **Sugestões Automáticas (Scraper):**
  * `GET /api/channels/{channelId}/suggestions` - Obter vídeos em alta sugeridos pelo scraper.
