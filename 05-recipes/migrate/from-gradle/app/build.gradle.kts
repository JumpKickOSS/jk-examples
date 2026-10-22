plugins {
    application
}

dependencies {
    implementation(project(":core"))
    implementation(libs.picocli)
}

application {
    mainClass = "dev.jkexamples.wordfreq.app.WordFreq"
}
