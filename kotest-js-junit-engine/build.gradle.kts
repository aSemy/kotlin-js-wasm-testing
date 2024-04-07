plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
}

group = "io.kotest"

dependencies {
    implementation("org.junit.platform:junit-platform-engine:1.10.2")
    implementation("com.microsoft.playwright:playwright:1.42.0")

    implementation(platform("io.ktor:ktor-bom:2.3.9"))
    implementation("io.ktor:ktor-server-core")
//        implementation("io.ktor:ktor-server-content-negotiation")
//        implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-cio")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("io.ktor:ktor-server-call-logging")


    implementation("ch.qos.logback:logback-classic:latest.release")
    implementation("org.apache.logging.log4j:log4j-core:latest.release")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:latest.release")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")

    api(project(":kotest-js-junit-engine-messages"))


    implementation("org.redundent:kotlin-xml-builder:1.9.1")
    implementation("org.opentest4j:opentest4j:1.3.0")

//        implementation("ch.qos.logback:logback-classic:$logback_version")
//        testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
//        testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
