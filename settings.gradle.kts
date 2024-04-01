plugins {
    // https://github.com/Splitties/refreshVersions/releases
    id("de.fayard.refreshVersions") version "0.60.3"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    pluginManagement {
        repositories {
            gradlePluginPortal()
        }
    }
    repositories {
        google()
        mavenCentral()
    }
}

refreshVersions {
    featureFlags {
        enable(de.fayard.refreshVersions.core.FeatureFlag.LIBS)
    }
}

rootProject.name = "kotlin-js-wasm-testing"

include(
    ":v1",
    ":v2",
    ":with-kotlin-test",
)
