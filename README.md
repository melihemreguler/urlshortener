# URL Shortener

This project is a URL shortener service built using Java and Spring Boot. The service allows users to shorten long URLs and retrieve the original URLs using a short code.

## Features
- Generate short URLs for given long URLs.
- Redirect short URLs to their original long URLs.
- Swagger API documentation available.
- Ping endpoint to check service availability.
- Exception handling for validation errors and non-existing short codes.
- MongoDB as the database.

## Technologies Used
- Java 17
- Spring Boot
- MongoDB
- Docker & Docker Compose
- JUnit & Mockito for testing
- Swagger for API documentation

## Setup and Installation

### Prerequisites
- Install [Docker](https://www.docker.com/get-started)
- Install [Docker Compose](https://docs.docker.com/compose/install/)

### Running the Application
To start the application, use the following command:

```sh
docker-compose up --build
```

This command will:
- Start a MongoDB container.
- Build and run the URL shortener backend service.

The backend service will be available at: `http://localhost:8080`

## API Endpoints

### 1. Create a Short URL
- **Endpoint:** `POST /api/url`
- **Request Body:**
  ```json
  {
    "longUrl": "https://example.com"
  }
  ```
- **Response:**
  ```json
  {
    "shortUrl": "http://localhost:8080/abc123"
  }
  ```

### 2. Redirect to Long URL
- **Endpoint:** `GET /{shortCode}`
- **Example:** `GET /abc123`
- **Response:** Redirects to the original URL.

### 3. Health Check (Ping)
- **Endpoint:** `GET /api/ping`
- **Response:**
  ```json
  "pong"
  ```

### 4. Swagger UI
- Access API documentation at: `http://localhost:8080/swagger-ui/index.html#`
- API specs available at: `http://localhost:8080/v3/api-docs`

## Exception Handling
The application handles exceptions for:
- **Invalid requests:** Returns HTTP 400 (Bad Request) with validation error messages.
- **Short URL not found:** Returns HTTP 404 (Not Found) if the short code does not exist.

## Database
The service uses MongoDB to store URL mappings. The schema includes:
- `longUrl` (Original URL)
- `shortCode` (Generated short URL code)
- `createdAt` (Timestamp of creation)
- `accessCount` (Number of times the short URL was accessed)

## Running Tests
To run unit tests:
```sh
mvn test
```
