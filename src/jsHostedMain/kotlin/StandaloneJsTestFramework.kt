internal expect val standaloneJsTestFramework: JsTestFramework

abstract class StandaloneJsTestFramework : JsTestFramework {

    class Test(val name: String, val testFn: () -> Any?)

    protected var nextFlowId: Int = 1
    protected val tests = mutableListOf<Test>()

    override fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
        if (ignored) {
            // TODO
        } else {
            tests.add(Test(name, testFn))
        }
    }

    protected fun reportSuiteStart(name: String, flowId: Int) {
        println("##teamcity[testSuiteStarted name='$name' flowId='$flowId']")
    }

    protected fun reportSuiteFinish(name: String, flowId: Int) {
        println("##teamcity[testSuiteFinished name='$name' flowId='$flowId']")
    }

    protected fun reportTestStart(name: String, flowId: Int) {
        println("##teamcity[testStarted name='$name' captureStandardOutput='true' flowId='$flowId']")
    }

    protected fun reportTestFinish(name: String, flowId: Int) {
        println("##teamcity[testFinished name='$name' duration='2' flowId='$flowId']")
    }

    protected fun reportTestFailure(name: String, message: String, flowId: Int) {
        val details = "(details)" // exception.stackTraceToString().replace('\n', ' ')
        println(
            "##teamcity[testFailed name='$name' message='$message'" +
                " details='$details' flowId='$flowId']"
        )
        reportTestFinish(name, flowId)
    }
}
