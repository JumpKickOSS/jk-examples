# web/vite-react — a Spring Boot app serving a Vite + React SPA from its image

Generated with `jk new -t java/spring-boot/webapp --frontend vite-react`: the template's Boot app
plus a front end from Vite's own generator (`npm create vite@latest -- --template react-ts`).

| Module | Role |
|--------|------|
| `app` | Spring Boot: `/api/hello`, the SPA from `classpath:/static/`, the SPA fallback, `[image]`. |
| `web` | A **node module** (`node = 24`): jk provisions Node.js, runs `npm ci` and the `build` script, and packages `dist/` as `web-0.1.0.jar` under `static/` because `app` depends on it. |

```sh
jk build                          # Node.js, the web jar, the Boot jar, app's tests
jk dev -m app                     # the JVM and Vite (inferred from the dependency), /api proxied
jk image -m app --tarball target/vite-react-image.tar
```

Known gap: `jk image` fails in the AOT-cache training run on this host ("the training run
recorded nothing"); the JVM image path, not the front end.
