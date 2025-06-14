import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://app:8080', // Docker Compose backend service name
        changeOrigin: true,
      },
    },
  },
});
