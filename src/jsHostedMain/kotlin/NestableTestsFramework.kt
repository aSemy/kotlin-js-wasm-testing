import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * Framework supporting nested tests in a coroutine hierarchy.
 *
 * It delegates to a base framework, where it creates a single suite and a single top-level test.
 * If possible (with the [StandaloneJsFlatTestFramework]), it makes the base framework report that test as a suite.
 * It then runs all its tests inside that base framework's top-level test.
 */
private class NestableJsTestFramework(baseFramework: JsTestFramework) : JsTestFramework by baseFramework {
    override val report = TestReport("n")

    init {
        baseFramework.report?.apply {
            reportTestsAsSuites = true
        }
    }

    suspend fun nestableTestContainer(name: String, ignored: Boolean, testBlock: suspend () -> Unit) {
        if (ignored) {
            // TODO
        } else {
            report.asSuite(name) {
                coroutineScope {
                    testBlock()
                }
            }
        }
    }

    suspend fun nestableTest(name: String, ignored: Boolean, testBlock: suspend () -> Unit) {
        if (ignored) {
            // TODO
        } else {
            report.asTest(name) {
                coroutineScope {
                    testBlock()
                }
            }
        }
    }
}

private val nestableTestFramework: NestableJsTestFramework by lazy { NestableJsTestFramework(flatTestFramework) }

/** Declares a test suite. */
private fun suite(name: String, testSuite: suspend () -> Unit) {
    nestableTestFramework.suite(name, false) { // top-level suite, delegated to the base framework
        nestableTestFramework.test(name, false) { // top-level test, delegated to the base framework
            @OptIn(DelicateCoroutinesApi::class)
            GlobalScope.testFunctionPromise {
                testSuite()
            }
        }
    }
}

/** Declares an asynchronous test container. */
private suspend fun container(name: String, testBlock: suspend () -> Unit) {
    nestableTestFramework.nestableTestContainer(name, false, testBlock)
}

/** Declares an asynchronous test. */
private suspend fun test(name: String, testBlock: suspend () -> Unit) {
    nestableTestFramework.nestableTest(name, false, testBlock)
}

fun runNestableTests() {
    suite("nestable async (via $flatTestFrameworkName)") {
        container("container") {
            test("should pass") {
                delay(1.seconds)
            }
        }

        test("should fail") {
            delay(2.seconds)
            throw (AssertionError("this is a failure"))
        }
    }
}
