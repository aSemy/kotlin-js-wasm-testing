import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
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

    @OptIn(ExperimentalWasmDsl::class)
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
        commonMain {
            dependencies {
                implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
            }
        }

        commonTest {
            dependencies {
//                implementation(kotlin("test"))
            }
        }
    }
}
