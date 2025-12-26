plugins {
    id("buildsrc.convention.kotlin-jvm")
    application
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.23")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")

    implementation("org.apache.kafka:kafka-clients:8.1.1-ccs")
    implementation(project(":models-api"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")

    implementation(platform("org.testcontainers:testcontainers-bom:2.0.2"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:testcontainers-kafka")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
    mainClass = "io.github.akilles.flowgram.worker.AppKt"
}


tasks.withType<Test> {
    useJUnitPlatform()
}
