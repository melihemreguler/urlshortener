// API configuration
// Primary source: Runtime config (window.ENV), then .env file (VITE_API_URL)
// This fallback should only be used during development if .env is missing
const DEFAULT_API_URL = 'http://localhost:8080';

// Check for runtime configuration first, then build-time environment variables
const getRuntimeConfig = (): string => {
  // @ts-ignore - window.ENV is loaded from config.js
  if (typeof window !== 'undefined' && window.ENV && window.ENV.VITE_API_URL) {
    // @ts-ignore
    return window.ENV.VITE_API_URL;
  }
  return import.meta.env.VITE_API_URL || DEFAULT_API_URL;
};

export const API_BASE_URL = getRuntimeConfig();

// Log configuration in development
if (import.meta.env.DEV) {
  console.log('API Base URL:', API_BASE_URL);
  // @ts-ignore
  if (typeof window !== 'undefined' && window.ENV && window.ENV.VITE_API_URL) {
    console.log('Using runtime configuration');
  } else if (!import.meta.env.VITE_API_URL) {
    console.warn('VITE_API_URL not found in environment, using fallback:', DEFAULT_API_URL);
  }
}

// Swagger UI URL
export const SWAGGER_UI_URL = `${API_BASE_URL}/swagger-ui.html`;

// API endpoints
export const API_ENDPOINTS = {
  URLS: `${API_BASE_URL}/url`,
  SEARCH: `${API_BASE_URL}/url/search`,
  DELETE_URL: (id: string) => `${API_BASE_URL}/url/${id}`,
} as const;

// Helper function to create short URL from shortCode
export const buildShortUrl = (shortCode: string): string => {
  // Short URLs should use the base domain without /api path for redirects
  const baseUrl = API_BASE_URL.replace('/api', '');
  return `${baseUrl}/${shortCode}`;
};
