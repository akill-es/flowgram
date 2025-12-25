plugins {
    id("buildsrc.convention.kotlin-jvm")
    application
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.23")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
    mainClass = "io.github.akilles.worker.AppKt"
}
