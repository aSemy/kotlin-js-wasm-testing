import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async
import kotlin.js.Promise

actual suspend fun testsLauncher(block: suspend () -> Unit) {
    if (isJasmine()) {

//    error("isBrowser: ${isBrowser()}, isJasmine:${isJasmine()}")
//
//    if (!isBrowser()) {
        launchBrowser(block)
    } else {
        launchNode(block)
    }
}


private suspend fun launchNode(block: suspend () -> Unit): Unit = block()

private suspend fun launchBrowser(block: suspend () -> Unit) {

//    error("in launchBrowser before blockResult")

//    val blockResult = supervisorScope {
//        async(start = CoroutineStart.LAZY) { block() }.asPromise()
//    }

    describe("browser-test-context") {
        it("browser-test") {
            println("##teamcity[testFinished name='browser-test' duration='']\n")

            GlobalScope.async {
                block()
                println("##teamcity[testStarted name='browser-test' captureStandardOutput='true' flowId='karmaTC-102870799420173956']\n")
            }.asPromise()
        }
    }

//    kTest.suite("browser-scope-suite", ignored = false) {
//        kTest.test("browser-scope", ignored = false) {
//            error("do I get here? $blockResult")
//            blockResult
//        }
//    }
}


private fun isJasmine(): Boolean =
    js("typeof describe === 'function' && typeof it === 'function'").toString().toBoolean()


private fun isBrowser(): Boolean =
    js("typeof window === 'undefined'").toString().toBoolean()


private var currentAdapter: FrameworkAdapter? = null // will be set when kotlin-test is initialize adapter

@OptIn(ExperimentalStdlibApi::class)
@EagerInitialization // it is necessary to initialize top-level property in non-lazy manner
val handleAdapter = run {
    val jso = js("{}")
    val currentTransformer: ((FrameworkAdapter) -> FrameworkAdapter)? = globalThis.kotlinTest?.adapterTransformer
    jso.adapterTransformer = { previousAdapter: FrameworkAdapter ->
        currentAdapter = previousAdapter
        currentTransformer?.let { it(previousAdapter) }
    }

    globalThis.kotlinTest = jso
}


external val globalThis: dynamic
private val kTest get() = currentAdapter!!


//@JsModule("jasmine")
private external fun it(name: String, testFn: () -> Any?)

@Suppress("UNUSED_PARAMETER")
private fun describe(description: String, suiteFn: () -> Unit) {
    // Here we disable the default 2s timeout and use the timeout support which Kotest provides via coroutines.
    // The strange invocation is necessary to avoid using a JS arrow function which would bind `this` to a
    // wrong scope: https://stackoverflow.com/a/23492442/2529022
    js("describe(description, function () { this.timeout(0); suiteFn(); })")
}


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
private external interface FrameworkAdapter {
    /** Declares a test suite. */
    fun suite(name: String, ignored: Boolean, suiteFn: () -> Unit)

    /** Declares a test. */
    fun test(name: String, ignored: Boolean, testFn: () -> Promise<Any>)
}


// Part of the Kotlin/JS test infra.
private external interface KotlinTestNamespace {
    val adapterTransformer: ((FrameworkAdapter) -> FrameworkAdapter)?
}

// Part of the Kotlin/JS test infra.
@JsName("kotlinTest")
private external val kotlinTestNamespace: KotlinTestNamespace


//@JsModule("jasmine")
//private external val jasmine: dynamic
//
//private external interface Jasmine {
//    fun getEnv(): JasmineEnv
//}
//
//private external interface JasmineEnv {
//    fun addReporter(reporter: JasmineReporter)
//}
//
//private external interface JasmineReporter {
//    val jasmineStarted: JasmineStartedFn
//    val suiteStarted: JasmineReporterSuiteStarted
//    val specStarted: JasmineReporterSpecStarted
//    val specDone: JasmineReporterSpecDone
//    val suiteDone: JasmineReporterSuiteDone
//    val jasmineDone: JasmineReporterJasmineDone
//}
//
//private typealias JasmineStartedFn = (suiteInfo: JasmineStartedInfo, done: JasmineDoneCallback?) -> Promise<Any>?
//private typealias JasmineReporterSuiteStarted = (result: SuiteResult, done: JasmineDoneCallback?) -> Promise<Any>?
//private typealias JasmineReporterSpecStarted = (result: SpecResult, done: JasmineDoneCallback?) -> Promise<Any>?
//private typealias JasmineReporterSpecDone = (result: SpecResult, done: JasmineDoneCallback?) -> Promise<Any>?
//private typealias JasmineReporterSuiteDone = (result: SuiteResult, done: JasmineDoneCallback?) -> Promise<Any>?
//private typealias JasmineReporterJasmineDone = (runDetails: JasmineDoneInfo, done: JasmineDoneCallback?) -> Promise<Any>?
//
///** Used to specify to Jasmine that this callback is asynchronous and Jasmine should wait until it has been called before moving on. */
//private typealias JasmineDoneCallback = () -> Unit
//
//
//private external interface JasmineStartedInfo {
//    val totalSpecsDefined: Int
////    val order: Order;
//}
//
//
//private external interface SuiteResult {
//    /** The unique id of this spec. */
////    id: string;
//
//    /** The description passed to the {@link it} that created this spec. */
////    description: string;
//
//    /**
//     * The full description including all ancestors of this spec.
//     */
////    fullName: string;
//
//    /**
//     * The list of expectations that failed during execution of this spec.
//     */
////    failedExpectations: FailedExpectation[];
//
//    /**
//     * The list of deprecation warnings that occurred during execution this spec.
//     */
////    deprecationWarnings: DeprecatedExpectation[];
//
//    /**
//     * Once the spec has completed, this string represents the pass/fail status of this spec.
//     */
////    status: string;
//
//    /**
//     * The time in ms used by the spec execution, including any before/afterEach.
//     */
////    duration: number | null;
//
//    /**
//     * User-supplied properties, if any, that were set using {@link Env.setSpecProperty}
//     */
////    properties: { [key: string]: unknown } | null;
//}
//
//
//private external interface SpecResult : SuiteResult {
//    /**
//     * The list of expectations that passed during execution of this spec.
//     */
//    passedExpectations: PassedExpectation[];
//
//    /**
//     * If the spec is pending, this will be the reason.
//     */
//    pendingReason: string;
//
//    debugLogs: DebugLogEntry[] | null;
//}
//
//
//private external interface JasmineDoneInfo {
//    val overallStatus: String
//    val totalTime: Number
//    val incompleteReason: String
//    val order: Order
//    failedExpectations: ExpectationResult[];
//    deprecationWarnings: ExpectationResult[];
//}
