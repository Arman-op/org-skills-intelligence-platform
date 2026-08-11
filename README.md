# Organizational Knowledge Gap Intelligence Platform

> An enterprise Spring Boot + PostgreSQL platform designed to detect workforce skill gaps, deliver AI-driven personalized learning paths, and provide real-time competency analytics across an organization.

---

## 🚀 Overview

The **Organizational Knowledge Gap Intelligence Platform** bridges the gap between workforce capabilities and organizational goals. By continuously analyzing employee skill assessments against required role competency benchmarks, the platform identifies missing skills and proficiency deficiencies, automatically triggering tailored AI recommendations and mentorship matches.

---

## ✨ Key Features & Modules

### 1. 🔍 Gap Detection & Competency Analytics Module
- **Automated Gap Calculation**: Evaluates employee skills against required job role competencies ($TargetScore - CurrentScore = GapScore$).
- **Risk & Severity Classification**: Classifies each skill gap into actionable risk tiers (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`).
- **Target Role Career Planning**: Allows employees to evaluate skill readiness against target future roles or internal promotions without overwriting current baseline metrics.
- **Departmental & Org Metrics**: Aggregates organization-wide readiness percentages, top missing skills, and risk distributions for HR & L&D managers.
- **Automated Alerts**: Automatically triggers alert notifications when critical skill gaps ($\ge 3.0$ gap score) are detected.

### 2. 🤖 AI-Powered Training Recommendation Module
- **Personalized Learning Paths**: Reads stored employee skill gaps and generates tailored, per-skill training recommendations referencing their exact job role, current proficiency, and targeted growth areas.
- **Dual LLM Engine Support**: Seamlessly supports both **Google Gemini** (`gemini-3.6-flash`) and **OpenAI** (`gpt-4o-mini`) APIs.
- **Resilient 3-Tier Architecture**:
  1. **Mock Mode (`llm.mock.enabled=true`)**: Generates realistic, structured mock recommendations locally for fast development without burning API credits.
  2. **Live LLM Integration**: Sends structured prompts to Gemini / OpenAI APIs requesting JSON output with resource type recommendations (`Course`, `Article`, `Practice Project`) and priority rankings.
  3. **Rule-Based Fallback Engine**: If the LLM API is unreachable or rate-limited, the system gracefully falls back to rule-based explanations—ensuring 100% uptime for end-users.
- **Auto-Regeneration**: Automatically purges outdated recommendations and generates fresh ones whenever new gap analyses are computed.

### 3. 🔐 Security & User Management
- **JWT Stateless Authentication**: Secure token-based authentication with BCrypt password hashing.
- **Role-Based Access Control (RBAC)**: Enforces role permissions across `EMPLOYEE`, `MANAGER`, `HR_ADMIN`, and `LND_ADMIN`.

### 4. 🤝 Mentorship & Notifications
- **Mentorship Matching**: Matches employees with high-proficiency internal mentors to close critical skill gaps.
- **Real-Time Notification System**: Notifies users of gap alerts and mentorship request updates.

---

## 🛠️ Technology Stack

| Domain | Technology |
|--------|------------|
| **Backend Framework** | Java 17, Spring Boot 3.3.2 |
| **Security** | Spring Security, JJWT (`0.12.6`), BCrypt |
| **Data & Persistence** | Spring Data JPA, Hibernate ORM |
| **Databases** | H2 In-Memory (Development), PostgreSQL (Production) |
| **AI / LLM Integration** | Google Gemini API (`v1beta`), OpenAI Chat Completions API |
| **Utilities** | Jackson JSON, Lombok, Maven 3.9+ |

---

## 📡 API Reference

### 🔑 Authentication (`/api/auth`)
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `POST` | `/api/auth/register` | Register a new user account | Public |
| `POST` | `/api/auth/login` | Authenticate user and return JWT token | Public |
| `GET` | `/api/auth/me` | Fetch profile of currently authenticated user | Authenticated |
| `PUT` | `/api/auth/profile` | Update user profile information | Authenticated |

### 📊 Gap Analysis (`/api/gaps`)
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `GET` | `/api/gaps/user/{userId}` | Calculate & return real-time skill gaps for user | Authenticated |
| `GET` | `/api/gaps/user/{userId}/stored` | Fetch previously stored gap analysis records | Authenticated |
| `GET` | `/api/gaps/user/{userId}/summary` | Retrieve overall readiness score & risk distribution | Authenticated |
| `GET` | `/api/gaps/user/{userId}/missing` | Get list of completely missing required skills | Authenticated |
| `GET` | `/api/gaps/user/{userId}/proficiency-gaps` | Get skills where proficiency is below requirement | Authenticated |
| `POST` | `/api/gaps/user/{userId}/compare-target` | Compare user capabilities against a target career role | Authenticated |
| `GET` | `/api/gaps/department/{department}` | Get aggregated department-wide gap metrics | Manager / Admin |
| `GET` | `/api/gaps/org-summary` | Get organization-wide gap intelligence metrics | HR / L&D Admin |

### 💡 AI Recommendations (`/api/recommendations`)
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `POST` | `/api/recommendations/{employeeId}` | Generate fresh AI recommendations (deletes old set) | Authenticated |
| `GET` | `/api/recommendations/{employeeId}` | Get latest saved recommendations ordered by priority | Authenticated |

---

## 💻 Configuration & Setup

The configuration is managed via `src/main/resources/application.yml`.

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Server HTTP Port | `8080` |
| `JWT_SECRET` | 32+ character JWT secret key | `change-me-to-a-secure-secret` |
| `OPENAI_API_KEY` | API Key for Google Gemini or OpenAI | *(Empty)* |
| `OPENAI_MODEL` | AI Model Name | `gemini-3.6-flash` |
| `OPENAI_BASE_URL` | LLM API Endpoint URL | `https://generativelanguage.googleapis.com/v1beta/openai/chat/completions` |
| `LLM_MOCK_ENABLED` | Toggle mock mode vs live LLM calls | `true` |
| `DB_URL` | PostgreSQL Connection URL (when switching to Postgres) | `jdbc:postgresql://localhost:5432/org_skills` |

---

## 🏃 Running Locally

### Prerequisites
- **JDK 17** or higher
- **Maven 3.9+** (or use local Maven installation)

### 1. Build the Project
```powershell
mvn clean compile
```

### 2. Run Automated Unit Tests
```powershell
mvn test
```

### 3. Start the Backend Application
```powershell
# Default run (Mock mode enabled, Port 8080)
mvn spring-boot:run

# Or run with a custom port
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

### 4. Running with Live AI (Google Gemini / OpenAI)
To enable live AI generation, pass your API key as an environment variable:
```powershell
$env:OPENAI_API_KEY="your-gemini-or-openai-api-key"
$env:LLM_MOCK_ENABLED="false"
mvn spring-boot:run
```

### 5. Access H2 Database Console
When running in development mode, access the database UI at:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:org_skills`
- **User**: `sa`
- **Password**: *(leave empty)*

---

## ⚙️ Switching from H2 to PostgreSQL

When moving to production PostgreSQL:
1. In `pom.xml`, uncomment the `postgresql` dependency and comment out `h2`.
2. In `application.yml`, uncomment the PostgreSQL datasource block:
```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/org_skills}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:your_password}
    driver-class-name: org.postgresql.Driver
```

---

## 🧪 Testing Coverage

The application includes unit tests for core services and controllers:
- `GapAnalysisServiceTest`: Verifies gap calculation logic, missing skills detection, risk severity classification, and summary aggregation.
- `RecommendationServiceTest`: Verifies mock mode, LLM draft parsing, rule-based fallback handling, delete-before-save behavior, and priority sorting.
- `GapAnalysisControllerTest`: Verifies API security and response contracts.

Run all tests:
```powershell
mvn test
```
