# 02-services/grails-app — a Grails 8 guestbook

A server-rendered Grails app: a GORM domain class, a controller, a GSP page, and Spock unit tests,
on Grails 8 (Apache, Spring Boot 4) and Groovy 5. It starts from `jk new -t grails/hello`. There
is no Gradle and no Grails CLI: `[grails] version` pins the BOM, and every `org.apache.grails`
dependency is versionless under it.

```sh
jk build                     # Groovy compile, Spock tests, a runnable Boot-style jar
jk run                       # http://localhost:8080 — sign the guestbook
jk dev                       # the same, restarting on change
```

## Layout

| Path | What |
|---|---|
| `grails-app/domain/…/Entry.groovy` | The GORM entity: name and message constraints, `dateCreated` filled by GORM. |
| `grails-app/controllers/…/EntryController.groovy` | `index` renders the page; `save` validates, then redirects with a flash message or re-renders with errors. |
| `grails-app/controllers/…/UrlMappings.groovy` | `/` is the guestbook, `POST /sign` signs it. |
| `grails-app/views/entry/index.gsp` | The page: `g:form`, `g:hasErrors`, `g:each`, `g:formatDate`. |
| `grails-app/init/…/BootStrap.groovy` | One seed entry. |
| `src/test/groovy/…` | `DomainUnitTest` on the constraints; `ControllerUnitTest` + `DataTest` on the actions. |

## A note on the test dependencies

`ControllerUnitTest` needs `grails-web-common`'s test fixtures (`GrailsWebMockUtil`). Grails
publishes that dependency only in its Gradle module metadata, not in its POM. jk reads the
metadata and locks `grails-web-common-8.0.0-test-fixtures.jar` on its own; a Maven build of the
same app would have to declare it.

## Teaches

- A Grails app in one `jk.toml`: `[grails] version`, versionless `org.apache.grails` dependencies, the
  `grails-app/` layout jk compiles as-is.
- GORM validation, GSP forms and Grails' Spock testing traits.

## Validates

- The grails plugin: `grails-app` source roots, Groovy joint compile with parameter names, the
  Boot-style jar, `jk run` from classes.
- Spock and Grails' testing support under `jk test`, including the test-fixtures jar Gradle
  module metadata asks for.
