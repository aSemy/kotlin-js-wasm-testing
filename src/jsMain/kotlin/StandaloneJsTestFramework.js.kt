import kotlin.js.Promise

internal actual val standaloneJsTestFramework = object : FrameworkAdapter {
    override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit)
        = untransformedJsTestFramework.suite(name, ignored, suiteFn)

    override fun test(name: String, ignored: Boolean, testFn: () -> Any?)
        = untransformedJsTestFramework.test(name, ignored, testFn)
}.toTestFramework("JS/standalone")

private val untransformedJsTestFramework = object : StandaloneJsTestFramework() {
    override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
        if (ignored) return// TODO

        suiteFn()

        val suiteFlowId = nextFlowId++
        reportSuiteStart(name, suiteFlowId)

        if (tests.isEmpty()) {
            // Finish a suite with no tests synchronously.
            reportSuiteFinish(name, suiteFlowId)
        } else {
            // Run tests and finish the suite asynchronously.
            fun Test.runWithRemainingTests(nextIndex: Int) {
                startedPromise().then {
                    val nextTest = tests.getOrNull(nextIndex)
                    if (nextTest != null) {
                        nextTest.runWithRemainingTests(nextIndex + 1)
                    } else {
                        reportSuiteFinish(name, suiteFlowId)
                    }
                }
            }

            tests.first().runWithRemainingTests(1)
        }
    }

    private fun Test.startedPromise() = Promise { resolve, _ ->
        val flowId = nextFlowId++

        reportTestStart(name, flowId)

        try {
            val promise = testFn() as? Promise<*>
            if (promise != null) {
                promise.then {
                    reportTestFinish(name, flowId)
                    resolve(Unit)
                }.catch { exception ->
                    reportTestFailure(name, "$exception", flowId)
                    resolve(Unit)
                }
            } else {
                reportTestFinish(name, flowId)
                resolve(Unit)
            }
        } catch (exception: Throwable) {
            reportTestFailure(name, "$exception", flowId)
            resolve(Unit)
        }
    }
}
