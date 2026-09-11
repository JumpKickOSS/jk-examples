# kotlin/serialization-cli — project-declared Kotlin compiler plugins

kotlinx-serialization's generated serializers only exist when its compiler plugin actually
loaded into `kotlinc`, so compiling a reference to `Message.serializer()` **is** the validation.

```toml
kotlin = "latest"

[[kotlin-plugins]]
coordinate = "org.jetbrains.kotlin:kotlin-serialization-compiler-plugin-embeddable"
```

The plugin declares no version: it rides the resolved Kotlin compiler version, which
`jk-lock.toml` pins together with `kotlinx-serialization-json` (`latest`).

```sh
jk build
jk run
jk guard
```
