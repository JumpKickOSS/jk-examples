import type { NextConfig } from "next";

// A standalone server: `next build` writes .next/standalone/server.js with only the files it needs,
// which `jk run` starts and `jk image` ships on a distroless Node.js base without node_modules.
const nextConfig: NextConfig = {
  output: "standalone",
};

export default nextConfig;
