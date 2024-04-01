import kotlin.js.Promise

private val untransformedJsTestFramework = object : StandaloneJsFlatTestFramework() {
    override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
        if (ignored) return // TODO

        val suiteFlowId = report.addSuiteStart(name)

        suiteFn()

        if (tests.isEmpty()) {
            // Finish a suite with no tests synchronously.
            report.addSuiteFinish(name, suiteFlowId)
        } else {
            // Run tests and finish the suite asynchronously.
            fun Test.runWithRemainingTests(nextIndex: Int) {
                startedPromise().then {
                    val nextTest = tests.getOrNull(nextIndex)
                    if (nextTest != null) {
                        nextTest.runWithRemainingTests(nextIndex + 1)
                    } else {
                        report.addSuiteFinish(name, suiteFlowId)
                    }
                }
            }

            tests.first().runWithRemainingTests(1)
        }
    }

    private fun Test.startedPromise() = Promise { resolve, _ ->
        val flowId = report.addTestStart(name)

        try {
            val promise = testFn() as? Promise<*>
            if (promise != null) {
                promise.then {
                    report.addTestFinish(name, flowId)
                    resolve(Unit)
                }.catch { exception ->
                    report.addTestFailureAndFinish(name, exception, flowId)
                    resolve(Unit)
                }
            } else {
                report.addTestFinish(name, flowId)
                resolve(Unit)
            }
        } catch (exception: Throwable) {
            report.addTestFailureAndFinish(name, exception, flowId)
            resolve(Unit)
        }
    }
}

internal actual val standaloneJsFlatTestFramework = object : FrameworkAdapter {
    override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) =
        untransformedJsTestFramework.suite(name, ignored, suiteFn)

    override fun test(name: String, ignored: Boolean, testFn: () -> Any?) =
        untransformedJsTestFramework.test(name, ignored, testFn)
}.toTestFramework("JS/standalone", untransformedJsTestFramework.report)
