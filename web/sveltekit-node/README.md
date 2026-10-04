# web/sveltekit-node — a SvelteKit server on adapter-node

Generated with `jk new --lang node -t sveltekit` (`sv create`), then `@sveltejs/adapter-auto`
swapped for `@sveltejs/adapter-node` (`jk node exec -- npm install -D @sveltejs/adapter-node`), so
`vite build` writes a Node.js server to `build/`.

```sh
jk build                          # Node.js from the lock, npm ci, vite build
jk run                            # node build/index.js on $PORT
jk image --tarball target/sveltekit-node.tar
```

`[node] framework = "sveltekit"` is written out: current SvelteKit keeps its config in
`vite.config.ts`, and jk otherwise takes the project for plain Vite (output `dist/`).
