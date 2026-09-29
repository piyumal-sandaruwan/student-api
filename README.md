# Student Management REST API (`student-api`)

A clean, minimalist Spring Boot REST API engineered specifically as a **DevOps and CI/CD learning project**.

The business logic is intentionally simple so you can focus 100% on mastering **Maven, automated testing, containerization with Docker, multi-environment profiles, GitLab CI/CD pipelines, container registries, and remote Linux/AWS deployments**.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [Project Structure](#3-project-structure)
4. [API Specification](#4-api-specification)
5. [Prerequisites](#5-prerequisites)
6. [Database Setup (MySQL)](#6-database-setup-mysql)
7. [Spring Profiles Architecture](#7-spring-profiles-architecture)
8. [Running Locally](#8-running-locally)
   - [Running with Local Profile](#running-with-local-profile)
   - [Running with Dev Profile](#running-with-dev-profile)
   - [Running with Prod Profile](#running-with-prod-profile)
9. [Automated Testing (`mvn test`)](#9-automated-testing-mvn-test)
10. [Building the Executable JAR (`mvn package`)](#10-building-the-executable-jar-mvn-package)
11. [Running the Executable JAR](#11-running-the-executable-jar)
12. [Docker Image Build & Run](#12-docker-image-build--run)
13. [Local Containerized Development with Docker Compose](#13-local-containerized-development-with-docker-compose)
14. [GitLab CI/CD Pipeline Architecture](#14-gitlab-cicd-pipeline-architecture)
15. [GitLab CI/CD Variables Setup](#15-gitlab-cicd-variables-setup)
16. [GitLab Environments & Branch Promotion](#16-gitlab-environments--branch-promotion)
17. [Future Deployment to AWS EC2 / Ubuntu Server](#17-future-deployment-to-aws-ec2--ubuntu-server)

---

## 1. Project Overview

`student-api` provides basic CRUD (Create, Read, Update, Delete) management for student records.

### Key Characteristics:
- **Zero bloat**: No authentication, JWT, or message brokers to distract from DevOps concepts.
- **Strict multi-profile isolation**: Seamless transitions between `local`, `dev`, and `prod` configurations without changing a single line of Java code.
- **Fail-safe automated testing**: Fast, in-memory H2 test suite that validates the context, repositories, service logic, and REST controllers during CI/CD.
- **Cloud-ready packaging**: Self-contained executable JAR and lightweight Docker container.

---

## 2. Technology Stack

- **Language / Runtime**: Java 25
- **Framework**: Spring Boot 4.x / Spring Framework 7
- **Data Access**: Spring Data JPA & Hibernate
- **Database**: MySQL 8.x / 9.x (Production/Local/Dev) & H2 (Automated Test Suite)
- **Validation**: Jakarta Bean Validation (`@NotBlank`, `@Email`)
- **Build Tool**: Apache Maven 3.9+
- **Testing**: JUnit 5, Mockito, Spring Boot Test, MockMvc, AssertJ
- **Containers**: Docker & Docker Compose
- **CI/CD**: GitLab CI/CD with Docker-in-Docker (dind) and GitLab Container Registry

---

## 3. Project Structure

```
student-api/
│
├── .gitlab-ci.yml                      # 5-Stage GitLab CI/CD Pipeline
├── Dockerfile                          # Java 25 production container image
├── compose.yaml                        # Local multi-container development (App + MySQL)
├── pom.xml                             # Maven build configuration & dependencies
├── README.md                           # Comprehensive documentation
│
├── scripts/
│   └── deploy.sh                       # Ubuntu/EC2 deployment helper script
│
├── src/
│   ├── main/
│   │   ├── java/com/piyumal/student_api/
│   │   │   ├── controller/
│   │   │   │   └── StudentController.java          # REST API endpoints (/api/students)
│   │   │   ├── service/
│   │   │   │   └── StudentService.java             # Business & validation logic
│   │   │   ├── repository/
│   │   │   │   └── StudentRepository.java          # Spring Data JPA interface
│   │   │   ├── entity/
│   │   │   │   └── Student.java                    # JPA Student entity
│   │   │   ├── dto/
│   │   │   │   ├── StudentRequest.java             # Validation request payload
│   │   │   │   └── StudentResponse.java            # Clean JSON response model
│   │   │   ├── exception/
│   │   │   │   ├── BadRequestException.java        # 400 Bad Request
│   │   │   │   ├── ResourceNotFoundException.java  # 404 Not Found
│   │   │   │   ├── ErrorResponse.java              # Structured JSON error DTO
│   │   │   │   └── GlobalExceptionHandler.java     # Centralized exception handler
│   │   │   └── StudentApiApplication.java          # Application entrypoint
│   │   │
│   │   └── resources/
│   │       ├── application.properties              # Shared configuration & default profile
│   │       ├── application-local.properties        # LOCAL profile database settings
│   │       ├── application-dev.properties          # DEV profile database settings
│   │       └── application-prod.properties         # PROD profile database settings
│   │
│   └── test/
│       ├── java/com/piyumal/student_api/
│       │   ├── StudentApiApplicationTests.java     # Spring context verification
│       │   ├── controller/
│       │   │   └── StudentControllerTest.java      # MockMvc endpoint & HTTP status tests
│       │   ├── service/
│       │   │   └── StudentServiceTest.java         # Mockito unit tests
│       │   └── repository/
│       │       └── StudentRepositoryTest.java      # JPA persistence tests
│       │
│       └── resources/
│           └── application-test.properties         # H2 in-memory test configuration
```

---

## 4. API Specification

Base URL: `http://localhost:8080/api/students`

| Method | Endpoint | Description | Success Code | Sample Request Body |
|--------|----------|-------------|--------------|---------------------|
| `GET` | `/api/students` | Retrieve all students | `200 OK` | None |
| `GET` | `/api/students/{id}` | Retrieve a student by ID | `200 OK` | None |
| `POST` | `/api/students` | Create a new student | `201 Created` | See below |
| `PUT` | `/api/students/{id}` | Update existing student | `200 OK` | See below |
| `DELETE` | `/api/students/{id}` | Delete student by ID | `204 No Content` | None |

### Sample POST/PUT Request Payload:
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "course": "Computer Science"
}
```

### Sample 201 Created / 200 OK Response:
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "course": "Computer Science"
}
```

### Sample 400 Bad Request (Validation Error):
```json
{
  "timestamp": "2026-09-27T22:45:00.123",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed for one or more fields",
  "path": "/api/students",
  "validationErrors": {
    "email": "Invalid email format",
    "name": "Name is required"
  }
}
```

---

## 5. Prerequisites

- **Java**: JDK 25 installed (`java -version`)
- **Maven**: 3.9+ installed (`mvn -version`)
- **MySQL**: Running locally on port `3306` (or via Docker)
- **Docker & Docker Compose**: (Optional for local JAR, required for containers)

---

## 6. Database Setup (MySQL)

For local development with an existing MySQL installation:

1. Connect to MySQL client:
   ```bash
   mysql -u root -p
   ```
2. Create the local database:
   ```sql
   CREATE DATABASE IF NOT EXISTS student_local;
   ```
3. Hibernate automatically creates and updates the `students` table on startup because `spring.jpa.hibernate.ddl-auto=update` is enabled.

---

## 7. Spring Profiles Architecture

The application enforces strict separation of environments through Spring Boot profiles:

```
                  SAME APPLICATION ARTIFACT (JAR)
                                 │
           ┌─────────────────────┼─────────────────────┐
           │                     │                     │
         LOCAL                  DEV                  PROD
           │                     │                     │
       Local PC              Dev Server           Production
    (student_local)        (student_dev)        (student_prod)
           │                     │                     │
    localhost:3306          DB_HOST:3306         DB_HOST:3306
```

### Configuration Files Breakdown:

| File | Role | Credentials Strategy |
|------|------|----------------------|
| `application.properties` | Common settings, default profile (`local`), port 8080 | None |
| `application-local.properties` | Local PC development (`student_local`) | Local defaults (`root`, password from `$DB_PASSWORD` or blank) |
| `application-dev.properties` | Remote Dev server (`student_dev`) | Sourced from environment variables: `$DB_HOST`, `$DB_USERNAME`, `$DB_PASSWORD` |
| `application-prod.properties` | Remote Production (`student_prod`) | Sourced strictly from environment variables: `$DB_HOST`, `$DB_USERNAME`, `$DB_PASSWORD` |
| `application-test.properties` | Automated test suite (`src/test/resources`) | Self-contained in-memory H2 database |

---

## 8. Running Locally

### Running with Local Profile (Default)

If your local MySQL root password is empty:
```bash
mvn spring-boot:run
```

If your local MySQL root user has a password:
- **Windows (PowerShell)**:
  ```powershell
  $env:DB_PASSWORD="your_password"
  mvn spring-boot:run
  ```
- **Linux / macOS**:
  ```bash
  DB_PASSWORD="your_password" mvn spring-boot:run
  ```

---

### Running with Dev Profile

Simulates the development server environment:
- **PowerShell**:
  ```powershell
  $env:SPRING_PROFILES_ACTIVE="dev"
  $env:DB_HOST="localhost"
  $env:DB_PORT="3306"
  $env:DB_NAME="student_dev"
  $env:DB_USERNAME="root"
  $env:DB_PASSWORD="your_password"
  mvn spring-boot:run
  ```
- **Linux / macOS**:
  ```bash
  export SPRING_PROFILES_ACTIVE=dev
  export DB_HOST=localhost
  export DB_PORT=3306
  export DB_NAME=student_dev
  export DB_USERNAME=root
  export DB_PASSWORD=your_password
  mvn spring-boot:run
  ```

---

### Running with Prod Profile

Simulates the production environment:
- **PowerShell**:
  ```powershell
  $env:SPRING_PROFILES_ACTIVE="prod"
  $env:DB_HOST="localhost"
  $env:DB_PORT="3306"
  $env:DB_NAME="student_prod"
  $env:DB_USERNAME="root"
  $env:DB_PASSWORD="your_password"
  mvn spring-boot:run
  ```
- **Linux / macOS**:
  ```bash
  export SPRING_PROFILES_ACTIVE=prod
  export DB_HOST=localhost
  export DB_PORT=3306
  export DB_NAME=student_prod
  export DB_USERNAME=root
  export DB_PASSWORD=your_password
  mvn spring-boot:run
  ```

---

## 9. Automated Testing (`mvn test`)

The test suite runs with an isolated in-memory H2 database (`application-test.properties`), making it fast, deterministic, and 100% independent of any external MySQL instance.

Run tests:
```bash
mvn test
```

### Tests Included:
1. **Context Load Test** ([`StudentApiApplicationTests`](file:///D:/Devops/student-api/src/test/java/com/piyumal/student_api/StudentApiApplicationTests.java)): Ensures Spring Boot ApplicationContext initializes properly.
2. **Controller Tests** ([`StudentControllerTest`](file:///D:/Devops/student-api/src/test/java/com/piyumal/student_api/controller/StudentControllerTest.java)): Tests GET, POST, PUT, DELETE, and Bean Validation error responses (400) using `MockMvc`.
3. **Service Unit Tests** ([`StudentServiceTest`](file:///D:/Devops/student-api/src/test/java/com/piyumal/student_api/service/StudentServiceTest.java)): Tests business logic, exception handling, and duplicate email prevention using Mockito.
4. **Repository Tests** ([`StudentRepositoryTest`](file:///D:/Devops/student-api/src/test/java/com/piyumal/student_api/repository/StudentRepositoryTest.java)): Tests JPA entity queries, uniqueness verification, and persistence.

If any test fails, Maven exits with a non-zero exit code, which immediately fails the GitLab CI/CD test stage.

---

## 10. Building the Executable JAR (`mvn package`)

To compile, test, and package the complete application into an executable fat JAR:

```bash
mvn clean package
```

The resulting JAR will be created in `target/`:
```
target/student-api-0.0.1-SNAPSHOT.jar
```

---

## 11. Running the Executable JAR

Run with default (`local`) profile:
```bash
java -jar target/student-api-0.0.1-SNAPSHOT.jar
```

Run with explicit profile and parameters:
```bash
java -jar target/student-api-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=dev \
  --spring.datasource.password=your_password
```

Or via environment variables:
```bash
DB_PASSWORD="your_password" java -jar target/student-api-0.0.1-SNAPSHOT.jar
```

---

## 12. Docker Image Build & Run

### 1. Build the Docker Image
Ensure you packaged the JAR first (`mvn package -DskipTests`).

```bash
docker build -t student-api:latest .
```

Tagged with Git commit short SHA (DevOps best practice):
```bash
docker build -t student-api:v1.0.0 .
```

### 2. Run the Container
Connect the container to an external MySQL instance:

```bash
docker run -d \
  --name student-api \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=local \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=3306 \
  -e DB_NAME=student_local \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=your_password \
  student-api:latest
```

View container logs:
```bash
docker logs -f student-api
```

---

## 13. Local Containerized Development with Docker Compose

To run the entire stack (Spring Boot container + MySQL 8.4 container with persistence volume) without installing MySQL on your host machine:

### Start the environment:
```bash
docker compose up -d --build
```

### What happens:
1. Spins up `student-mysql` on port `3306` with database `student_local` and password `rootpassword`.
2. Runs healthcheck `mysqladmin ping` until MySQL is ready.
3. Builds the `student-api` image and starts the container on port `8080`.
4. Connects `student-api` to `student-mysql` through the private Docker bridge network `student-net`.

### Verify running containers:
```bash
docker compose ps
```

### Stop and remove containers (preserving data volume):
```bash
docker compose down
```

### Stop and remove containers and database volume:
```bash
docker compose down -v
```

---

## 14. GitLab CI/CD Pipeline Architecture

The pipeline is defined in [`.gitlab-ci.yml`](file:///D:/Devops/student-api/.gitlab-ci.yml).

### Pipeline Stages:

```
[Git Push]
    │
    ▼
┌────────────────────────────────────────────────────────┐
│ 1. BUILD   : mvn compile (caches dependencies)        │
└────────────────────────────────────────────────────────┘
    │
    ▼
┌────────────────────────────────────────────────────────┐
│ 2. TEST    : mvn test (generates JUnit report)         │
└────────────────────────────────────────────────────────┘
    │
    ▼
┌────────────────────────────────────────────────────────┐
│ 3. PACKAGE : mvn package -DskipTests (archives JAR)    │
└────────────────────────────────────────────────────────┘
    │
    ▼
┌────────────────────────────────────────────────────────┐
│ 4. DOCKER  : docker build & push to GitLab Registry    │
│              (tagged with $CI_COMMIT_SHORT_SHA)        │
└────────────────────────────────────────────────────────┘
    │
    ├───────────────────────────────┐
    ▼                               ▼
[Branch: develop]              [Branch: main]
┌───────────────────────────┐  ┌───────────────────────────┐
│ 5a. DEPLOY: DEVELOPMENT   │  │ 5b. DEPLOY: PRODUCTION    │
│ (Automatic deployment to  │  │ (Manual approval trigger  │
│  development environment) │  │  for safe production roll)│
└───────────────────────────┘  └───────────────────────────┘
```

---

## 15. GitLab CI/CD Variables Setup

In GitLab, navigate to:
**Settings** > **CI/CD** > **Variables** > **Add variable**

Configure the following variables (choose environment scope `development` or `production`):

| Variable Name | Description | Environment Scope | Masked? | Example Value |
|---------------|-------------|-------------------|---------|---------------|
| `DB_HOST` | Remote MySQL Host / RDS endpoint | dev / prod | No | `rds-mysql-dev.xxx.rds.amazonaws.com` |
| `DB_PORT` | MySQL Port | dev / prod | No | `3306` |
| `DB_NAME` | Database Name | dev / prod | No | `student_dev` or `student_prod` |
| `DB_USERNAME` | MySQL Username | dev / prod | No | `student_user` |
| `DB_PASSWORD` | MySQL Password | dev / prod | **Yes** | `SecretStrongPassword123!` |

> **Security Rule**: Never check database passwords into Git. Mask and protect sensitive variables in GitLab CI/CD settings.

---

## 16. GitLab Environments & Branch Promotion

GitLab Environments tracks deployments under **Operate** > **Environments**:

1. **`develop` branch**:
   - Every merge/push to `develop` automatically executes `build` -> `test` -> `package` -> `docker` -> `deploy_to_development`.
   - The environment is updated to `development`.

2. **`main` branch**:
   - Merge to `main` builds, tests, packages, and pushes the production Docker image.
   - The `deploy_to_production` job is configured with `when: manual`.
   - A team lead or engineer clicks the **Play** button in the GitLab Pipeline UI to promote and deploy to `production`.

---

## 17. Future Deployment to AWS EC2 / Ubuntu Server

When you are ready to deploy to an AWS EC2 Ubuntu instance:

### Step 1: Launch EC2 Instance
- OS: Ubuntu 24.04 LTS
- Instance Type: `t3.micro` or `t3.small`
- Security Group Inbound Rules:
  - Port `22` (SSH from your IP)
  - Port `8080` (HTTP from anywhere or load balancer)

### Step 2: Install Docker on Ubuntu
```bash
sudo apt-get update
sudo apt-get install -y docker.io
sudo usermod -aG docker ubuntu
newgrp docker
```

### Step 3: Log in to GitLab Container Registry on EC2
```bash
echo "<PERSONAL_ACCESS_TOKEN>" | docker login registry.gitlab.com -u <GITLAB_USERNAME> --password-stdin
```

### Step 4: Run the Deployment Helper Script
Clone your repository or copy [`scripts/deploy.sh`](file:///D:/Devops/student-api/scripts/deploy.sh) to the server:

```bash
chmod +x scripts/deploy.sh

# Run development deployment:
DB_HOST="your-rds-or-mysql-ip" \
DB_PORT="3306" \
DB_NAME="student_dev" \
DB_USERNAME="student_user" \
DB_PASSWORD="your_password" \
./scripts/deploy.sh dev registry.gitlab.com/your-username/student-api:a1b2c3d

# Run production deployment:
DB_HOST="your-prod-rds-endpoint" \
DB_PORT="3306" \
DB_NAME="student_prod" \
DB_USERNAME="student_user" \
DB_PASSWORD="your_password" \
./scripts/deploy.sh prod registry.gitlab.com/your-username/student-api:a1b2c3d
```

### Step 5: Test the Remote API
```bash
curl http://<EC2_PUBLIC_IP>:8080/api/students
```
