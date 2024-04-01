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
    val flowId = addTestStart(name)
    try {
        test()
    } catch (exception: Throwable) {
        addTestFailure(name, exception, flowId)
    } finally {
        addTestFinish(name, flowId)
    }
}

fun TestReport.addSuiteStart(name: String): Int {
    val flowId = nextFlowId++
    println(
        teamCityMessage(
            "testSuiteStarted",
            name = name,
            flowId = "${prefix}${flowId}"
        )
    )
    return flowId
}

fun TestReport.addSuiteFinish(name: String, flowId: Int) {
    println(
        teamCityMessage(
            "testSuiteFinished",
            name = name,
            flowId = "${prefix}${flowId}"
        )
    )
}

fun TestReport.addTestStart(name: String): Int {
    if (reportTestsAsSuites) return addSuiteStart(name)

    val flowId = nextFlowId++
    println(
        teamCityMessage(
            "testStarted",
            name = name,
            captureStandardOutput = true,
            flowId = "${prefix}${flowId}"
        )
    )
    return flowId
}

fun TestReport.addTestFailure(name: String, exception: Throwable, flowId: Int) {
    if (reportTestsAsSuites) return

    val details = "(details)" // exception.stackTraceToString().replace('\n', ' ')
    println(
        teamCityMessage(
            "testFailed",
            name = name,
            message = exception.message,
            details = details,
            flowId = "$prefix$flowId"
        )
    )
}

fun TestReport.testIgnored(name: String, message: String, flowId: Int) {
    println(
        teamCityMessage(
            "testIgnored",
            name = name,
            message = message,
            flowId = "$prefix$flowId"
        )
    )
}

fun TestReport.addTestFinish(name: String, flowId: Int) {
    if (reportTestsAsSuites) return addSuiteFinish(name, flowId)

    println("##teamcity[testFinished name='$name' duration='2' flowId='$prefix$flowId']")
}

fun TestReport.addTestFailureAndFinish(name: String, exception: Throwable, flowId: Int) {
    addTestFailure(name, exception, flowId)
    addTestFinish(name, flowId)
}


private fun teamCityMessage(
    id: String,
    name: String? = null,
    message: String? = null,
    duration: String? = null,
    flowId: String? = null,
    details: String? = null,
    captureStandardOutput: Boolean? = null,
): String {
    return buildString {
        append("##teamcity[")
        append(id.tcEscape())
        if (name != null) append("name='${name.tcEscape()}'")
        if (message != null) append("message='${message.tcEscape()}'")
//        if (flowId != null) append("flowId='${flowId.tcEscape()}'")
        if (duration != null) append("duration='${duration.tcEscape()}'")
        if (details != null) append("details='${details.tcEscape()}'")
        if (captureStandardOutput != null) append("captureStandardOutput='${captureStandardOutput}'")
        append("]")
    }
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
