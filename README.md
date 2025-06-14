# URL Shortener

A full-stack URL shortener application built with Java Spring Boot (backend) and React TypeScript (frontend). The service allows users to shorten long URLs, manage them with a modern web interface, and provides comprehensive search and deletion capabilities.

## ✨ Features

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

## 🛠 Technologies Used

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

## 📋 Prerequisites

- **Docker** and **Docker Compose** installed on your system
- **Git** for cloning the repository
- **Java 17+** and **Maven** (for local development)
- **Node.js 18+** and **npm** (for frontend development)

## 🚀 Quick Start

### Using Docker Compose (Recommended)

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd urlshortener
   ```

2. **Start all services:**
   ```bash
   docker-compose up --build
   ```

3. **Access the applications:**
   - **Frontend**: http://localhost:3000
   - **Backend API**: http://localhost:8080
   - **API Documentation**: http://localhost:8080/swagger-ui/index.html
   - **MongoDB**: localhost:27017

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

## 📚 API Documentation

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
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI Specs**: http://localhost:8080/v3/api-docs

## 🗄 Database Schema

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

## 🧪 Testing

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

## 🔧 Configuration

### Environment Variables

#### Backend Configuration
- `SPRING_PROFILES_ACTIVE`: Set to `production` or `test`
- `SPRING_DATA_MONGODB_URI`: MongoDB connection string
- `SERVICE_CONFIG_SERVICEURL`: Base URL for short URL generation

#### Frontend Configuration
- `VITE_API_URL`: Backend API base URL (default: http://localhost:8080)

### Development vs Production

#### Development (docker-compose.yml)
- Uses local MongoDB instance
- Hot reload enabled for frontend
- Debug logging enabled
- API accessible at localhost:8080
- Frontend dev server at localhost:3000

#### Production Configuration
- Optimized Docker images
- Production-ready MongoDB setup
- Compressed frontend assets
- Environment-specific configurations

## 🛡 Error Handling

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

## 🌐 Internationalization

The frontend supports multiple languages:
- **English** (default)
- **Turkish**

Language detection is automatic based on browser settings, with manual switching available.

## 🔄 Architecture

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

## 📊 Performance Features

- **Pagination**: Efficient handling of large datasets
- **Debounced Search**: Optimized search performance
- **Connection Pooling**: MongoDB connection optimization
- **Caching**: Browser caching for static assets
- **Lazy Loading**: On-demand resource loading

## 🚧 Future Enhancements

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
