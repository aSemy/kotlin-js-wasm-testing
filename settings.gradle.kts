pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

plugins {
    // https://github.com/Splitties/refreshVersions/releases
    id("de.fayard.refreshVersions") version "0.60.3"
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
    ":v3",
    ":with-kotlin-test",
    ":test-output-logging",
)

includeBuild("kotest-js-junit-engine")
