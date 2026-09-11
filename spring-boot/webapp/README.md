# spring-boot/webapp — a JVM backend serving a single-page front end

The most common shape of application people actually ship, as a two-module workspace:

| Module | Role |
|--------|------|
| `app` | Spring Boot: `/api/hello`, the static SPA from `classpath:/static/`, and the SPA fallback. `[image]`, `[dev.sidecars]`. |
| `web` | A tiny Vite + React app. **Resource-only jk module** — no JVM sources; its resource root is where Vite writes the bundle. |

## The classpath seam

The front end and the back end have two build tools and one artifact. The seam between them is
a **resource jar**:

1. `web/jk.toml` declares no sources, so the module uses jk's simple layout and `web/resources/`
   is its resource root.
2. `web/vite.config.ts` sets `build.outDir = "resources/static"` (with `emptyOutDir`), so
   `npm run build` writes `index.html` and the hashed `assets/` straight into that root.
   `web/resources/` is gitignored — it is build output, not source.
3. `jk build` packages the directory as `web-0.1.0.jar` with `static/` at its root.
4. `app/jk.toml` depends on it with `web.workspace = true`, so the jar rides into the Boot jar's
   `BOOT-INF/lib/` like any other dependency. Nothing is copied between module trees.
5. Spring Boot's default static locations already include `classpath:/static/`, so `GET /` and
   `GET /assets/<hash>.js` are served with no configuration. `spa/StaticAssets` adds a one-year
   `Cache-Control` for `/assets/**` only — the file names carry a content hash, `index.html`
   does not and stays uncached.

**The fallback.** A single-page app owns routes the server has never heard of (`/about`).
`spa/SpaFallback` is a `@ControllerAdvice` on `NoResourceFoundException`: a `GET`/`HEAD` outside
`/api`, whose last path segment has no extension, from a client that accepts HTML, gets
`index.html` (`Cache-Control: no-store`) with status 200 — so a deep link and a reload both work.
It deliberately does **not** catch: anything under `/api` (a 404 problem detail, as the API
does), a missing file such as `/missing.png`, or a client that asked for JSON. The bundle's
own assets never reach it.

## Build

Node is a prerequisite jk does not manage; the bundle has to exist before `jk build` packages it.

```sh
cd web && npm ci && npm run build && cd ..   # → web/resources/static/
jk build                                      # web jar, app Boot jar, the /api/hello test
unzip -l target/web/lib/web-0.1.0.jar | grep static/index.html
java -jar target/app-0.1.0.jar                # http://localhost:8080/ and /about and /api/hello
jk guard                                      # spring + monorepo packs
jk image -m app                               # optional: JRE 25 image with a trained AOT cache
```

## Two live loops with one command

```sh
cd app && jk dev
```

`app/jk.toml` declares the Vite dev server as a sidecar:

```toml
[dev.sidecars]
web = { command = "npm run dev", cwd = "../web", ready = "http://localhost:5173", front-door = true }
```

`jk dev` starts the JVM with reload **and** `npm run dev` in `web/`, waits until 5173 answers,
prints that URL as the front door, and stops both on Ctrl-C. Vite serves the React app with hot
reload and proxies `/api` to the JVM on 8080 (`server.proxy` in `vite.config.ts`), so in
development the browser talks to Vite and the classpath seam is not involved at all. `jk dev
--no-sidecars` runs the app alone; `jk run`, `jk build` and `jk test` never read the table.

## What else it shows

- `[spring-boot] version = "latest"` — `jk-lock.toml` pins Boot for the whole workspace.
- Root-level test tiers: `[test] exclude-tags` plus one profile per tag.
- Guards: the `spring` pack for the framework and the `monorepo` pack for the workspace.
- The front end is the minimum that shows the seam — no router, state or CSS library.
