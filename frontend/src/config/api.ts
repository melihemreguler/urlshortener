// API configuration
// Primary source: .env file (VITE_API_URL)
// This fallback should only be used during development if .env is missing
const DEFAULT_API_URL = 'http://localhost:8080';

export const API_BASE_URL = import.meta.env.VITE_API_URL || DEFAULT_API_URL;

// Log configuration in development
if (import.meta.env.DEV) {
  console.log('API Base URL:', API_BASE_URL);
  if (!import.meta.env.VITE_API_URL) {
    console.warn('VITE_API_URL not found in environment, using fallback:', DEFAULT_API_URL);
  }
}

// Swagger UI URL
export const SWAGGER_UI_URL = `${API_BASE_URL}/swagger-ui/index.html`;

// API endpoints
export const API_ENDPOINTS = {
  URLS: `${API_BASE_URL}/api/url`,
  SEARCH: `${API_BASE_URL}/api/url/search`,
  DELETE_URL: (id: string) => `${API_BASE_URL}/api/url/${id}`,
} as const;

// Helper function to create short URL from shortCode
export const buildShortUrl = (shortCode: string): string => {
  return `${API_BASE_URL}/${shortCode}`;
};
