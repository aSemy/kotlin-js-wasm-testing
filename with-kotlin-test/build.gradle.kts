import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

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
                implementation(kotlin("test"))
            }
        }
    }
}


tasks.withType<Test>().configureEach {
    this.logging.captureStandardOutput(LogLevel.LIFECYCLE)
    this.testLogging {
        this.showStandardStreams = true
    }
}


/*
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteStarted name=' TestJs.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testStarted name='kotlinTestTests' captureStandardOutput='true' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testFinished name='kotlinTestTests' duration='1' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteFinished name=' TestJs.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' duration='%s' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteStarted name='nestable-async-via-kotlin-test.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testStarted name='should-fail' captureStandardOutput='true' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testFailed name='should-fail' message='FAILED' details='AssertionError: this-is-a-failure|n    at TestJs$kotlinTestTests$lambda$lambda_0 (/Users/dev/projects/external/kotlin-js-wasm-testing/with-kotlin-test/src/jsTest/kotlin/kotlinTest.kt:46:23 <- kotlin-js-wasm-testing-with-kotlin-test-test.1338310944.js:59:11)|n    at Context.<anonymous> (/Users/dev/projects/external/kotlin-js-wasm-testing/with-kotlin-test/src/jsTest/kotlin/kotlinTest.kt:103:9 <- kotlin-js-wasm-testing-with-kotlin-test-test.1338310944.js:103:7)|n' captureStandardOutput='true' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testFinished name='should-fail' duration='' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteFinished name='nestable-async-via-kotlin-test.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' duration='%s' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteStarted name='nestable-async-via-kotlin-test container.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testStarted name='should-pass' captureStandardOutput='true' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testFinished name='should-pass' duration='' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteFinished name='nestable-async-via-kotlin-test container.Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)' duration='%s' flowId='karmaTC146421387594619892']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[blockClosed name='JavaScript Unit Tests' flowId='%s']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[blockOpened name='JavaScript Unit Tests' flowId='%s']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[message text='01 04 2024 13:45:44.556:INFO |[karma-server|]: Karma v6.4.2 server started at http://localhost:9876/' type='INFO']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[message text='01 04 2024 13:45:44.557:INFO |[launcher|]: Launching browsers ChromeHeadless with concurrency unlimited' type='INFO']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[message text='01 04 2024 13:45:44.559:INFO |[launcher|]: Starting browser ChromeHeadless' type='INFO']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[message text='01 04 2024 13:45:44.823:INFO |[Chrome Headless 123.0.6312.87 (Mac OS 10.15.7)|]: Connected on socket qXRYVd0p8IccC5giAAAB with id 94619892' type='INFO']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteStarted name='' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteStarted name='TestJs' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testStarted name='kotlinTestTests' captureStandardOutput='true' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testFinished name='kotlinTestTests' duration='0' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteFinished name='TestJs' duration='' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteFinished name='' duration='2' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteStarted name='nestable-async-via-kotlin-test' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testStarted name='should-fail' captureStandardOutput='true' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testFailed name='should-fail' message='this-is-a-failure' details='AssertionError: this-is-a-failure|n    at TestJs$kotlinTestTests$lambda$lambda_0 (/Users/dev/projects/external/kotlin-js-wasm-testing/with-kotlin-test/src/jsTest/kotlin/kotlinTest.kt:46:23)|n    at Context.<anonymous> (/Users/dev/projects/external/kotlin-js-wasm-testing/with-kotlin-test/src/jsTest/kotlin/kotlinTest.kt:103:9)|n    at processImmediate (node:internal/timers:478:21)' captureStandardOutput='true' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testFinished name='should-fail' duration='0' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteStarted name='container' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testStarted name='should-pass' captureStandardOutput='true' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testFinished name='should-pass' duration='0' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteFinished name='container' duration='' flowId='63191']
[org.jetbrains.kotlin.gradle.tasks.testing] [KOTLIN] TCSM: ##teamcity[testSuiteFinished name='nestable-async-via-kotlin-test' duration='4' flowId='63191']
 */
