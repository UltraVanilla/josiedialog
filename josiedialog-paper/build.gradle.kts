plugins {
  `java-library`
  `maven-publish`
  id("de.eldoria.plugin-yml.paper") version "0.7.1"
  id("com.gradleup.shadow") version "8.3.5"
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

group = "josie.dialog.paper"

repositories {
  mavenCentral()
  maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
  maven("https://ultravanilla.github.io/maven/release")
}

dependencies {
  paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
  implementation(project(":josiedialog-api"))
  implementation(project(":josiedialog"))

  api("org.jspecify:jspecify:${project.property("jspecify_version")}")
}

tasks.withType<JavaCompile>().configureEach { options.release.set(21) }

tasks.shadowJar {
  fun reloc(pkg: String) = relocate(pkg, "${project.group}.dependency.$pkg")

  reloc("com.caoccao.javet")
  reloc("io.jsonwebtoken")
}

tasks.build { dependsOn(tasks.shadowJar) }

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

paper {
  name = "josiedialog"

  authors = listOf("lordpipe", "JosieToolkit Contributors", "UltraVanilla Contributors")
  website = "https://ultravanilla.world/"

  main = "${project.group}.JosieDialogPaper"
  apiVersion = "1.21"
  load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
}
