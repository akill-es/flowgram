plugins {
    id("buildsrc.convention.kotlin-jvm")
}

group = "io.github.akilles"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
    api("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
