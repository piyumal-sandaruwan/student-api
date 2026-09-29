# Student Management REST API

A simple Student Management REST API built with **Spring Boot, Java, Maven, Spring Data JPA, and MySQL**.

This project focuses on building a clean backend REST API with CRUD operations, validation, exception handling, database persistence, and automated testing.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [Project Structure](#3-project-structure)
4. [API Specification](#4-api-specification)
5. [Prerequisites](#5-prerequisites)
6. [Database Setup](#6-database-setup)
7. [Configuration](#7-configuration)
8. [Running the Application](#8-running-the-application)
9. [Testing](#9-testing)
10. [Building the Application](#10-building-the-application)
11. [Running the JAR](#11-running-the-jar)
12. [Future DevOps Integration](#12-future-devops-integration)

---

## 1. Project Overview

`student-api` is a backend REST API for managing student records.

The application provides basic CRUD operations:

- Create students
- Retrieve students
- Update students
- Delete students
- Validate request data
- Handle application exceptions
- Persist data using MySQL
- Run automated tests using H2

### Application Architecture

```text
Client
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
MySQL