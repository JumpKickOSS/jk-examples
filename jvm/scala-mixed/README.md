# jvm/scala-mixed — one module of Java and Scala, imported by Metals through jk's BSP

A single JumpKick module whose `src/main/java` and `src/main/scala` compile in one Zinc session and
reference each other both ways: `Greeting.java` calls the Scala `Greeter`, `Main.scala` reads its
default from `Greeting`. Scala `3.9.0` is the manifest's exact pin (`scala = "3.9.0"`); JUnit 6 runs
a Java test and a Scala test in the same unit tier.

It demonstrates:

- a mixed module as one BSP build target: `languageIds` name both languages, the target carries the
  `scala` data Metals imports it by (compiler version, binary version, the compiler jars a build
  fetched, the JDK), and `buildTarget/scalacOptions` / `buildTarget/javacOptions` answer with the
  arguments jk's Zinc session passes, the compile classpath and the class directory
- a compile through `buildTarget/compile`, and a Scala type error arriving as `build/publishDiagnostics`
- the test-tier table (`[test] exclude-tags` plus one profile per tag) and the house guard baseline

```sh
jk build                 # compiles both halves, runs the unit tier, packages target/scala-mixed-0.1.0.jar
jk test                  # Tests: 4 passed (4 total) — GreetingTest (Java) and GreeterTest (Scala)
jk run . Ada Grace       # Hello, Ada! / Hello, Grace!
jk guard
jk bsp install           # .bsp/jk.json — what Metals and IntelliJ's BSP discover
./bsp-session.py         # the wire shapes below, from a real `jk bsp serve`
./bsp-session.py --diagnostic   # the same, with a type error dropped in and taken out again
```

## What Metals sees

`jk bsp install` (or `jk ide`) writes the connection file:

```json
{
  "name": "jk",
  "version": "0.13.7",
  "bspVersion": "2.1.0",
  "languages": ["java", "scala"],
  "argv": ["jk", "bsp", "serve"]
}
```

Metals reads `.bsp/jk.json` when the folder opens and offers **Import build**; choosing it starts
`jk bsp serve` on stdio and runs the exchange `bsp-session.py` reproduces. Recorded from that server
(long URI arrays folded to their first entries):

`build/initialize` — the server names `scala` among the languages it compiles, tests and runs:

```json
{"displayName": "jk", "version": "0.13.7", "bspVersion": "2.1.0",
 "capabilities": {"compileProvider": {"languageIds": ["java", "scala"]},
                  "testProvider": {"languageIds": ["java", "scala"]},
                  "runProvider": {"languageIds": ["java", "scala"]}, "canReload": true}}
```

`workspace/buildTargets` — one target for the module, a Scala target to Metals:

```json
{"id": {"uri": "file:///…/jvm/scala-mixed/#scala-mixed"},
 "displayName": "scala-mixed", "baseDirectory": "file:///…/jvm/scala-mixed/",
 "tags": ["library"], "languageIds": ["java", "scala"], "dependencies": [],
 "capabilities": {"canCompile": true, "canTest": true, "canRun": true},
 "dataKind": "scala",
 "data": {"scalaOrganization": "org.scala-lang", "scalaVersion": "3.9.0", "scalaBinaryVersion": "3",
          "platform": 1,
          "jars": ["file:///…/store/tools/scala/3.9.0/lib/compiler-interface-1.12.0.jar",
                   "file:///…/store/tools/scala/3.9.0/lib/interface-1.0.29-M4.jar", "… 15 more"],
          "jvmBuildTarget": {"javaHome": "file:///home/…/.jdks/temurin-25/", "javaVersion": "25.0.4.1"}}}
```

`buildTarget/scalacOptions` and `buildTarget/javacOptions` — the flags jk's Zinc session passes for
`java = 25`, the same classpath (the Scala library jars first, then the resolved libraries) and the
one class directory both halves compile into:

```json
{"target": {"uri": "file:///…/jvm/scala-mixed/#scala-mixed"},
 "options": ["-java-output-version", "25"],
 "classpath": ["file:///…/store/tools/scala/3.9.0/lib/scala-library-3.9.0.jar",
               "file:///…/store/tools/scala/3.9.0/lib/scala3-library_3-3.9.0.jar", "… 12 more"],
 "classDirectory": "file:///…/jvm/scala-mixed/target/classes/main/"}
```

```json
{"target": {"uri": "file:///…/jvm/scala-mixed/#scala-mixed"},
 "options": ["--release", "25"],
 "classpath": ["… the same 14 entries …"],
 "classDirectory": "file:///…/jvm/scala-mixed/target/classes/main/"}
```

`buildTarget/compile` answers `{"statusCode": 1}` (OK) on the committed sources. With
`--diagnostic`, `bsp-session.py` writes `Broken.scala` (`val n: Int = "not a number"`) before the
compile and deletes it afterwards; the server publishes the error before the result:

```json
{"method": "build/publishDiagnostics",
 "params": {"textDocument": {"uri": "file:///…/jvm/scala-mixed/"},
            "buildTarget": {"uri": "file:///…/jvm/scala-mixed/#scala-mixed"},
            "diagnostics": [{"range": {"start": {"line": 0, "character": 0}, "end": {"line": 0, "character": 0}},
                             "severity": 1,
                             "message": "/…/src/main/scala/com/example/mixed/Broken.scala:4: error: Found:    (\"not a number\" : String)\nRequired: Int"}]}}
```

```json
{"id": 5, "result": {"statusCode": 2, "message": "/…/Broken.scala:4: error: Found: …"}}
```

With that import in place Metals treats the folder as one Scala target: `Greeting.java` and
`Greeter.scala` are sources of the same target, so go-to-definition on `Greeter` in the Java file
opens the Scala class, and a save recompiles through `buildTarget/compile` with the diagnostics
above rendered in the editor. The transcript is what the server sends; it was recorded with
`bsp-session.py`, not from a Metals session.

## Gaps

- A scalac diagnostic lands at the project root with `range` 0:0 — the file and line ride inside the
  message, and the message keeps scalac's colour escapes. javac diagnostics arrive with file, line
  and column ([IDE and BSP](https://github.com/JumpKickOSS/jk/blob/main/docs/user/ide.md#bsp)).
- The target is tagged `library` although `[application] main` names a main class; Metals uses the
  tag to offer a run lens, so the run goes through `jk run` or `buildTarget/run`.
