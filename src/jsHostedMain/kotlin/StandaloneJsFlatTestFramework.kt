internal expect val standaloneJsFlatTestFramework: JsTestFramework

abstract class StandaloneJsFlatTestFramework : JsTestFramework {
    override val report = TestReport("s")

    class Test(val name: String, val testFn: () -> Any?)

    val tests = mutableListOf<Test>()

    override fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
        if (ignored) {
            // TODO
        } else {
            tests.add(Test(name, testFn))
        }
    }
}
