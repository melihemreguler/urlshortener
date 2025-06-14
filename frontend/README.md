# URL Shortener Frontend (Vite + React)

This project is a modern React frontend for the URL shortener application, built with Vite and TypeScript. It supports localization (i18n), dark/light theme toggle, and a clean UI. Features include:

- Create new short URLs
- List existing short URLs
- Delete short URLs
- Localization with react-i18next
- Dark/Light theme support
- Docker-compatible for use in docker-compose

## Getting Started

1. Install dependencies:
   ```sh
   npm install
   ```
2. Start the development server:
   ```sh
   npm run dev
   ```

## Docker

This app is ready to be used in a Docker container. See the root `docker-compose.yaml` for integration.

## Customization
- Update i18n resources in `src/locales/`.
- Adjust API endpoints in the service files as needed.

---

For more details, see the project documentation or contact the maintainer.
