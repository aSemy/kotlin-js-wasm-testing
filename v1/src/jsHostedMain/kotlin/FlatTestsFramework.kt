import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/** Declares a test suite. */
private fun suite(name: String, suiteDefinitions: () -> Unit) {
    flatTestFramework.suite(name, false, suiteFn = suiteDefinitions)
}

/** Declares a test. */
private fun testSync(name: String, testBlock: () -> Any?) {
    flatTestFramework.test(name, false, testBlock)
}

/** Declares an asynchronous test. */
private fun testAsync(name: String, testBlock: suspend () -> Unit) {
    @OptIn(DelicateCoroutinesApi::class)
    flatTestFramework.test(name, false) { GlobalScope.testFunctionPromise { testBlock() } }
}

fun runFlatTestSuiteSync() {
    suite("flat sync ($flatTestFrameworkName)") {
        testSync("should pass") {}

        testSync("should fail") {
            throw (AssertionError("this is a failure"))
        }
    }
}

fun runFlatTestSuiteAsync() {
    suite("flat async ($flatTestFrameworkName)") {
        testAsync("should pass") {
            delay(1.seconds)
        }

        testAsync("should fail") {
            delay(2.seconds)
            throw (AssertionError("this is a failure"))
        }
    }
}
