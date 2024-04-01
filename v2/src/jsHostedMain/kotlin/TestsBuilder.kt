import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.TimeSource
import kotlin.time.measureTime

@DslMarker
annotation class TestsBuilderDsl

@TestsBuilderDsl
class TestsBuilder(
    private val scopeName: String,
    parent: String?
) {
    private val reporter = TestReporter("tests-reporter", parent = parent)

    suspend fun context(
        name: String,
        block: suspend TestsBuilder.() -> Unit,
    ): Unit = supervisorScope {
        reporter.suiteStart(name)
        val duration = measureTime {
            TestsBuilder(name, scopeName).block()
        }
        reporter.suiteFinish(name, duration)
    }

    suspend fun xcontext(
        name: String,
        @Suppress("UNUSED_PARAMETER")
        block: suspend TestsBuilder.() -> Unit,
    ): Unit = supervisorScope {
        reporter.testIgnored(name, "context")
    }

    suspend fun test(
        name: String,
        block: suspend TestScope.() -> Unit,
    ): Unit = supervisorScope {
        reporter.testStart(name)

        val markStart = TimeSource.Monotonic.markNow()

        try {
            TestScope.block()
        } catch (ex: Throwable) {
            reporter.testFailure(name, ex)
            if (ex is CancellationException) throw ex
        } finally {
            reporter.testFinish(name, markStart.elapsedNow())
        }
    }

    suspend fun xtest(
        name: String,
        @Suppress("UNUSED_PARAMETER")
        block: suspend TestScope.() -> Unit,
    ): Unit = supervisorScope {
        reporter.testIgnored(name, "xtest")
    }
}


@TestsBuilderDsl
object TestScope


suspend fun tests(block: suspend TestsBuilder.() -> Unit): Unit = supervisorScope {
    TestsBuilder("", null).block()
}


//expect fun testsBuilder(name: String): TestsBuilder
