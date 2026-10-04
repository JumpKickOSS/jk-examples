# web/angular — an Angular single-page app as a node module

Generated with `jk new --lang node -t angular` (`@angular/cli new`). jk infers the framework from
`angular.json`: it builds with `ng build` into `dist/angular/browser/` and runs `ng test`
(Vitest on jsdom, no browser) as the module's test step.

```sh
jk build                          # Node.js from the lock, npm ci, ng build, ng test
jk dev                            # ng serve
```

A static front end packages nothing until a JVM module depends on it (then `dist/` rides in a
resource jar under `static/`) or `[image] kind = "static"` asks for an nginx image.
