# graphql/dgs-codegen — GraphQL code generation as a `[generate]` recipe

[DGS codegen](https://netflix.github.io/dgs/generating-code-from-schema/) ships a command line
in its core jar, so it needs no preset: one `[generate.dgs]` entry runs `CodeGenCli` over the
schema under `src/main/resources/schema`, the types land under `com.acme.graphql.types` and the
typed query builder under `com.acme.graphql.client`, and both join the compile.

```toml
[generate.dgs]
tool    = "com.netflix.graphql.dgs.codegen:graphql-dgs-codegen-core:8.6.0"
main    = "com.netflix.graphql.dgs.codegen.CodeGenCli"
inputs  = ["src/main/resources/schema/**/*.graphqls"]
args    = ["--output-dir", "${out}", "--package-name", "com.acme.graphql", "--generate-client", "${inputs}"]
discard = ["generated-examples"]   # the CLI's example data fetchers, which need the DGS runtime
```

The tool's runtime closure (Kotlin, Spring context, graphql-java, …) is fetched with it and hashed
into the step's key; `jk-lock.toml` pins the one runtime jar the generated client reads. A schema
edit re-runs the step, an unchanged schema is a cache hit (`jk explain`).

```sh
jk build
jk run       # prints a generated type and the query document the client builds
jk test
jk guard
```
