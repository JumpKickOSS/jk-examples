import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Development: `jk dev` at the workspace root runs this dev server beside the app (inferred from
// app's dependency on web); it serves on 5173 and proxies /api to the Spring Boot server on 8080.
// Production: `jk build` runs the build script and packages dist/ as web-<version>.jar; the app
// serves it from classpath:/static/.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: { "/api": "http://localhost:8080" },
  },
});
