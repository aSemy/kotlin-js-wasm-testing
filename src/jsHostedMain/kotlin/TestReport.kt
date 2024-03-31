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
    println("##teamcity[testSuiteStarted name='$name' flowId='$prefix$flowId']")
    return flowId
}

fun TestReport.addSuiteFinish(name: String, flowId: Int) {
    println("##teamcity[testSuiteFinished name='$name' flowId='$prefix$flowId']")
}

fun TestReport.addTestStart(name: String): Int {
    if (reportTestsAsSuites) return addSuiteStart(name)

    val flowId = nextFlowId++
    println("##teamcity[testStarted name='$name' captureStandardOutput='true' flowId='$prefix$flowId']")
    return flowId
}

fun TestReport.addTestFailure(name: String, exception: Throwable, flowId: Int) {
    if (reportTestsAsSuites) return

    val details = "(details)" // exception.stackTraceToString().replace('\n', ' ')
    println(
        "##teamcity[testFailed name='$name' message='$exception'" +
            " details='$details' flowId='$prefix$flowId']"
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
