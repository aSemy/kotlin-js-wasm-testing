
internal val flatTestFrameworkName get() = "$flatTestFramework"

// internal val flatTestFramework: JsTestFramework = standaloneJsFlatTestFramework
internal val flatTestFramework: JsTestFramework = kotlinJsTestFramework

fun runTests() {
    // runFlatTestSuiteSync()
    // runFlatTestSuiteAsync()
    runNestableTests()
}

fun main() {
    runTests()
}
