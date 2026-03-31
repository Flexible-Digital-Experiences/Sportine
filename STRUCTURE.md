# Project Structure: Sportine

This document provides a high-level overview of the project structure for the Sportine platform.

## Root Directory

- **`Sportine/`**: **Android Mobile Application**
    - Built with Kotlin and Gradle.
    - Path: `/Sportine/Sportine`
- **`sportine_backend/`**: **Central Backend API**
    - Built with Spring Boot (Java 17+).
    - Contains business logic, database models, and REST controllers.
- **`sportine_frontend/`**: **Web Application**
    - Built with vanilla HTML, CSS, and Javascript.
    - Connects to the backend via REST API.
- **`sportine_db.sql`**: SQL dump for the MySQL database.

---

## Detailed Component Breakdown

### Backend (`/sportine_backend/backend`)
- **Controller**: REST API endpoints for authentication, social features, and training management.
- **Service**: Logic layer for processing data and handling business rules.
- **Repository**: DB interaction layer using Spring Data JPA.
- **Model**: JPA Entities representing the database tables.

### Web Frontend (`/sportine_frontend`)
- **Pages**: Contains HTML templates organized by user role (`alumno`, `entrenador`, `auth`).
- **JS/API**: Centralized `api.js` for handling AJAX/Fetch requests to the backend.
- **JS/Pages**: Javascript logic specific to each page.

### Mobile App (`/Sportine`)
- **App**: Main Android source code.
- **Gradle**: Build and dependency management.

---

## Key Features & Responsibilities
- **Authentication**: JWT-based security managed by the backend.
- **Social Module**: Handles friend requests, notifications, and social interactions (HTML: `social.html`, `buscar-amigo.html`).
- **Roles**: Distinct workflows for students (`alumno`) and coaches (`entrenador`).
