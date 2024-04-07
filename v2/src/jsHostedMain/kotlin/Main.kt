import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


suspend fun runTests(): Unit = tests {
    context("nestable async v2") {
        context("container") {
            test("should pass") {
                delay(1.seconds)
            }
        }

        test("should fail") {
            delay(2.seconds)
            throw AssertionError("this is a failure")
        }

        xtest("should be ignored") {
            delay(5.seconds)
            throw IllegalStateException("should not be thrown")
        }
    }

    xcontext("ignored context") {
        test("active test, but should be ignored") {
            delay(3.seconds)
        }
    }
}


suspend fun main() {
    runTests()
}
/*





 */
