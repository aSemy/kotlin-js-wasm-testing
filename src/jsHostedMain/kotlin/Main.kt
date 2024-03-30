import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/** Declares a test suite. */
private fun suite(name: String, suiteDefinitions: () -> Unit) {
    testFramework.suite(name, false, suiteFn = suiteDefinitions)
}

/** Declares a test. */
private fun testSync(name: String, testBlock: () -> Any?) {
    testFramework.test(name, false, testBlock)
}

/** Declares an asynchronous test. */
private fun testAsync(name: String, testBlock: suspend () -> Unit) {
    @OptIn(DelicateCoroutinesApi::class)
    testFramework.test(name, false) { GlobalScope.testFunctionPromise { testBlock() } }
}

private fun runFlatTestSuiteSync() {
    suite("flat sync ($frameworkName)") {
        testSync("should pass") {}

        testSync("should fail") {
            throw (AssertionError("this is a failure"))
        }
    }
}

private fun runNestedTestSuitesSync() {
    suite("nested sync ($frameworkName)") {
        testSync("container") {
            testSync("should pass") {}
        }

        testSync("should fail") {
            throw (AssertionError("this is a failure"))
        }
    }
}

private fun runFlatTestSuiteAsync() {
    suite("flat async ($frameworkName)") {
        testAsync("should pass") {
            delay(1.seconds)
        }

        testAsync("should fail") {
            delay(2.seconds)
            throw (AssertionError("this is a failure"))
        }
    }
}

private fun runNestedTestSuitesAsync() {
    suite("nested async ($frameworkName)") {
        testAsync("container") {
            testAsync("should pass") {
                delay(1.seconds)
            }
        }

        testAsync("should fail") {
            delay(2.seconds)
            throw (AssertionError("this is a failure"))
        }
    }
}

private val frameworkName get() = "$testFramework"

private val testFramework: JsTestFramework = standaloneJsTestFramework
// private val testFramework: JsTestFramework = kotlinJsTestFramework

fun runTests() {
    // runFlatTestSuiteSync()
    // runNestedTestSuitesSync()
    runFlatTestSuiteAsync()
    // runNestedTestSuitesAsync()
}

fun main() {
    runTests()
}
