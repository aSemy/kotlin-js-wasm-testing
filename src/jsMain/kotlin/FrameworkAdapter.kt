
/**
 * JS test framework adapter interface defined by the Kotlin/JS test infra.
 *
 * This interface allows framework function invocations to be conditionally transformed as required for proper
 * reporting of [failing JS tests on Node.js](https://youtrack.jetbrains.com/issue/KT-64533).
 *
 * Inside the Kotlin/JS test infra, the interface is actually known as `KotlinTestRunner`:
 *     https://github.com/JetBrains/kotlin/blob/v1.9.23/libraries/tools/kotlin-test-js-runner/src/KotlinTestRunner.ts
 * Proper test reporting depends on using kotlinTest.adapterTransformer, which is defined here for Node.js:
 *     https://github.com/JetBrains/kotlin/blob/v1.9.23/libraries/tools/kotlin-test-js-runner/nodejs.ts
 */
internal external interface FrameworkAdapter {
    /** Declares a test suite. */
    fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit)

    /** Declares a test. */
    fun test(name: String, ignored: Boolean, testFn: () -> Any?)
}

internal fun FrameworkAdapter.toTestFramework(untransformedName: String) = object : JsTestFramework {
    val frameworkAdapter: FrameworkAdapter by lazy {
        transformedAdapter()
        // To disable the transformation, uncomment the following line.
        // this@toTestFramework
    }

    override fun toString(): String = "$untransformedName/transformed"

    override fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit) {
        frameworkAdapter.suite(name, ignored, suiteFn)
    }

    override fun test(name: String, ignored: Boolean, testFn: () -> Any?) {
        frameworkAdapter.test(name, ignored, testFn)
    }
}

// Conditional transformation required by the Kotlin/JS test infra.
private fun FrameworkAdapter.transformedAdapter(): FrameworkAdapter {
    return if (jsTypeOf(kotlinTestNamespace) != "undefined") {
        kotlinTestNamespace.adapterTransformer?.invoke(this) ?: this
    } else {
        this
    }
}

// Part of the Kotlin/JS test infra.
private external interface KotlinTestNamespace {
    val adapterTransformer: ((FrameworkAdapter) -> FrameworkAdapter)?
}

// Part of the Kotlin/JS test infra.
@JsName("kotlinTest")
private external val kotlinTestNamespace: KotlinTestNamespace
