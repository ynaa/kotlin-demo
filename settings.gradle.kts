
rootProject.name = "kotlin-demo"

include(
    "bank-common",
    "bank-db",
    "bank-front"
)


pluginManagement {

    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
    }
    val kotlinVersion = "1.8.20"
    plugins {
        kotlin("jvm") version kotlinVersion
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        fileTree("versions").filter { it.isFile }.forEach {
            create(it.name.split(".").first()) {
                from(files(it))
            }
        }
    }
}
