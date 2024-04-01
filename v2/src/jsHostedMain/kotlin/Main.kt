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
    }
}


suspend fun main() {
    runTests()
}
