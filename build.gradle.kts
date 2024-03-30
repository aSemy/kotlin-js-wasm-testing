import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate

plugins {
    alias(libs.plugins.org.jetbrains.kotlin.multiplatform)
}

kotlin {
    jvmToolchain(11)

    js {
        browser {
            testTask {
                useKarma {
                    // useDebuggableChrome()
                    useChromeHeadless()
                }
            }
        }
        nodejs()
        binaries.executable()
    }

    wasmJs {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyHierarchyTemplate(KotlinHierarchyTemplate.default) {
        group("common") {
            group("jsHosted") {
                withJs()
                withWasm() // FIXME: KT-63417 – to be split into `withWasmJs` and `withWasmWasi`
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
            }
        }
    }
}

// FIXME: WORKAROUND https://youtrack.jetbrains.com/issue/KT-65864
//     Use a Node.js version current enough to support Kotlin/Wasm

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        // Initialize once in a multi-project build.
        // Otherwise, Gradle would complain "Configuration already finalized for previous property values".
        if (!System.getProperty("nodeJsCanaryConfigured").toBoolean()) {
            nodeVersion = "22.0.0-nightly2024010568c8472ed9"
            println("Using Node.js $nodeVersion to support Kotlin/Wasm")
            nodeDownloadBaseUrl = "https://nodejs.org/download/nightly"
            System.setProperty("nodeJsCanaryConfigured", "true")
        }
    }
}

rootProject.tasks.withType<org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask>().configureEach {
    args.add("--ignore-engines") // Prevent Yarn from complaining about newer Node.js versions.
}
