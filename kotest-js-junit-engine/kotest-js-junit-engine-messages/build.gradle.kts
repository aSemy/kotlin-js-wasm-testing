plugins {
    base
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "io.kotest"

kotlin {
    jvmToolchain(11)

    js {
        browser {}
        nodejs()
        binaries.library()
    }

    jvm()

    sourceSets {
        commonMain {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3")
                api("com.squareup.okio:okio:3.9.0")
            }
        }
    }
}
