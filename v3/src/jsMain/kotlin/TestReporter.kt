import io.kotest.js_messages.*
import kotlinx.serialization.encodeToString
import kotlin.js.Date
import kotlin.time.Duration

class TestReporter(
    private val flowId: String,
    private val parents: List<String>,
) {

    fun suiteStart(name: String) {
        val event = TestEvent(
            type = TestEventType.BeforeSuite,
            descriptor = TestDescriptor(
                id = (parents + name).joinToString("/"),
                name = name,
                className = parents.lastOrNull(),
            )
        )
        println("kotest{${kxsBinary.encodeToString(event)}}\n")

        teamCityMessage(
            "testSuiteStarted",
            name = name,
        )
    }

    fun suiteFinish(name: String, duration: Duration) {
        val event = TestEvent(
            type = TestEventType.AfterSuite,
            descriptor = TestDescriptor(
                id = (parents + name).joinToString("/"),
                name = name,
                className = parents.lastOrNull(),
            )
        )
        println("kotest{${kxsBinary.encodeToString(event)}}\n")

        teamCityMessage(
            "testSuiteFinished",
            name = name,
            duration = duration,
        )
    }

    fun testStart(name: String) {
        val event = TestEvent(
            type = TestEventType.BeforeTest,
            descriptor = TestDescriptor(
                id = (parents + name).joinToString("/"),
                name = name,
                className = parents.lastOrNull(),
            )
        )
        println("kotest{${kxsBinary.encodeToString(event)}}\n")

        teamCityMessage(
            "testStarted",
            name = name,
            captureStandardOutput = true,
        )
    }

    fun testFailure(name: String, exception: Throwable) {
        val details = exception.stackTraceToString()

        val event = TestEvent(
            type = TestEventType.AfterTest,
            descriptor = TestDescriptor(
                id = (parents + name).joinToString("/"),
                name = name,
                className = parents.lastOrNull(),
            ),
            result = TestResult(
                result = TestResult.ResultType.Failure,
                endTime = Date.now().toLong(),
            )
        )
        println("kotest{${kxsBinary.encodeToString(event)}}\n")


        teamCityMessage(
            "testFailed",
            name = name,
            message = exception.message,
            details = details,
        )
    }

    fun testIgnored(name: String, message: String) {
        val event = TestEvent(
            type = TestEventType.AfterTest,
            descriptor = TestDescriptor(
                id = (parents + name).joinToString("/"),
                name = name,
                className = parents.lastOrNull(),
            ),
            result = TestResult(
                result = TestResult.ResultType.Skipped,
                endTime = Date.now().toLong(),
            )
        )
        println("kotest{${kxsBinary.encodeToString(event)}}\n")
        teamCityMessage(
            "testIgnored",
            name = name,
            message = message,
        )
    }

    fun testFinish(name: String, duration: Duration) {
        val event = TestEvent(
            type = TestEventType.AfterTest,
            descriptor = TestDescriptor(
                id = (parents + name).joinToString("/"),
                name = name,
                className = parents.lastOrNull(),
            ),
            result = TestResult(
                result = TestResult.ResultType.Success
            )
        )
        println("kotest{${kxsBinary.encodeToString(event)}}\n")
        teamCityMessage(
            "testFinished",
            name = name,
            duration = duration,
        )
    }

    private fun teamCityMessage(
        operation: String,
        name: String? = null,
        message: String? = null,
        duration: Duration? = null,
//        flowId: String? = null,
        details: String? = null,
        captureStandardOutput: Boolean? = null,
//        timestamp: String? = currentDateTime(),
    ) {
        val dummyData = "." // tests must end in a . otherwise KotlinKarma fails

        val tcArgs = mapOf(
            "name" to name?.plus(dummyData),
            "parent" to parents.lastOrNull()?.plus(dummyData),
            "message" to message,
//            "timestamp" to timestamp?.removeSuffix("Z"),
            "flowId" to flowId,
            "duration" to duration,
            "details" to details,
            "captureStandardOutput" to captureStandardOutput,
        )
            .filterValues { it != null }
            .entries
            .joinToString(separator = " ") { (k, v) ->
                "$k='${v.tcEscape()}'"
            }
//        println("\t TC ARGS ~ tcArgs ~  \t\n")
//        println("##teamcity[$operation ${tcArgs}]\n")
    }

    private fun Any?.tcEscape(): String {
        if (this == null) return ""

        return toString()
            .replace(Regex("\\x1B.*?m"), "") // ANSI colour codes
            .replace("|", "||")
            .replace("\n", "|n")
            .replace("\r", "|r")
            .replace("[", "|[")
            .replace("]", "|]")
            .replace("\u0085", "|x") // next line
            .replace("\u2028", "|l") // line separator
            .replace("\u2029", "|p") // paragraph separator
            .replace("'", "|'")
    }

}
