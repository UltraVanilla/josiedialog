plugins {
  `java-library`
  `maven-publish`
  id("fabric-loom") version "1.10-SNAPSHOT"
}

group = "josie.dialog.fabric.example"

repositories { mavenCentral() }

dependencies {
  minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
  mappings(loom.officialMojangMappings())
  modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

  setOf("fabric-lifecycle-events-v1", "fabric-command-api-v1").forEach {
    modImplementation(fabricApi.module(it, project.property("fabric_version").toString()))
  }

  implementation(project(":josiedialog-api"))

  api("org.jspecify:jspecify:1.0.0")
}

tasks.withType<JavaCompile>().configureEach { options.release.set(21) }

java {
  withSourcesJar()
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

tasks.named<Jar>("jar") {
  inputs.property("archivesName", project.name)

  from("LICENSE") { rename { "${it}_${project.name}" } }
}

tasks.named<ProcessResources>("processResources") {
  notCompatibleWithConfigurationCache("uses Task.project at execution time")
  inputs.property("version", project.version)
  filesMatching("fabric.mod.json") { expand(mapOf("version" to project.version)) }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])

      pom {
        name = "${project.name} ${project.version}"
        description = project.description
        url = "https://github.com/UltraVanilla/${project.name}"

        licenses {
          license {
            name = "LGPL-3.0-or-later"
            url = "https://www.gnu.org/licenses/lgpl-3.0.html"
            distribution = "repo"
          }
        }

        developers {
          developer {
            name = "lordpipe"
            organization = "UltraVanilla"
            organizationUrl = "https://ultravanilla.world/"
          }
          developer { name = "JosieToolkit Contributors" }
        }

        scm {
          url = "https://github.com/UltraVanilla/${project.name}"
          connection = "scm:https://UltraVanilla@github.com/UltraVanilla/${project.name}.git"
          developerConnection = "scm:git://github.com/UltraVanilla/${project.name}.git"
        }

        issueManagement {
          system = "GitHub"
          url = "https://github.com/UltraVanilla/${project.name}/issues"
        }
      }
    }
  }
}
