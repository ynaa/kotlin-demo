import org.gradle.jvm.tasks.Jar

plugins {
    id("idea")
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":bank-common"))
    implementation(ktor.bundles.ktorServerBundle)
    implementation(ktor.serializationJson)
    implementation(ktor.serializationXml)
    implementation(ktor.bundles.ktorClientBundle)
    implementation(logs.bundles.logs)

    testImplementation(test.kotest)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}




