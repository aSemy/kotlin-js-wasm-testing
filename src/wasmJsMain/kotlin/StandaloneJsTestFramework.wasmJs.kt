import kotlin.js.Promise

internal actual val standaloneJsTestFramework: JsTestFramework = object : StandaloneJsTestFramework() {
    override fun toString(): String = "standalone/Wasm/JS"

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
                    null
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
                    resolve(null)
                    null
                }.catch { exception ->
                    reportTestFailure(name, "$exception", flowId)
                    resolve(null)
                    null
                }
            } else {
                reportTestFinish(name, flowId)
                resolve(null)
            }
        } catch (exception: Throwable) {
            reportTestFailure(name, "$exception", flowId)
            resolve(null)
        }
    }
}
