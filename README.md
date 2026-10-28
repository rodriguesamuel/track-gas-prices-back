# Track Gas Prices — Backend

API REST para controle de **abastecimentos de combustível** e **manutenção de veículos**, com autenticação via JWT e relatórios de custos.

## Tecnologias

- **Java 21**
- **Spring Boot 3.4.1** (Web, Security, Data JPA, Validation)
- **PostgreSQL** + **Flyway** (migrations)
- **JWT** (Nimbus JOSE + JWT)
- **Lombok** e **MapStruct**
- **Maven**

## Pré-requisitos

- JDK 21 (ex.: [Eclipse Temurin 21](https://adoptium.net))
- Maven 3.9+
- PostgreSQL (local ou gerenciado, ex.: Neon)

## Configuração

A aplicação lê as configurações de variáveis de ambiente. Crie um arquivo `.env` na raiz do projeto (ele é ignorado pelo Git):

```env
# String de conexão completa do PostgreSQL
DATABASE_URL=postgresql://usuario:senha@host:5432/trackgas?sslmode=require

# Segredo do JWT (mínimo 32 caracteres)
JWT_SECRET=troque-este-segredo-por-um-valor-seguro-min-32-chars
```

As propriedades em `application.yml` aceitam também estas variáveis diretamente:

| Variável      | Descrição                         | Padrão                                      |
|---------------|-----------------------------------|---------------------------------------------|
| `DB_URL`      | URL JDBC do banco                 | `jdbc:postgresql://localhost:5432/trackgas` |
| `DB_USER`     | Usuário do banco                  | `postgres`                                  |
| `DB_PASSWORD` | Senha do banco                    | *(vazio)*                                   |
| `JWT_SECRET`  | Segredo para assinatura do JWT    | *(valor de desenvolvimento)*                |
| `PORT`        | Porta do servidor                 | `8080`                                      |

> O script `run.ps1` converte automaticamente `DATABASE_URL` (formato `postgresql://...`) nas propriedades `DB_URL`, `DB_USER` e `DB_PASSWORD`.

## Como executar

### Windows (PowerShell)

```powershell
.\run.ps1
```

O script configura o JDK 21, carrega o `.env` e inicia `mvn spring-boot:run`.

### Multiplataforma (Maven)

```bash
mvn -s settings.xml spring-boot:run
```

> **Dependências via Maven Central.** O projeto inclui um `settings.xml` próprio que resolve as
> dependências direto do Maven Central, ignorando qualquer mirror corporativo (ex.: JFrog Artifactory)
> configurado no `~/.m2/settings.xml` global. Use sempre `-s settings.xml` nos comandos Maven
> (`mvn -s settings.xml clean package`, etc.). O `run.ps1` já faz isso automaticamente.

A aplicação sobe em `http://localhost:8080`. As migrations do Flyway são aplicadas automaticamente na inicialização.

### Build

```bash
mvn -s settings.xml clean package
java -jar target/trackgas-backend-0.0.1-SNAPSHOT.jar
```

## Autenticação

Todos os endpoints (exceto `/api/auth/**`) exigem o header:

```
Authorization: Bearer <token>
```

O token é obtido via `POST /api/auth/register` ou `POST /api/auth/login`.

## Endpoints

### Auth — `/api/auth`
| Método | Rota        | Descrição                       |
|--------|-------------|---------------------------------|
| POST   | `/register` | Cria usuário e retorna o token  |
| POST   | `/login`    | Autentica e retorna o token     |

### Veículos — `/api/vehicles`
| Método | Rota     | Descrição                 |
|--------|----------|---------------------------|
| GET    | `/`      | Lista veículos do usuário |
| GET    | `/{id}`  | Busca veículo por id      |
| POST   | `/`      | Cria veículo              |
| PUT    | `/{id}`  | Atualiza veículo          |
| DELETE | `/{id}`  | Remove veículo            |

### Abastecimentos — `/api/fuel-fillups`
| Método | Rota     | Descrição                                             |
|--------|----------|-------------------------------------------------------|
| GET    | `/`      | Lista (filtros: `vehicleId`, `from`, `to`)            |
| GET    | `/{id}`  | Busca por id                                          |
| POST   | `/`      | Cria abastecimento                                    |
| PUT    | `/{id}`  | Atualiza abastecimento                                |
| DELETE | `/{id}`  | Remove abastecimento                                  |

### Manutenção — `/api/maintenance`
| Método | Rota               | Descrição                                      |
|--------|--------------------|------------------------------------------------|
| GET    | `/schedules`       | Lista planos de manutenção (`vehicleId`)       |
| POST   | `/schedules`       | Cria plano de manutenção                       |
| PUT    | `/schedules/{id}`  | Atualiza plano                                 |
| DELETE | `/schedules/{id}`  | Remove plano                                   |
| GET    | `/expenses`        | Lista despesas (`vehicleId`, `from`, `to`)     |
| GET    | `/expenses/{id}`   | Busca despesa por id                           |
| POST   | `/expenses`        | Cria despesa                                   |
| PUT    | `/expenses/{id}`   | Atualiza despesa                               |
| DELETE | `/expenses/{id}`   | Remove despesa                                 |

### Relatórios — `/api/reports`
Todos aceitam os filtros opcionais `vehicleId`, `from`, `to`.

| Método | Rota            | Descrição                               |
|--------|-----------------|-----------------------------------------|
| GET    | `/fuel`         | Relatório de combustível                |
| GET    | `/maintenance`  | Relatório de manutenção                 |
| GET    | `/consolidated` | Relatório consolidado (custos totais)   |

## Modelo de dados

- **users** — usuários da aplicação
- **vehicles** — veículos vinculados ao usuário
- **fuel_fillups** — abastecimentos (custo total é calculado: `litros × preço/litro`)
- **maintenance_schedules** — planos de manutenção (por km ou período)
- **maintenance_expenses** — despesas de manutenção

Ver detalhes em `src/main/resources/db/migration/V1__init.sql`.

## Estrutura do projeto

```
src/main/java/com/trackgas
├── config/          # CORS, Security, JWT properties
├── domain/
│   ├── auth/        # autenticação e usuários
│   ├── vehicle/     # veículos
│   ├── fuel/        # abastecimentos
│   ├── maintenance/ # planos e despesas de manutenção
│   └── report/      # relatórios
└── shared/          # utilitários e tratamento de exceções
```

## Testes

```bash
mvn test
```
