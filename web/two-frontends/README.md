# web/two-frontends — one API, two front ends, one `jk dev`

| Module | Role |
|--------|------|
| `app` | Spring Boot API (`/api/hello`); serves `admin` from `classpath:/static/`. |
| `admin` | Vite + React admin SPA, a node module `app` depends on (resource jar under `static/`). |
| `storefront` | Next.js storefront, a standalone node server of its own. |

Generated with `jk new -t java/spring-boot/webapp --frontend vite-react` (its `web` renamed
`admin`) and `jk new --lang node -t next storefront` inside the workspace, which joins it.

```sh
jk build                          # every module; app's tests
jk dev                            # at the root: app on 8080, admin's Vite on 5173, storefront on 3000
jk dev -m app                     # the API with admin (inferred from the dependency) only
```

No `[dev.sidecars]` is written: `jk dev` infers each node module's dev server, prefixes every
line with its member, and prints one ready line listing both front doors.
