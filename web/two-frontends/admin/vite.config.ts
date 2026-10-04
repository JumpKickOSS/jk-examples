import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// In development /api is proxied to the JVM on :8080. `jk build` packages dist/ as the admin jar;
// the app serves it from classpath:static.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: { '/api': 'http://localhost:8080' },
  },
})
