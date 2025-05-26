pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven("https://papermc.io/repo/repository/maven-public/")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

rootProject.name = "josiedialog"
include("josiedialog-api")
include("josiedialog")
include("josiedialog-paper")
include("josiedialog-fabric")
include("josiedialog-example-fabric")
