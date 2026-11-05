# 01-start/kotlin-cli — project-declared Kotlin compiler plugins

kotlinx-serialization's generated serializers only exist when its compiler plugin actually
loaded into `kotlinc`, so compiling a reference to `Message.serializer()` **is** the validation.

```toml
kotlin = "…"          # the exact Kotlin release; jk.toml has it

[[kotlin-plugins]]
coordinate = "org.jetbrains.kotlin:kotlin-serialization-compiler-plugin-embeddable"
```

The plugin declares no version: it rides the pinned Kotlin compiler version;
`kotlinx-serialization-json` is an exact pin beside it.

```sh
jk build
jk run
jk guard
```
