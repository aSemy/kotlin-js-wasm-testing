package io.kotest.runner.junit.platform

import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import io.kotest.js_messages.TestEvent
import io.kotest.js_messages.TestEventType.*
import io.kotest.js_messages.kxsBinary
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.html.*
import kotlinx.serialization.decodeFromString
import org.junit.platform.engine.*
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.io.path.Path

class KotestJSTestEngine : TestEngine {
    private val id = "KotestJSTestEngine"

    private val ijLogger = IJTestEventLogger()

    override fun getId(): String = id

    override fun discover(
        request: EngineDiscoveryRequest,
        uniqueId: UniqueId,
    ): TestDescriptor {
//        error("xxx")
        println("KotestJSTestEngine.discover($request, $uniqueId)")

        val engineDescriptor = EngineDescriptor(uniqueId, "KotestJSTestEngine")

        val testDevExecDir = Path(System.getProperty("testDevExecDir"))
        println("testDevExecDir: $testDevExecDir")

        engineDescriptor.addChild(
            JSTestDescriptor(
                uniqueId = engineDescriptor.uniqueId.append("test-segment", "js"),
                displayName = "JSTestDescriptor display name",
                jsDir = testDevExecDir,
            )
        )

        println("engineDescriptor.children: ${engineDescriptor.children}")

        return engineDescriptor
    }

    override fun execute(
        request: ExecutionRequest
    ): Unit = runBlocking(Dispatchers.IO) {
        println("KotestJSTestEngine.execute($request, ${request.rootTestDescriptor})")

        val root = request.rootTestDescriptor

        println("children: ${root.children}")

        request.engineExecutionListener.executionStarted(root)

        Playwright.create().use { playwright ->
            println("[KotestJSTestEngine] Created Playwright $playwright")

            playwright.webkit().launch().use { browser ->

                root.children
                    .filterIsInstance<JSTestDescriptor>()
                    .forEach { jsTest ->
                        println("running $jsTest")

                        println("creating server...")
                        val server = embeddedServer(CIO, port = 0) {
                            install(CallLogging)
                            routing {
                                get("/") {
                                    call.respondHtml(HttpStatusCode.OK) {
                                        head {
                                            meta { charset = "UTF-8" }
                                            title { +"KotestJSTestEngine" }
                                        }
                                        body {
                                            attributes["style"] = "background-color: #161616;"

                                            // FIXME For main sources, KGP generates a combined .js file that contains all dependencies,
                                            //       but for test sources, we have to define all files manually.
                                            //       This is a hassle. Is there a better way?
                                            //@formatter:off
                                            script { src = "/src/kotlin-kotlin-stdlib.js" }
                                            script { src = "/src/kotlinx-atomicfu.js" }
                                            script { src = "/src/kotlinx-coroutines-core.js" }
                                            script { src = "/src/okio-parent-okio.js" }
                                            script { src = "/src/kotlinx-serialization-kotlinx-serialization-core.js" }
                                            script { src = "/src/kotest-js-junit-engine-kotest-js-junit-engine-messages.js" }
                                            script { src = "/src/kotlin-js-wasm-testing-v3.js" }
                                            script { src = "/src/kotlin-js-wasm-testing-v3-test.js" }
                                            //@formatter:on
                                        }
                                    }
                                }
                                staticFiles("/src", jsTest.jsDir.toFile())
                            }
                        }

                        println("launching server...")
                        server.start(wait = false)

                        val serverPort = server.resolvedConnectors().first().port
                        println("server started. port:$serverPort")

                        browser.newPage().use { page ->

                            // suspend, until we receive the KOTEST-FINISHED marker
                            suspendCoroutine { continuation ->

                                page.onConsoleMessage { msg ->
                                    val msgText = msg.text()
                                    println("[page ${page}] console message: $msgText")

                                    val kotestMsg = Regex("~~~KOTEST\\{([^\\}]+)\\}~~~").find(msgText)

                                    if (kotestMsg != null) {
                                        val message = kotestMsg.groupValues[1]
                                        val event: TestEvent = kxsBinary.decodeFromString(message)
                                        println("Got event $event")
                                        when (event.type) {
                                            AfterSuite -> ijLogger.afterSuite(event.descriptor, event.result)
                                            AfterTest -> ijLogger.afterTest(event.descriptor, event.result)
                                            BeforeSuite -> ijLogger.beforeSuite(event.descriptor)
                                            BeforeTest -> ijLogger.beforeTest(event.descriptor)
                                            ConfigurationError -> TODO()
                                            OnOutput -> ijLogger.onOutput(event.descriptor, event.output)
                                            ReportLocation -> TODO()
                                            Unknown -> TODO()
                                        }
                                    }
                                }

                                page.navigate("http://localhost:$serverPort/")

                                page.waitForConsoleMessage(
                                    Page.WaitForConsoleMessageOptions().setPredicate {
                                        it.text().trim() == "~~~KOTEST-FINISHED~~~"
                                    }
                                ) {
                                    continuation.resume(true)
                                }
                            }
                        }

                        println("test finished, stopping server...")
                        server.stop()
                    }
            }
        }

        request.engineExecutionListener.executionFinished(root, TestExecutionResult.successful())
    }
}
