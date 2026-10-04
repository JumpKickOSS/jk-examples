# web/sveltekit-node — a SvelteKit server on adapter-node

Generated with `jk new --lang node -t sveltekit` (`sv create`), then `@sveltejs/adapter-auto`
swapped for `@sveltejs/adapter-node` (`jk node exec -- npm install -D @sveltejs/adapter-node`), so
`vite build` writes a Node.js server to `build/`.

```sh
jk build                          # Node.js from the lock, npm ci, vite build
jk run                            # node build/index.js on $PORT
jk image --tarball target/sveltekit-node.tar
```

jk recognises SvelteKit by its `@sveltejs/kit` dependency; with adapter-node the output is `build/`
and the server starts with `node build/index.js`.
