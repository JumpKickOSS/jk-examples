# Spring Boot 4.1 petshop (JK-1170)

Multi-module workspace: `domain` → `service` → `web` (`[spring-boot] version = "4.1.0"`).

```bash
jk lock && jk build && jk test
jk run -C web   # or from root if run picks app module
```
