# Spring Boot Task Project

## Description
This project is a Spring Boot REST API for managing books.
It was implemented as part of the Java / Spring Boot task assignment.

The application supports:
- CRUD operations for books
- validation of request data
- exception handling
- authentication and authorization with OAuth2 + JWT
- database migrations with Flyway
- unit and controller tests

---

## Implemented Tasks

### Task 2 — CRUD REST API
Implemented a REST API for managing books:

- `POST /api/books` — create a new book
- `GET /api/books` — get all books
- `GET /api/books/{id}` — get book by id
- `PUT /api/books/{id}` — update book
- `DELETE /api/books/{id}` — delete book

Validation is implemented using Jakarta Validation annotations.

Global exception handling is implemented with `@RestControllerAdvice`.

---

### Task 3 — Security
Authentication and authorization are implemented with:

- Spring Security
- OAuth2 Resource Server
- JWT
- Keycloak

Access rules:
- `USER` role can read books (`GET`)
- `ADMIN` role can create, update, and delete books (`POST`, `PUT`, `DELETE`)

If the user is not authenticated, API returns `401 Unauthorized`.

If the user does not have enough permissions, API returns `403 Forbidden`.

---

### Task 5 — Flyway
Database schema is managed with Flyway migrations.

Migration files:
- `V1__create_books_table.sql`
- `V2__insert_books.sql`

Flyway creates the `books` table and inserts test data.

---

### Task 7 — Tests
Implemented tests:
- `BookServiceTest` — unit tests for service layer
- `BookControllerTest` — controller tests for REST endpoints and security rules

---

## Technologies Used

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- JWT
- Keycloak
- H2 Database
- Flyway
- Maven
- JUnit 5
- Mockito
- MockMvc

---

## Project Structure

```text
src/main/java/com/example
├── config
│   ├── SecurityConfig.java
│   └── KeycloakRoleConverter.java
├── controller
│   └── BookController.java
├── entity
│   └── Book.java
├── exception
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository
│   └── BookRepository.java
├── service
│   └── BookService.java
└── SpringBootTask2Application.java

src/main/resources
├── application.properties
└── db
    └── migration
        ├── V1__create_books_table.sql
        └── V2__insert_books.sql