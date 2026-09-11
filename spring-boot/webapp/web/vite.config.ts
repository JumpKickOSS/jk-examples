import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Development: `npm run dev` (or the [dev.sidecars] entry in ../app/jk.toml) serves the app on
// 5173 and proxies /api to the Spring Boot server on 8080.
// Production: the bundle is written into this module's jk resource root, so `jk build` packages
// it as web-<version>.jar and the app serves it from classpath:/static/.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: { "/api": "http://localhost:8080" },
  },
  build: { outDir: "resources/static", emptyOutDir: true },
});
