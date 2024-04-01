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
    val flowIdStr = "${prefix}${flowId}".tcEscape()
    println("##teamcity[testSuiteStarted name='${name.tcEscape()}' flowId='$flowIdStr']")
    return flowId
}

fun TestReport.addSuiteFinish(name: String, flowId: Int) {
    val flowIdStr = "${prefix}${flowId}".tcEscape()
    println("##teamcity[testSuiteFinished name='${name.tcEscape()}' flowId='$flowIdStr']")
}

fun TestReport.addTestStart(name: String): Int {
    if (reportTestsAsSuites) return addSuiteStart(name)

    val flowId = nextFlowId++
    val flowIdStr = "${prefix}${flowId}".tcEscape()
    println("##teamcity[testStarted name='${name.tcEscape()}' captureStandardOutput='true' flowId='$flowIdStr']")
    return flowId
}

fun TestReport.addTestFailure(name: String, exception: Throwable, flowId: Int) {
    if (reportTestsAsSuites) return

    val details = "(details)" // exception.stackTraceToString().replace('\n', ' ')
    println(
        "##teamcity[testFailed name='${name.tcEscape()}' message='${exception.tcEscape()}' details='${details.tcEscape()}' flowId='${"$prefix$flowId".tcEscape()}']"
    )
}

fun TestReport.testIgnored(name: String, message: String, flowId: Int) {
    val details = "(details)" // exception.stackTraceToString().replace('\n', ' ')
    println(
        "##teamcity[testIgnored name='${name.tcEscape()}' message='${message.tcEscape()}' flowId='${flowId.tcEscape()}']"
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
