# URL Shortener

A full-stack URL shortener application built with Java Spring Boot (backend) and React TypeScript (frontend). The service allows users to shorten long URLs, manage them with a modern web interface, and provides comprehensive search and deletion capabilities.

## Table of Contents

- [Features](#features)
  - [Backend (Spring Boot)](#backend-spring-boot)
  - [Frontend (React + TypeScript)](#frontend-react--typescript)
  - [Infrastructure](#infrastructure)
- [Technologies Used](#technologies-used)
  - [Backend](#backend)
  - [Frontend](#frontend)
  - [Infrastructure](#infrastructure-1)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
  - [Local Development (with Docker & Local MongoDB)](#local-development-with-docker--local-mongodb)
  - [Production Deployment (with MongoDB Atlas)](#production-deployment-with-mongodb-atlas)
  - [Switching Between Environments](#switching-between-environments)
- [Local Development](#local-development)
- [API Documentation](#api-documentation)
  - [Core Endpoints](#core-endpoints)
  - [Interactive Documentation](#interactive-documentation)
- [Database Schema](#database-schema)
- [Testing](#testing)
  - [Backend Tests](#backend-tests)
  - [Frontend Tests](#frontend-tests)
- [Configuration](#configuration)
  - [Environment Variables](#environment-variables)
  - [Development vs Production](#development-vs-production)
  - [Database Configuration](#database-configuration)
- [Error Handling](#error-handling)
- [Internationalization](#internationalization)
- [Architecture](#architecture)
- [Performance Features](#performance-features)
- [Future Enhancements](#future-enhancements)

## Features

### Backend (Spring Boot)
- **URL Shortening**: Generate short URLs for given long URLs with duplicate detection
- **URL Redirection**: Redirect short URLs to their original destinations with access tracking
- **URL Management**: List, search, and delete URLs with pagination support
- **Search Functionality**: Full-text search across URLs and short codes (case-insensitive)
- **REST API**: Comprehensive RESTful API with proper error handling
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Health Monitoring**: Ping endpoint for service availability checks
- **Exception Handling**: Robust error handling with appropriate HTTP status codes
- **Data Validation**: Input validation with detailed error messages
- **Access Tracking**: Track how many times each short URL is accessed

### Frontend (React + TypeScript)
- **Modern Web Interface**: Responsive Material-UI design with dark/light theme support
- **Real-time Search**: Debounced search functionality with instant results
- **URL Management**: Create, view, search, and delete URLs with intuitive interface
- **Pagination**: Navigate through large sets of URLs efficiently
- **Undo Functionality**: Undo URL deletions with toast notifications
- **Internationalization**: Multi-language support (English/Turkish)
- **Error Handling**: User-friendly error messages and loading states
- **Responsive Design**: Works seamlessly on desktop and mobile devices

### Infrastructure
- **Containerization**: Full Docker Compose setup for easy deployment
- **Database**: MongoDB for reliable data persistence
- **Environment Configuration**: Flexible configuration for different environments
- **CORS Support**: Properly configured cross-origin resource sharing

## Technologies Used

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.4.1** - Web framework and dependency injection
- **Spring Data MongoDB** - Database integration
- **MongoDB 6.0.20** - NoSQL database for data persistence
- **Swagger/OpenAPI 3** - API documentation
- **JUnit 5 & Mockito** - Unit and integration testing
- **Maven** - Build and dependency management

### Frontend
- **React 19.1.0** - UI library
- **TypeScript** - Type-safe JavaScript
- **Material-UI (MUI) 7.1.1** - Component library and theming
- **Vite** - Build tool and development server
- **i18next** - Internationalization framework
- **ESLint** - Code linting and formatting

### Infrastructure
- **Docker & Docker Compose** - Containerization and orchestration
- **MongoDB 6.0.20** - Document database
- **Nginx** - Frontend web server (in production container)

## Prerequisites

- **Docker** and **Docker Compose** installed on your system
- **Git** for cloning the repository
- **Java 17+** and **Maven** (for local development)
- **Node.js 18+** and **npm** (for frontend development)

## Quick Start

### Local Development (with Docker & Local MongoDB)

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd urlshortener
   ```

2. **Start the application with local MongoDB:**
   ```bash
   docker-compose -f docker-compose.local.yaml --env-file .env.development up --build
   ```

3. **Access the applications:**
   - **Frontend**: http://localhost:3000
   - **Backend API**: http://localhost:8080
   - **API Documentation**: http://localhost:8080/swagger-ui.html
   - **MongoDB**: localhost:27017 (admin/secret)

### Production Deployment (with MongoDB Atlas)

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd urlshortener
   ```

2. **Configure production environment:**
   ```bash
   # Copy environment template
   cp .env.example .env.production
   
   # Edit .env.production with your actual MongoDB Atlas connection string and production URLs
   nano .env.production
   ```

   **⚠️ Security Note**: The `.env.production` file contains sensitive database credentials and is already added to `.gitignore`.

3. **Start the application:**
   ```bash
   docker-compose --env-file .env.production up --build
   ```

   **📋 Prerequisites for Production:**
   - Domain configured: `urlshortener.melihemre.dev`
   - nginx-proxy running on the server with `web` network
   - DNS A records pointing to your server IP
   - Project repository cloned on EC2: `/home/ubuntu/urlshortener`

   ** First-time EC2 Setup:**
   ```bash
   # On your EC2 server
   cd /home/ubuntu
   git clone https://github.com/YOUR_USERNAME/urlshortener.git
   cd urlshortener
   ```

### Automated Deployment (GitHub Actions)

The project includes automated CI/CD pipeline that:
- Builds Docker images on every push to `main` branch
- Pushes images to Docker Hub
- Deploys to EC2 server automatically

**🔧 Setup GitHub Secrets:**

Navigate to your GitHub repository → Settings → Secrets and variables → Actions, and add:

```bash
# Docker Hub
DOCKER_HUB_USERNAME=your_dockerhub_username
DOCKER_HUB_ACCESS_TOKEN=your_dockerhub_access_token

# EC2 Deployment
EC2_HOST=your_server_ip_or_domain
EC2_USER=ubuntu
EC2_SSH_KEY=your_private_ssh_key_content

# Application Environment
MONGODB_URI=your_mongodb_atlas_connection_string
SERVICE_URL=https://urlshortener.melihemre.dev
VITE_API_URL=https://urlshortener.melihemre.dev
LETSENCRYPT_EMAIL=your_email@domain.com
```

** Deployment Process:**
1. Push code to `main` branch
2. GitHub Actions automatically builds and pushes Docker images
3. Connects to EC2 and updates the application
4. Runs health checks to verify deployment

4. **Access the applications:**
   - **Frontend**: https://urlshortener.melihemre.dev
   - **Backend API**: https://urlshortener.melihemre.dev/api
   - **API Documentation**: https://urlshortener.melihemre.dev/api/swagger-ui.html

### Switching Between Environments

When switching from production to development (or vice versa), you may need to clean up Docker networks:

1. **Check existing networks:**
   ```bash
   docker network ls --filter "name=urlshortener"
   ```

2. **Remove the network if needed:**
   ```bash
   docker network rm urlshortener_default
   ```

3. **Then start with the desired environment:**
   ```bash
   # For development
   docker-compose -f docker-compose.local.yaml --env-file .env.development up --build
   
   # For production
   docker-compose --env-file .env.production up --build
   ```

### Local Development

#### Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

## API Documentation

### Core Endpoints

#### 1. Create Short URL
- **Endpoint:** `POST /api/url`
- **Request:**
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

#### 2. Redirect to Original URL
- **Endpoint:** `GET /{shortCode}`
- **Example:** `GET /abc123`
- **Response:** 302 redirect to the original URL

#### 3. List URLs (Paginated)
- **Endpoint:** `GET /api/url`
- **Parameters:** 
  - `page` (optional): Page number (0-based, default: 0)
  - `size` (optional): Items per page (default: 10)
- **Response:**
  ```json
  {
    "content": [...],
    "page": 0,
    "size": 10,
    "totalElements": 25,
    "totalPages": 3,
    "first": true,
    "last": false
  }
  ```

#### 4. Search URLs
- **Endpoint:** `GET /api/url/search`
- **Parameters:**
  - `q` (optional): Search term for URLs and short codes
  - `page` (optional): Page number (default: 0)
  - `size` (optional): Items per page (default: 10)
- **Response:** Same paginated format as list URLs

#### 5. Delete URL
- **Endpoint:** `DELETE /api/url/{id}`
- **Response:** 200 OK (empty body)

#### 6. Health Check
- **Endpoint:** `GET /api/ping`
- **Response:** `"pong"`

### Interactive Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Specs**: http://localhost:8080/v3/api-docs

## Database Schema

The application uses MongoDB with the following document structure:

```javascript
{
  "_id": "ObjectId",
  "longUrl": "https://example.com",
  "shortCode": "abc123",
  "createdAt": "2025-06-15T10:30:00Z",
  "accessCount": 42,
  "_class": "com.github.melihemreguler.urlshortener.dto.UrlDto"
}
```

## Testing

### Backend Tests
```bash
cd backend
mvn test
```

**Test Coverage:**
- **Unit Tests**: 52 tests covering all service and controller methods
- **Integration Tests**: 17 tests for MongoDB repository operations
- **Mocking**: Comprehensive mocking with Mockito
- **Test Profiles**: Separate test configuration for isolated testing

**Test Categories:**
- `UrlServiceTest`: Business logic testing (17 tests)
- `UrlControllerTest`: REST API endpoint testing (16 tests)
- `UrlRepositoryTest`: Database integration testing (17 tests)
- `RedirectControllerTest`: URL redirection testing (2 tests)

### Frontend Tests
```bash
cd frontend
npm run test  # (when configured)
```

## Configuration

### Environment Variables

The application uses environment files for configuration:

#### Production Environment (`.env.production`)
```bash
# MongoDB Atlas connection string
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/urlshortenerdb

# Service URL for generating short URLs (your production domain)
SERVICE_URL=https://your-production-domain.com

# Spring profile
SPRING_PROFILES_ACTIVE=production
```

#### Development Environment (`.env.development`)
```bash
# Local MongoDB connection (for Docker Compose)
MONGODB_URI=mongodb://admin:secret@mongodb:27017/urlshortenerdb?authSource=admin

# Service URL for development
SERVICE_URL=http://localhost:8080

# Spring profile
SPRING_PROFILES_ACTIVE=development
```

#### Frontend Configuration (`frontend/.env`)
```bash
# Backend API URL
VITE_API_URL=http://localhost:8080
```

### Development vs Production

#### Development Setup
- **MongoDB**: Local containerized instance (docker-compose.local.yaml)
- **Configuration**: Uses `.env.development`
- **Frontend**: Development server with hot reload
- **Database**: `mongodb://admin:secret@mongodb:27017/urlshortenerdb?authSource=admin`

#### Production Setup
- **MongoDB**: MongoDB Atlas cloud database
- **Configuration**: Uses `.env.production`
- **Frontend**: Optimized build served by nginx
- **Database**: MongoDB Atlas connection string

### Database Configuration

#### For MongoDB Atlas (Production)
1. Create a MongoDB Atlas cluster
2. Get your connection string from Atlas dashboard
3. Add it to `.env.production` as `MONGODB_URI`

#### For Local Development
1. Use `docker-compose.local.yaml` to start MongoDB container
2. MongoDB will be available at `mongodb://admin:secret@mongodb:27017`

```bash
# Start local development with MongoDB
docker-compose -f docker-compose.local.yaml up --build

# Start production deployment (requires .env.production)
docker-compose up --build
```

## Error Handling

The application implements comprehensive error handling:

### Backend Error Responses
- **400 Bad Request**: Invalid input, validation errors, malformed JSON
- **404 Not Found**: Short URL not found
- **500 Internal Server Error**: Unexpected server errors

### Frontend Error Handling
- User-friendly error messages
- Loading states and indicators
- Graceful fallbacks for network issues
- Toast notifications for user feedback

## Internationalization

The frontend supports multiple languages:
- **English** (default)
- **Turkish**

Language detection is automatic based on browser settings, with manual switching available.

## Architecture

### Backend Architecture
```
Controller Layer (REST endpoints)
    ↓
Service Layer (Business logic)
    ↓
Repository Layer (Data access)
    ↓
MongoDB Database
```

### Frontend Architecture
```
React Components
    ↓
Custom Hooks (useUrlManagement)
    ↓
API Layer (axios/fetch)
    ↓
Backend REST API
```

## Performance Features

- **Pagination**: Efficient handling of large datasets
- **Debounced Search**: Optimized search performance
- **Connection Pooling**: MongoDB connection optimization
- **Caching**: Browser caching for static assets
- **Lazy Loading**: On-demand resource loading

## Future Enhancements

- [ ] User authentication and authorization
- [ ] URL expiration dates
- [ ] Analytics dashboard
- [ ] Custom short code support
- [ ] QR code generation
- [ ] Bulk URL operations
- [ ] API rate limiting
- [ ] Email notifications
- [ ] URL preview functionality
- [ ] Advanced search filters
