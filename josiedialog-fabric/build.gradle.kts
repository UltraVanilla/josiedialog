plugins {
  `java-library`
  `maven-publish`
  id("fabric-loom") version "1.10-SNAPSHOT"
}

group = "josie.dialog.fabric"

repositories {
  mavenCentral()
  //  maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
  //    name = "sonatype-oss-snapshots1"
  //    mavenContent { snapshotsOnly() }
  //  }
  mavenLocal()
}

dependencies {
  minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
  mappings(loom.officialMojangMappings())
  modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

  setOf("fabric-lifecycle-events-v1", "fabric-command-api-v2").forEach {
    modImplementation(fabricApi.module(it, project.property("fabric_version").toString()))
  }

  implementation("net.kyori:adventure-api:${project.property("adventure_api_version")}")
  modImplementation(
    include(
      "net.kyori:adventure-platform-fabric:${project.property("adventure_platform_fabric_version")}"
    )!!
  )

  api(include(project(":josiedialog-api"))!!)
  implementation(include(project(":josiedialog"))!!)

  // TODO: why do we have to re-specify transitive dependencies anyways?
  include("com.caoccao.javet:javet:${project.property("javet_version")}")
  include("com.caoccao.javet:javet-v8-linux-arm64:${project.property("javet_version")}")
  include("com.caoccao.javet:javet-v8-linux-x86_64:${project.property("javet_version")}")
  include("com.caoccao.javet:javet-v8-macos-arm64:${project.property("javet_version")}")
  include("com.caoccao.javet:javet-v8-macos-x86_64:${project.property("javet_version")}")
  include("com.caoccao.javet:javet-v8-windows-x86_64:${project.property("javet_version")}")

  include("io.jsonwebtoken:jjwt-api:${project.property("jjwt_version")}")
  include("io.jsonwebtoken:jjwt-impl:${project.property("jjwt_version")}")
  include("io.jsonwebtoken:jjwt-gson:${project.property("jjwt_version")}")

  api("org.jspecify:jspecify:${project.property("jspecify_version")}")
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
