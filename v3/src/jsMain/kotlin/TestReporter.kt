import io.kotest.js_messages.*
import kotlin.js.Date
import kotlin.time.Duration

class TestReporter(
    private val className: String,
    private val parent: TestDescriptor?,
) {

    fun id(name: String): String {
        return generateSequence(parent) { it.parent }.joinToString("/") { it.name } + "/$name"
    }

    fun suiteStart(name: String): TestDescriptor {
        val descriptor = TestDescriptor(
            id = "$className//${id(name)}",
            name = name,
            className = className,
            isComposite = true,
            parent = parent,
        )
        val event = TestEvent(
            type = TestEventType.BeforeSuite,
            descriptor = descriptor,
        )
        printEvent(event)
        return descriptor
    }

    fun suiteFinish(name: String, duration: Duration) {
        val event = TestEvent(
            type = TestEventType.AfterSuite,
            descriptor = TestDescriptor(
                id = "$className//${id(name)}",
                name = name,
                className = className,
                isComposite = true,
                parent = parent,
            ),
            result = TestResult(
                result = TestResult.ResultType.Failure,
                startTime = Date.now().toLong() - duration.inWholeMilliseconds,
                endTime = Date.now().toLong(),
            ),
        )
        printEvent(event)
    }

    fun testStart(name: String) {
        val event = TestEvent(
            type = TestEventType.BeforeTest,
            descriptor = TestDescriptor(
                id = "$className//${id(name)}",
                name = name,
                className = className,
                parent = parent,
            )
        )
        printEvent(event)
    }

    fun testFailure(name: String, exception: Throwable) {
        val details = exception.stackTraceToString()

        stderr(name, details)
        val event = TestEvent(
            type = TestEventType.AfterTest,
            descriptor = TestDescriptor(
                id = "$className//${id(name)}",
                name = name,
                className = className,
                parent = parent,
            ),
            result = TestResult(
                result = TestResult.ResultType.Failure,
                endTime = Date.now().toLong(),
            ),
        )
        printEvent(event)
    }

    fun testIgnored(name: String, message: String) {
        stdout(name, message)
        val event = TestEvent(
            type = TestEventType.AfterTest,
            descriptor = TestDescriptor(
                id = "$className//${id(name)}",
                name = name,
//                displayName = nam,
                className = className,
                parent = parent,
            ),
            result = TestResult(
                result = TestResult.ResultType.Skipped,
                endTime = Date.now().toLong(),
            )
        )
        printEvent(event)
    }

    fun testFinish(name: String, duration: Duration) {
        val event = TestEvent(
            type = TestEventType.AfterTest,
            descriptor = TestDescriptor(
                id = "$className//${id(name)}",
                name = name,
                className = className,
                parent = parent,
            ),
            result = TestResult(
                result = TestResult.ResultType.Success,
                startTime = Date.now().toLong() - duration.inWholeMilliseconds,
                endTime = Date.now().toLong(),
            )
        )
        printEvent(event)
    }

    fun stdout(name: String, message: String) = output(name, message, TestOutput.Destination.StdOut)
    fun stderr(name: String, message: String) = output(name, message, TestOutput.Destination.StdErr)

    fun output(name: String, message: String, destination: TestOutput.Destination) {
        val event = TestEvent(
            type = TestEventType.OnOutput,
            descriptor = TestDescriptor(
                id = "$className//${id(name)}",
                name = name,
                className = className,
                parent = parent,
            ),
            output = TestOutput(
                destination = destination,
                message = message,
            )
        )
        printEvent(event)
    }

    private fun printEvent(event: TestEvent) {
        val encoded = kxsBinary.encodeToString(TestEvent.serializer(), event)
        // sometimes on JS it doesn't log the last line, so add an extra \n, just in case
        println("~~~KOTEST{$encoded}~~~\n")
    }
}
