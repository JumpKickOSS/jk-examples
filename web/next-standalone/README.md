# web/next-standalone — a Next.js server as a node module

Generated with `jk new --lang node -t next` (`create-next-app`), then `output: "standalone"` in
`next.config.ts`, so `next build` writes `.next/standalone/server.js` with only what it needs.

```sh
jk build                          # Node.js from the lock, npm ci, next build
jk run                            # node .next/standalone/server.js on $PORT (default 3000)
jk image --tarball target/next-standalone.tar   # a distroless Node.js server image
```

`jk-guards.toml` measures the front end's sources (not lockfiles or build output).

Known gap: `jk image` fails here with `[jk-image-builder].mainClass missing`; the command does
not yet hand a node module to the node image plan.
