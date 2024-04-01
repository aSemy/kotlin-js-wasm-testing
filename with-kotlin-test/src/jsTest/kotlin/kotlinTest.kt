import kotlin.test.FrameworkAdapter
import kotlin.test.Test

// https://youtrack.jetbrains.com/issue/KT-65360/KJS-Gradle-Cannot-find-module-kotlin-when-using-kotlin-test
//@JsNonModule
//@JsModule("kotlin-test")
////@JsModule("kotlin-test-js-runner")
//external val kTest: dynamic


var currentAdapter: FrameworkAdapter? = null // will be set when kotlin-test is initialize adapter

@OptIn(ExperimentalStdlibApi::class)
@EagerInitialization // it is necessary to initialize top-level property in non-lazy manner
val handleAdapter = run {
    val jso = js("{}")
    val currentTransformer: ((FrameworkAdapter) -> FrameworkAdapter)? = globalThis.kotlinTest?.adapterTransformer
    jso.adapterTransformer = { previousAdapter: FrameworkAdapter ->
        currentAdapter = previousAdapter
        currentTransformer?.let { it(previousAdapter) }
    }

    globalThis.kotlinTest = jso
}


external val globalThis: dynamic
val kTest get() = currentAdapter!!


class TestJs {


    @Test
    fun kotlinTestTests() {

        suite("nestable-async-via-kotlin-test") {
            suite("container") {
                test("should-pass") {
                    //delay(1.seconds)
                }
            }

            test("should-fail") {
//                delay(2.seconds)
                throw AssertionError("this-is-a-failure")
            }
        }
    }
}

/** Test context - for nesting tests */
fun suite(name: String, ignored: Boolean = false, contextBlock: () -> Unit) {
    kTest.suite(name, ignored) {
        contextBlock()
    }
}

/** Execute a test */
fun test(name: String, ignored: Boolean = false, testBlock: () -> Unit) {
    kTest.test(name, ignored) {
        testBlock()
    }
}



//class TestJs {
//
//    @Test
//    fun passwordValidationTests() {
//
//
//
////        // create an outer context
////        context("Given a password validator") {
////            val usernameValidator = UsernameValidator()
////
////            // nest some inner contexts + tests
////            context("When username is very long") {
////                val username = "very long username ".repeat(100)
////                test("Then expect validation passes") {
////                    assertTrue { usernameValidator.isValid(username) }
////                }
////            }
////            context("When username is not blank") {
////                val username = "foo bar"
////                test("Then expect validation passes") {
////                    assertTrue { usernameValidator.isValid(username) }
////                }
////            }
////
////            // tests + contexts can be dynamically added
////            mapOf(
////                "blank" to "   ",
////                "null" to null,
////            ).forEach { (failureReason, username) ->
////                context("When username is $failureReason") {
////                    test("Then expect validation fails") {
////                        assertTrue { usernameValidator.isValid(username) }
////                    }
////                }
////            }
////        }
//    }
//}
