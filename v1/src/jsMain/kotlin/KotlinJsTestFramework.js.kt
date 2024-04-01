import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise

internal actual val kotlinJsTestFramework: JsTestFramework = object : FrameworkAdapter {
    override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
        if (ignored) {
            xdescribe(name, suiteFn)
        } else {
            describe(name, suiteFn)
        }
    }

    override fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
        if (ignored) {
            xit(name, testFn)
        } else {
            it(name, testFn)
        }
    }
}.toTestFramework("JS/Mocha")

internal actual fun CoroutineScope.testFunctionPromise(testFunction: suspend () -> Unit): Any? =
    promise { testFunction() }

// Jasmine/Mocha/Jest test API

@Suppress("UNUSED_PARAMETER")
private fun describe(description: String, suiteFn: () -> Unit) {
    // Here we disable the default 2s timeout and use the timeout support which Kotest provides via coroutines.
    // The strange invocation is necessary to avoid using a JS arrow function which would bind `this` to a
    // wrong scope: https://stackoverflow.com/a/23492442/2529022
    js("describe(description, function () { this.timeout(0); suiteFn(); })")
}

private external fun xdescribe(name: String, testFn: () -> Unit)
private external fun it(name: String, testFn: () -> Any?)
private external fun xit(name: String, testFn: () -> Any?)
