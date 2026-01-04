# 🛡️ Enterprise IAM Service (Identity & Access Management)

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Security](https://img.shields.io/badge/Spring_Security-6-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)

> Um microsserviço de autenticação e autorização robusto, projetado para ambientes corporativos SaaS, implementando padrões modernos de segurança, RBAC, Multi-tenancy real e Rotação de Tokens.

---

## 🏗️ Arquitetura e Fluxos de Segurança

O projeto segue uma **Arquitetura em Camadas** estrita. Abaixo, detalho os dois fluxos principais de autenticação.

### 1. Fluxo de Autenticação (Login & Access)
O acesso aos recursos protegidos é feito via **JWT Stateless**.

```mermaid
sequenceDiagram
    participant User
    participant Filter as JWT Filter
    participant Controller
    participant Service
    
    User->>Filter: Request (Header: Bearer Access_Token)
    alt Token Inválido/Expirado
        Filter-->>User: 403 Forbidden
    else Token Válido
        Filter->>Controller: Forward Request (User Context)
        Controller->>Service: Regra de Negócio (Auditoria Automática)
        Service-->>Controller: Response DTO
        Controller-->>User: 200 OK
    end
```

### 2. Fluxo de Token Rotation (Refresh Token)
Para aumentar a segurança, o Access Token tem vida curta. O Refresh Token (salvo no banco) permite renovação sem login, com **rotação automática** (uso único) para prevenir roubo de sessão.

```mermaid
sequenceDiagram
    participant User
    participant AuthController
    participant RefreshService
    participant DB as PostgreSQL

    User->>AuthController: POST /refresh-token
    AuthController->>RefreshService: validaToken(refresh_token)
    RefreshService->>DB: Busca e Verifica Expiração
    
    alt Token Válido
        RefreshService->>DB: Deleta Antigo (Rotação)
        RefreshService->>DB: Salva Novo Token
        RefreshService-->>AuthController: Retorna Novo Par de Tokens
        AuthController-->>User: 200 OK (Access + Refresh)
    else Token Inválido/Reutilizado
        RefreshService-->>AuthController: Exception
        AuthController-->>User: 403 Forbidden (Requer novo Login)
    end
```

---

## 💾 Modelagem de Dados (ER Diagram)

O sistema implementa **Multi-tenancy Relacional**. Usuários e Permissões (Roles) pertencem estritamente a uma Empresa (`COMPANIES`).

```mermaid
erDiagram
    COMPANIES ||--o{ USERS : "emprega"
    COMPANIES ||--o{ ROLES : "define"
    USERS ||--o{ REFRESH_TOKENS : "possui ativo"
    USERS ||--o{ USER_ROLES : "tem permissao"
    ROLES ||--o{ USER_ROLES : "atribuido a"
    
    COMPANIES {
        UUID id PK
        String name
        String cnpj
        Boolean active
    }
    
    USERS {
        UUID id PK
        String email UK
        String password
        UUID company_id FK
    }
    
    ROLES {
        UUID id PK
        String name "Ex: ROLE_ADMIN"
        UUID company_id FK
    }

    REFRESH_TOKENS {
        UUID id PK
        String token
        Instant expiry_date
        UUID user_id FK
    }
```

---

## 🚀 Funcionalidades Enterprise

Diferenciais técnicos implementados além do básico:

| Funcionalidade | Implementação Técnica | Benefício |
| :--- | :--- | :--- |
| **Secure Token Rotation** | `RefreshTokenService` | Se um token for roubado, ele vale por pouco tempo. O refresh token é invalidado após o uso. |
| **Multi-tenancy Real** | Entidade `Company` + Relationships | Isolamento lógico de dados. Um usuário da "Google" não acessa dados da "Microsoft". |
| **Auditoria Automática** | `@EntityListeners(AuditingEntityListener.class)` | Rastreio automático de `created_at` e `updated_at` sem sujar o código de negócio. |
| **Tratamento de Erros** | `@RestControllerAdvice` | Padronização de respostas de erro (401, 403, 404) em formato JSON amigável. |
| **RBAC Dinâmico** | `@PreAuthorize("hasRole('ADMIN')")` | Proteção granular de endpoints baseada em cargos. |

---

## 📦 Como Rodar o Projeto

### Pré-requisitos
*   Docker & Docker Compose
*   Java 17+ (Opcional se usar Docker)
*   Maven

### Passo a Passo

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/murylomarques/enterprise-iam-service.git
    ```

2.  **Inicie a Infraestrutura (Postgres + Redis):**
    ```bash
    docker-compose up -d
    ```

3.  **Execute a Aplicação:**
    ```bash
    ./mvnw spring-boot:run
    ```

4.  **Acesse a Documentação Interativa (Swagger):**
    *   URL: `http://localhost:8080/swagger-ui/index.html`

---

## 🧪 Testando a API (Exemplos)

### 1. Criar Empresa e Usuário (Registro)
O sistema detecta se a empresa existe. Se não, cria uma nova (Tenant onboarding).
**POST** `/auth/register`
```json
{
  "firstName": "Murylo",
  "lastName": "CEO",
  "email": "ceo@tech.com",
  "password": "123",
  "companyId": "Minha Startup SaaS"
}
```

### 2. Renovação de Acesso (Refresh Token)
**POST** `/auth/refresh-token`
```json
{
  "refreshToken": "COLE_SEU_UUID_AQUI"
}
```

---

## 👨‍💻 Autor

Desenvolvido por **Murylo Marques**. Focado em Arquitetura de Software e Java Enterprise.

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/murylo-marques)