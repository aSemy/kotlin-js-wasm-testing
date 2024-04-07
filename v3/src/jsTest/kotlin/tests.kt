import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.promise
import kotlin.js.Promise
import kotlin.time.Duration.Companion.seconds

//@OptIn(ExperimentalJsExport::class)
//@JsExport
//fun mainPromise(): Promise<Boolean> =
//    @OptIn(DelicateCoroutinesApi::class)
//    GlobalScope.promise {
//        main()
//        true
//    }

suspend fun main(): Unit = tests {

    context("nestable async v2") {
        context("container") {
            test("should pass") {
                delay(1.seconds)
            }
        }

        test("should fail") {
            delay(2.seconds)
            throw AssertionError("this is a failure")
        }

        xtest("should be ignored") {
            delay(5.seconds)
            throw IllegalStateException("should not be thrown")
        }
    }

//    xcontext("ignored context") {
//        test("active test, but should be ignored") {
//            delay(3.seconds)
//        }
//    }
}
