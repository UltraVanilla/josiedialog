plugins {
  `java-library`
  `maven-publish`
}

group = "josie.dialog"

repositories {
  mavenCentral()
  maven("https://jitpack.io")
}

dependencies {
  api(project(":josiedialog-api"))

  api("org.jspecify:jspecify:1.0.0")

  // TODO: make a maven repository
  implementation(
    "com.github.UltraVanilla:blockgamekeyvalue:v${project.property("blockgamekeyvalue_version")}"
  )
  implementation("com.google.code.gson:gson:${project.property("gson_version")}")
  implementation("com.caoccao.javet:javet:${project.property("javet_version")}")
  implementation("com.caoccao.javet:javet-v8-linux-arm64:${project.property("javet_version")}")
  implementation("com.caoccao.javet:javet-v8-linux-x86_64:${project.property("javet_version")}")
  implementation("com.caoccao.javet:javet-v8-macos-arm64:${project.property("javet_version")}")
  implementation("com.caoccao.javet:javet-v8-macos-x86_64:${project.property("javet_version")}")
  implementation("com.caoccao.javet:javet-v8-windows-x86_64:${project.property("javet_version")}")
  implementation("com.google.guava:guava:${project.property("guava_version")}")
}

tasks.withType<JavaCompile>().configureEach { options.release.set(21) }

java {
  withSourcesJar()
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
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
