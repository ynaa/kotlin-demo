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
    testImplementation(ktor.client)
    testImplementation(ktor.cio)
    testImplementation(db.h2)
}

val mainClass = "no.miles.kotlindemo.MainKt" // replace it!
val fatJar = task("fatJar", type = Jar::class) {
    val baseName = "${project.name}-fat"
    // manifest Main-Class attribute is optional.
    // (Used only to provide default main class for executable jar)
    manifest {
        attributes["Main-Class"] = mainClass // fully qualified class name of default main class
    }
    from(configurations
        .runtimeClasspath
        .get()
        .map({ if (it.isDirectory) it else zipTree(it) }))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    with(tasks["jar"] as CopySpec)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}




