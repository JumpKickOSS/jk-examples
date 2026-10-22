subprojects {
    group = "dev.jkexamples"
    version = "0.1.0"

    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
