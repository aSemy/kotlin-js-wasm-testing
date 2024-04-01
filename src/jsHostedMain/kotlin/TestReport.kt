import kotlin.time.Duration
import kotlin.time.TimeSource

class TestReport(val prefix: String) {
    var reportTestsAsSuites: Boolean = false
    internal var nextFlowId: Int = 1
}

inline fun TestReport.asSuite(name: String, suite: () -> Unit) {
    val flowId = addSuiteStart(name)
    suite()
    addSuiteFinish(name, flowId)
}

inline fun TestReport.asTest(name: String, test: () -> Unit) {
    println("launching as test")
    val flowId = addTestStart(name)
    val timeMark = TimeSource.Monotonic.markNow()
    try {
        test()
    } catch (exception: Throwable) {
        addTestFailure(name, exception, flowId)
    } finally {
        addTestFinish(name, flowId, timeMark.elapsedNow())
    }
}

fun TestReport.addSuiteStart(name: String): Int {
    val flowId = nextFlowId++
    teamCityMessage(
        "testSuiteStarted",
        name = name,
        flowId = "${prefix}${flowId}"
    )
    return flowId
}

fun TestReport.addSuiteFinish(name: String, flowId: Int) {
    teamCityMessage(
        "testSuiteFinished",
        name = name,
        flowId = "${prefix}${flowId}"
    )
}

fun TestReport.addTestStart(name: String): Int {
    if (reportTestsAsSuites) return addSuiteStart(name)

    val flowId = nextFlowId++
    teamCityMessage(
        "testStarted",
        name = name,
        captureStandardOutput = true,
        flowId = "${prefix}${flowId}"
    )
    return flowId
}

fun TestReport.addTestFailure(name: String, exception: Throwable, flowId: Int) {
    if (reportTestsAsSuites) return

    val details = exception.stackTraceToString()
    teamCityMessage(
        "testFailed",
        name = name,
        message = exception.message,
        details = details,
        flowId = "$prefix$flowId"
    )
}

fun TestReport.testIgnored(name: String, message: String, flowId: Int) {
    teamCityMessage(
        "testIgnored",
        name = name,
        message = message,
        flowId = "$prefix$flowId"
    )
}

fun TestReport.addTestFinish(name: String, flowId: Int, duration: Duration) {
    if (reportTestsAsSuites) return addSuiteFinish(name, flowId)

    teamCityMessage(
        "testFinished",
        name = name,
        duration = duration,
        flowId = "$prefix$flowId"
    )
    println("##teamcity[testFinished name='$name' duration='2' flowId='$prefix$flowId']")
}

fun TestReport.addTestFailureAndFinish(
    name: String,
    exception: Throwable,
    flowId: Int,
    duration: Duration,
) {
    addTestFailure(name, exception, flowId)
    addTestFinish(name, flowId, duration)
}


private fun teamCityMessage(
    operation: String,
    name: String? = null,
    message: String? = null,
    duration: Duration? = null,
    flowId: String? = null,
    details: String? = null,
    captureStandardOutput: Boolean? = null,
) {
    val args = buildList {
        add(operation.tcEscape())
        if (name != null) add("name='${name.tcEscape()}'")
        if (message != null) add("message='${message.tcEscape()}'")
//        if (flowId != null) add("flowId='${flowId.tcEscape()}'")
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
