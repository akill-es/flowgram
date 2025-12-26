plugins {
    kotlin("jvm")
}

group = "io.github.akilles"
version = "unspecified"

repositories {
    mavenCentral()
    maven("https://mvn.mchv.eu/repository/mchv/")
}

dependencies {
    implementation(platform("it.tdlight:tdlight-java-bom:3.4.4+td.1.8.52"))
    implementation("it.tdlight:tdlight-java")
    implementation(group = "it.tdlight", name = "tdlight-natives", classifier = "linux_amd64_clang_ssl3")
    implementation(group = "it.tdlight", name = "tdlight-natives", classifier = "linux_arm64_clang_ssl3")
    implementation(group = "it.tdlight", name = "tdlight-natives", classifier = "windows_amd64")
    implementation(group = "it.tdlight", name = "tdlight-natives", classifier = "macos_arm64")
    implementation(group = "it.tdlight", name = "tdlight-natives", classifier = "macos_amd64")


    implementation("ch.qos.logback:logback-classic:1.5.23")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
