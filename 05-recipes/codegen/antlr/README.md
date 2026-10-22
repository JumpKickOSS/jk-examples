# 05-recipes/codegen/antlr — a language from a grammar

`calc` is a small expression language — variables, `+ - * / % ^`, `min`/`max`/`sqrt`, comments —
built from one ANTLR grammar:

```text
src/main/antlr4/dev/jkexamples/calc/Calc.g4   →   CalcLexer, CalcParser, CalcBaseVisitor (package dev.jkexamples.calc)
```

`[antlr]` runs the ANTLR tool in the generate stage. The grammar's directory under
`src/main/antlr4` is the generated classes' package; `visitor = true` writes the visitor
`Evaluator` extends. The tool is fetched at `[antlr] version`; the runtime the parser reads at run
time is pinned to the same number in `[dependencies]`, so `jk update` moves both together.

```sh
jk build                     # generate-antlr, then compile against the generated parser; tests
jk run -- sample.calc        # 1776, then 1598.4
echo '2 ^ 10' | jk run       # 1024
```

## Teaches

- A grammar is a source file: put it under `src/main/antlr4/<package>/` and build.
- Visitor vs listener, chosen in `[antlr]`; precise syntax errors through an error listener.

## Validates

- The `generate-antlr` step: package from the directory, the visitor, generated classes in the compile.
- A grammar edit regenerates; an unchanged grammar is a cache hit.
