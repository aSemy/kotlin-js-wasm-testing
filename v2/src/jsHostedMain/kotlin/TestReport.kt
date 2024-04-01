import kotlin.time.Duration

class TestReporter(private val flowId: String) {


    fun suiteStart(name: String) {
        teamCityMessage(
            "testSuiteStarted",
            name = name,
        )
    }

    fun suiteFinish(name: String, duration: Duration) {
        teamCityMessage(
            "testSuiteFinished",
            name = name,
            duration = duration,
        )
    }

    fun testStart(name: String) {
        teamCityMessage(
            "testStarted",
            name = name,
            captureStandardOutput = true,
        )
    }

    fun testFailure(name: String, exception: Throwable) {
        val details = exception.stackTraceToString()
        teamCityMessage(
            "testFailed",
            name = name,
            message = exception.message,
            details = details,
        )
    }

    fun testIgnored(name: String, message: String) {
        teamCityMessage(
            "testIgnored",
            name = name,
            message = message,
        )
    }

    fun testFinish(name: String, duration: Duration) {
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
        timestamp: String? = currentDateTime(),
    ) {
        val args = buildList {
            add(operation.tcEscape())
            if (name != null) add("name='${name.tcEscape()}'")
            if (message != null) add("message='${message.tcEscape()}'")
            if (timestamp != null) add("timestamp='${timestamp.removeSuffix("Z").tcEscape()}'")
//        if (flowId != null) add("flowId='${flowId.tcEscape()}'")
            add("flowId='${flowId.tcEscape()}'")
//            add("flowId='kotest-flow'")

        if (duration != null) add("duration='${duration.inWholeMilliseconds}'")
            if (details != null) add("details='${details.tcEscape()}'")
            if (captureStandardOutput != null) add("captureStandardOutput='${captureStandardOutput}'")
        }.joinToString(separator = " ")

        println("\t TC ARGS ~ $args ~  \t\n")
        println("\n##teamcity[${args}]\n")
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


//inline fun TestReport.asSuite(name: String, suite: () -> Unit) {
//    val flowId = addSuiteStart(name)
//    val duration = measureTime {
//        suite()
//    }
//    addSuiteFinish(name, flowId, duration)
//}

//inline fun TestReport.asTest(name: String, test: () -> Unit) {
//    println("launching as test")
//    val flowId = addTestStart(name)
//    val timeMark = TimeSource.Monotonic.markNow()
//    try {
//        test()
//    } catch (exception: Throwable) {
//        addTestFailure(name, exception, flowId)
//    } finally {
//        addTestFinish(name, flowId, timeMark.elapsedNow())
//    }
//}



//fun TestReport.addTestFailureAndFinish(
//    name: String,
//    exception: Throwable,
//    flowId: Int,
//    duration: Duration,
//) {
//    addTestFailure(name, exception, flowId)
//    addTestFinish(name, flowId, duration)
//}
//
//fun formatMessage(
//    tcMessage: String,
//    vararg args: Any
//): String {
//    val formattedArguments = mutableListOf<Any>()
//    args.map { param ->
//        formattedArguments.add(param.tcEscape())
//    }
//    formattedArguments.add(0, tcMessage)
//    return format(*formattedArguments.toTypedArray())
//}
