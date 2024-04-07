import io.kotest.js_messages.TestDescriptor
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.TimeSource
import kotlin.time.measureTime

@DslMarker
annotation class TestsBuilderDsl

@TestsBuilderDsl
class TestsBuilder(
    private val scopeName: String,
    private val parent: TestDescriptor?,
) {
//    private val parentsJoined = parents.joinToString(".")

    private val reporter = TestReporter("tests-reporter", parent = parent)

    suspend fun context(
        name: String,
        block: suspend TestsBuilder.() -> Unit,
    ): Unit = supervisorScope {
        val desc = reporter.suiteStart(name)
        val duration = measureTime {
            TestsBuilder(name, desc).block()
        }
        reporter.suiteFinish(name, duration)
    }

    suspend fun xcontext(
        name: String,
        block: suspend TestsBuilder.() -> Unit,
    ): Unit = supervisorScope {
        reporter.testIgnored(name, "skipping context $block")
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
//            val parentsArr = parents.joinToString(",") { "\"${it.replace(" ", "-")}\"" }
//            println("""--END_KOTLIN_TEST-- {"suite": [$parentsArr], "description": "$name"} ${"\n\n"}""")
        }
    }

    suspend fun xtest(
        name: String,
        block: suspend TestScope.() -> Unit,
    ): Unit = supervisorScope {
        reporter.testIgnored(name, "skipping $block")
    }
}


@TestsBuilderDsl
object TestScope


suspend fun tests(block: suspend TestsBuilder.() -> Unit): Unit = supervisorScope {
    TestsBuilder("tests-entry", parent = null).apply {
//        context("KotestJs") {
            block()
//        }
    }
    println("~~~KOTEST-FINISHED~~~")
}


//expect fun testsBuilder(name: String): TestsBuilder
