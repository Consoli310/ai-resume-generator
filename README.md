# AI Resume Generator

An intelligent backend application built on Spring Boot that parses resumes, tailors them to specific job descriptions using Google Gemini AI, and generates polished, downloadable PDF resumes.

---

## Features

- **User Authentication**: Secure user registration and login utilizing Spring Security, password hashing (BCrypt), and stateless JWT-based session management.
- **AI-Powered Resume Analysis**:
  - Extracts and parses core candidate information from raw resume text.
  - Tailors candidate profile descriptions, key achievements, and skills to match specific target job descriptions using Google Gemini AI models.
- **Automated Resume PDF Generation**: Compiles tailored resume data into HTML templates and exports them as highly professional, print-ready PDF files.
- **Fault-Tolerant API Integration**: Implements robust Gemini API communications with JSON cleanup utilities and fallback strategies to handle unexpected AI responses.
- **Centralized Error Handling**: Detailed API responses for validation errors, conflict states, authentication issues, and external AI integration faults.
- **OpenAPI / Swagger documentation**: Built-in interactive API exploration and client generation configurations.

---

## Tech Stack

- **Framework**: Spring Boot 3.x (Spring Web, Spring Security, Spring Data JPA)
- **Database**: Postgresql via Neon
- **Security**: JSON Web Token (JWT) with `io.jsonwebtoken` and BCrypt encryption
- **AI Integration**: Google Gemini API via Spring `RestClient`
- **Template Engine & PDF Generation**: HTML-to-PDF compilation mechanisms
- **JSON Processing**: Jackson ObjectMapper with specialized deserialization logic (e.g., custom string list deserializer)
- **API Documentation**: Springdoc OpenAPI / Swagger UI
- **Build Tool**: Maven (Wrapper included)
- **Containerization**: Docker & Docker Compose

---

## Architecture

The project adheres to a clean, layered architecture:

```
src/main/java/consoli/resume/
├── ai/                      # AI Client integration layer (Gemini API DTOs & client implementation)
├── config/                  # Configuration beans (Jackson, RestClient, Security, OpenAPI)
├── controller/              # Web Controller layer exposing API endpoints
├── dto/                     # Data Transfer Objects for requests and responses
├── entity/                  # Database JPA Entities
├── exception/               # Custom exceptions and the global controller advice handler
├── pdf/                     # HTML generation and PDF compilation services
├── repository/              # Database repositories
├── security/                # JWT filters, UserDetailsService, and Security configuration
└── service/                 # Core business services (Auth and Resume processing logic)
```

---

## API Endpoints

### Authentication

#### Register a New User
- **HTTP Method**: `POST`
- **Route**: `/auth/register`
- **Purpose**: Creates a new user account with secure password hashing.
- **Authentication**: None (Public)

#### User Login
- **HTTP Method**: `POST`
- **Route**: `/auth/login`
- **Purpose**: Validates user credentials and issues a JWT token.
- **Authentication**: None (Public)

---

### Resume Management

#### Download Tailored Resume
- **HTTP Method**: `POST`
- **Route**: `/api/resume/download`
- **Purpose**: Submits candidate data and target job description to produce and download the tailored PDF resume.
- **Authentication**: Bearer JWT token required

---

## Security

The application uses stateless token-based authorization via Spring Security and JWT:
1. **Password Hashing**: User passwords are encrypted using `BCryptPasswordEncoder` before storage.
2. **Authentication Filter (`JwtFilter`)**: Every incoming request to secure endpoints must contain a `Authorization: Bearer <token>` header. The filter extracts and decodes the token using a secret key to establish the authenticated context inside Spring Security.
3. **Endpoint Access**: Auth controller endpoints (`/auth/**`) are completely public, while API endpoints (`/api/**`) are protected and require a valid authenticated session.

---

## AI Integration

The system communicates with Google Gemini API via a configured `RestClient`:
- **Resume Parsing**: Converts unstructured text inputs into organized DTOs (`CandidateProfileDTO`).
- **Tailoring**: Optimizes the parsed profile against a target job description to highlight relevant skills and rewrite experience descriptions.
- **JSON Safety**: Since raw LLM responses can contain markdown or auxiliary text, the client includes a robust `cleanJsonResponse` sanitizer to isolate and parse the exact JSON response structure reliably.

---

## PDF Generation

PDF generation is handled by compiling HTML into print-ready PDF documents:
1. **HTML Template Construction**: `ResumeHtmlServiceImpl` takes the customized candidate profile and formats it into standard HTML using a robust structure defined in `resume-template.html`.
2. **Text Sanitization**: `ResumeTextSanitizer` cleans string and list elements to prevent formatting breaks.
3. **PDF Generation**: `ResumePdfServiceImpl` takes the compiled HTML and renders it into raw PDF bytes, returned to the user with appropriate content-delivery headers for instant download.

---

## Installation

### Prerequisites

- Java 17 or higher
- Maven 3.x (or use the provided `./mvnw` wrapper)
- A valid Google Gemini API Key

### Steps

1. Clone this repository:
   ```bash
   git clone https://github.com/consoli/resume.git
   cd resume
   ```

2. Configure your environment variables (see below).

3. Build the application:
   ```bash
   ./mvnw clean install
   ```

---

## Environment Variables

Ensure you define the following environment variables or system properties:

| Variable Name | Description | Default / Example Value |
| :--- | :--- | :--- |
| `JWT_SECRET` | Secret key used to sign and verify JWT tokens. Must be at least 256 bits. | `your-very-secure-randomly-generated-test-secret-key-32-chars-min` |
| `GEMINI_API_KEY` | API Key used to authenticate with Google Gemini. | `AIzaSy...` |
| `SPRING_DATASOURCE_URL` | JDBC database URL. | `jdbc:h2:mem:testdb` (In-Memory H2 for local dev) |
| `SPRING_DATASOURCE_USERNAME` | Username for database connection. | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Password for database connection. | *(empty)* |

---

## Running the Application

### Locally with Maven Wrapper
Run the application locally using:
```bash
./mvnw spring-boot:run
```

### With Docker Compose
A `compose.yaml` and `Dockerfile` are available at the root of the project to package and run the application containerized:
```bash
docker compose up --build
```

---

## Swagger Documentation

When the application is running, you can explore the API endpoints, view detailed DTO schemas, and perform interactive requests through the Swagger UI:

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI Description (JSON)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## Deployment

The application features a production-ready multi-stage `Dockerfile` and can be seamlessly deployed on platforms like **Render**, Fly.io, or AWS.

To deploy on Render:
1. Create a **Web Service** on Render and link your Git repository.
2. Select the **Docker** runtime.
3. Add your required environment variables (`GEMINI_API_KEY`, `JWT_SECRET`, and optionally custom database settings) inside Render's Dashboard.

---

## Future Improvements

- **Database Persistence**: Add options to save parsed and tailored resumes directly to the user's account for history tracking.
- **Multiple PDF Templates**: Provide distinct template options (e.g., Creative, Minimalist, Executive) for PDF compilation.
- **Third-Party Integrations**: Support imports from LinkedIn or GitHub profiles.
- **Response Caching**: Introduce Redis caching for similar resume/job description pairings to optimize API costs.

---

## Author

**Matheus Consoli**

Java Backend Developer with a strong foundation in Spring Boot, REST APIs, PostgreSQL, Spring Security, Docker, and software engineering best practices. This project was developed as part of my professional portfolio to demonstrate backend development skills, API design, authentication and authorization, AI integration, and PDF generation.

- LinkedIn: https://linkedin.com/in/matheus-consoli
- Email: matheusconsoli310@gmail.com
