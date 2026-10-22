import type { NextConfig } from "next";

// The Spring Boot app's origin; `next build` bakes it into the standalone server's /api rewrite.
const apiOrigin = process.env.API_ORIGIN ?? "http://localhost:8080";

// A standalone server: `next build` writes .next/standalone/server.js with only the files it needs,
// which `jk run` starts and `jk image` ships on a distroless Node.js base without node_modules.
const nextConfig: NextConfig = {
  output: "standalone",
  // /api belongs to the JVM: the page fetches same-origin and Next proxies it to the app.
  async rewrites() {
    return [{ source: "/api/:path*", destination: `${apiOrigin}/api/:path*` }];
  },
};

export default nextConfig;
