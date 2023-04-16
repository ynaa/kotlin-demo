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
    implementation(ktor.serializationXml)

    implementation(logs.bundles.logs)
    implementation(db.bundles.exposedWithPostgres)

    testImplementation(test.kotest)
    testImplementation(db.h2)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}




