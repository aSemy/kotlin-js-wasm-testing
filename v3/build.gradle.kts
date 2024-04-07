import org.gradle.api.attributes.Bundling.BUNDLING_ATTRIBUTE
import org.gradle.api.attributes.Bundling.EXTERNAL
import org.gradle.api.attributes.Category.CATEGORY_ATTRIBUTE
import org.gradle.api.attributes.Category.LIBRARY
import org.gradle.api.attributes.LibraryElements.JAR
import org.gradle.api.attributes.LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE
import org.gradle.api.attributes.Usage.JAVA_RUNTIME
import org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE
import org.gradle.api.attributes.java.TargetJvmEnvironment.STANDARD_JVM
import org.gradle.api.attributes.java.TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.TEST_COMPILATION_NAME

//import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
//import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate
//import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(11)

    js {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled = true
                }
            }
            testTask {
                useKarma {
                    // useDebuggableChrome()
                    useChromeHeadless()
                }
            }
        }

//        binaries.library()
//        binaries.library(compilation = compilations.getByName(TEST_COMPILATION_NAME))
//        binaries.executable()
        binaries.executable(compilation = compilations.getByName(TEST_COMPILATION_NAME))
//        nodejs()
    }

//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//        nodejs()
//    }
//
//    @OptIn(ExperimentalKotlinGradlePluginApi::class)
//    applyHierarchyTemplate(KotlinHierarchyTemplate.default) {
//        group("common") {
//            group("jsHosted") {
//                withJs()
//                withWasm() // FIXME: KT-63417 – to be split into `withWasmJs` and `withWasmWasi`
//            }
//        }
//    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
                implementation("io.kotest:kotest-js-junit-engine-messages")
            }
        }

//        jsTest {
//            dependencies {
//            }
//        }
    }
}


val kotestJsTestClasspath: Configuration by configurations.creating {
    isVisible = false
    isCanBeConsumed = false
    isCanBeResolved = false
    isCanBeDeclared = true
}

val kotestJsTestClasspathResolver: Configuration by configurations.creating {
    isVisible = false
    isCanBeConsumed = false
    isCanBeResolved = true
    isCanBeDeclared = false

    extendsFrom(kotestJsTestClasspath)

    attributes {
        attribute(USAGE_ATTRIBUTE, objects.named(JAVA_RUNTIME))
        attribute(CATEGORY_ATTRIBUTE, objects.named(LIBRARY))
        attribute(BUNDLING_ATTRIBUTE, objects.named(EXTERNAL))
        attribute(TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(STANDARD_JVM))
        attribute(LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(JAR))
    }
}


dependencies {
    kotestJsTestClasspath("io.kotest:kotest-js-junit-engine")
}


val jsTestKotest by tasks.registering(Test::class) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    // Must have non-empty classes dir, otherwise Gradle skips the entire task.
    // The values will be ignored - we will discover the tests through other methods (TBD at time of writing!)
    testClassesDirs = files("classes/kotlin/main")

    dependsOn(kotestJsTestClasspathResolver)
    classpath += kotestJsTestClasspathResolver
    useJUnitPlatform {
        includeEngines.add("KotestJSTestEngine")
    }

    dependsOn("jsTestTestDevelopmentExecutableCompileSync")

    outputs.upToDateWhen { false }

    val testDevExecDir = layout.buildDirectory.dir("compileSync/js/test/testDevelopmentExecutable/kotlin")
    inputs.dir(testDevExecDir).withPropertyName("testDevExecDir")

    systemProperty(
        "testDevExecDir",
        testDevExecDir.get().asFile
            .relativeTo(layout.projectDirectory.asFile)
            .invariantSeparatorsPath
    )
}
