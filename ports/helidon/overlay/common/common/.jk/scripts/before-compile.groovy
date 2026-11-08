// Hand edit (import gap, see PARITY.md): Helidon's common/common runs maven-resources-plugin's
// `resources` goal over src/main/templates with filtering into target/generated-sources/templates,
// which build-helper adds as a source root. That yields io.helidon.common.Version with the project
// version and buildnumber-maven-plugin's git revision. jk import keeps the source root but not the
// filtered copy; this script writes the same file into the generated-source root.
def version = "27.0.1"
def revision = "unknown"
def shaFile = projectDir.resolve("../../.jk-helidon-sha").toFile()
if (shaFile.isFile()) revision = shaFile.text.trim().take(7)
def templates = projectDir.resolve("src/main/templates")
templates.toFile().eachFileRecurse(groovy.io.FileType.FILES) { f ->
    def rel = templates.relativize(f.toPath())
    def out = outDir.resolve(rel.toString()).toFile()
    out.parentFile.mkdirs()
    out.text = f.text.replace('${project.version}', version).replace('${buildNumber}', revision)
}
