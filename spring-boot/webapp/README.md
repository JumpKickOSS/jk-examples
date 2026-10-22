# spring-boot/webapp — a JVM backend serving a single-page front end

The most common shape of application people actually ship, as a two-module workspace:

| Module | Role |
|--------|------|
| `app` | Spring Boot: `/api/hello`, the static SPA from `classpath:/static/`, and the SPA fallback. `[image]`. |
| `web` | A tiny Vite + React app that fetches `/api/hello`, with one Vitest test. A **node module**: `node = 24` and no JVM sources. |

## The classpath seam

The front end and the back end have two toolchains and one artifact. The seam between them is
a **resource jar**:

1. `web/jk.toml` declares `node = 24`. jk provisions that Node.js, installs from
   `package-lock.json` (`npm ci`) and runs the `build` script, so Vite writes `web/dist/`. Every
   step is cached: a second `jk build` installs and bundles nothing.
2. Because `app` depends on `web`, jk packages `dist/` as `web-0.1.0.jar` with `static/` at its
   root.
3. `app/jk.toml` depends on it with `web.workspace = true`, so the jar rides into the Boot jar's
   `BOOT-INF/lib/` like any other dependency. Nothing is copied between module trees.
4. Spring Boot's default static locations already include `classpath:/static/`, so `GET /` and
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

```sh
jk build                                      # Node.js, the web jar and its Vitest test, app's Boot jar, the /api/hello test
unzip -l web/target/web-0.1.0.jar | grep static/index.html
java -jar app/target/app-0.1.0.jar            # http://localhost:8080/ and /about and /api/hello
jk guard                                      # spring + monorepo packs
jk image -m app                               # optional: JRE 25 image with a trained AOT cache
```

## Two live loops with one command

```sh
jk dev                                        # at the workspace root
```

Nothing in `jk.toml` declares the front end's dev server: `app` depends on the node module `web`,
so `jk dev` infers it and runs web's `dev` script under the locked Node.js beside the app. It
starts the JVM with reload **and** Vite, prefixes each line with its member, prints the Vite URL
(5173) as the front door once both are up, and stops both on Ctrl-C. Vite serves the React app
with hot reload and proxies `/api` to the JVM on 8080 (`server.proxy` in `vite.config.ts`), so in
development the browser talks to Vite and the classpath seam is not involved at all.

## What else it shows

- `[spring-boot] version = "4.1.1"` on `app` — one exact Boot pin, and `jk-lock.toml` carries the
  BOM's versions for the whole workspace.
- Root-level test tiers: `[test] exclude-tags` plus one profile per tag.
- Guards: the `spring` pack for the framework and the `monorepo` pack for the workspace.
- `[build-info]` on `app`: the Boot jar carries `git.properties` and `META-INF/build-info.properties`
  with the commit the build came from.
- The front end is the minimum that shows the seam — no router, state or CSS library.
