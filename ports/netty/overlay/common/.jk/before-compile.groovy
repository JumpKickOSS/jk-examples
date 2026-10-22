// BEFORE_COMPILE: Netty common collection templates → Java sources.
// Mirrors Maven groovy-maven-plugin / Mill common.generatedSources / scripts/generate-common.sh.
// Host bindings: projectDir, outDir, classesDir, properties, ant.

def script = projectDir.resolve("src/main/script/codegen.groovy").toFile()
if (!script.isFile()) {
    // Not a full Netty common tree (or codegen removed) — no-op.
    return
}

def templates = projectDir.resolve("src/main/templates").toString()
def testTemplates = projectDir.resolve("src/test/templates").toString()
def outMain = projectDir.resolve("src/main/java")
def outTest = projectDir.resolve("src/test/java")
outMain.toFile().mkdirs()
outTest.toFile().mkdirs()

properties["collection.template.dir"] = templates
properties["collection.template.test.dir"] = testTemplates
properties["collection.src.dir"] = outMain.toString()
properties["collection.testsrc.dir"] = outTest.toString()

// Upstream codegen.groovy uses properties[...] and ant.copy / regexpmapper.
evaluate(script)

outDir.resolve("netty-codegen.stamp").toFile().text = "ok\n"
