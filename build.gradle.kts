plugins {
    id("com.diffplug.spotless") version "7.0.3" apply false
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    afterEvaluate {
        configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            isEnforceCheck = false
            encoding("UTF-8")
            format("misc") {
                target(".gitignore", "README.md")
                leadingTabsToSpaces()
                endWithNewline()
                trimTrailingWhitespace()
            }
            java {
                target("**/*.java")
                leadingTabsToSpaces()
                endWithNewline()
                trimTrailingWhitespace()
                removeUnusedImports()
                palantirJavaFormat()
            }
            kotlinGradle {
                ktfmt().googleStyle()
                leadingTabsToSpaces()
                endWithNewline()
                trimTrailingWhitespace()
            }
        }
    }
}
