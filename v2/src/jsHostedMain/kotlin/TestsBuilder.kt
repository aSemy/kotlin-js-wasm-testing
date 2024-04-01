import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.time.TimeSource
import kotlin.time.measureTime

@DslMarker
annotation class TestsBuilderDsl

@TestsBuilderDsl
class TestsBuilder {

    private val reporter = TestReporter("tests-reporter")

    suspend fun context(name: String, block: suspend TestsBuilder.() -> Unit) {
        reporter.suiteStart(name)
        val duration = measureTime {
            block()
        }
        reporter.suiteFinish(name, duration)
    }

    suspend fun test(name: String, block: suspend () -> Unit): Unit = supervisorScope {
        reporter.testStart(name)

        val markStart = TimeSource.Monotonic.markNow()

        launch(
            CoroutineExceptionHandler { _, exception ->
                reporter.testFailure(name, exception)
                reporter.testFinish(name, markStart.elapsedNow())
            }
        ) {
            block()
            reporter.testFinish(name, markStart.elapsedNow())
        }
    }
}


suspend fun tests(block: suspend TestsBuilder.() -> Unit) {
    TestsBuilder().block()
}


//expect fun testsBuilder(name: String): TestsBuilder
