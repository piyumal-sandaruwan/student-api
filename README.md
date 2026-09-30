# Student Management REST API

A simple Student Management REST API built with **Spring Boot, Java, Maven, Spring Data JPA, and MySQL**.

## Features

- Create students
- Get all students
- Get student by ID
- Update students
- Delete students
- Request validation
- Exception handling
- Automated testing

## Technology Stack

- Java
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- JUnit 5
- H2

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/students` | Get all students |
| GET | `/api/students/{id}` | Get student by ID |
| POST | `/api/students` | Create student |
| PUT | `/api/students/{id}` | Update student |
| DELETE | `/api/students/{id}` | Delete student |

## Run Locally

### 1. Configure MySQL

Create the database:

    CREATE DATABASE student_local;

### 2. Configure Environment Variables

Set the following environment variables:

    DB_HOST=localhost
    DB_PORT=3306
    DB_NAME=student_local
    DB_USERNAME=your_username
    DB_PASSWORD=your_password

### 3. Run the Application

    mvn spring-boot:run

The API will be available at:

    http://localhost:8080

## Run Tests

    mvn test

## Build

    mvn clean package

## Project Architecture

    Controller
        ↓
    Service
        ↓
    Repository
        ↓
    MySQL

## Future Development

DevOps and CI/CD features such as **Docker, GitLab CI/CD, Jenkins, and AWS deployment** will be implemented in the next stage of the project.