package io.kotest.runner.junit.platform

import io.kotest.js_messages.TestDescriptor
import io.kotest.js_messages.TestEventType
import io.kotest.js_messages.TestOutput
import io.kotest.js_messages.TestResult

class IJTestEventLogger {

    private val testEventXmlBuilder = TestEventXmlBuilder()

    fun beforeSuite(suite: TestDescriptor) {
        logTestEvent(TestEventType.BeforeSuite, suite, null, null)
    }

    fun afterSuite(suite: TestDescriptor, result: TestResult?) {
        logTestEvent(TestEventType.AfterSuite, suite, null, result)
    }

    fun beforeTest(testDescriptor: TestDescriptor) {
        logTestEvent(TestEventType.BeforeTest, testDescriptor, null, null)
    }

    fun afterTest(testDescriptor: TestDescriptor, result: TestResult?) {
        logTestEvent(TestEventType.AfterTest, testDescriptor, null, result)
    }

    fun onOutput(testDescriptor: TestDescriptor, outputEvent: TestOutput?) {
        logTestEvent(TestEventType.OnOutput, testDescriptor, outputEvent, null)
    }

    private fun logTestEvent(
        testEventType: TestEventType,
        testDescriptor: TestDescriptor,
        testEvent: TestOutput?,
        testResult: TestResult?,
    ) {
        val xml = testEventXmlBuilder
            .build(testEventType, testDescriptor, testEvent, testResult)
            .lines().joinToString("\n")

//    logger.lifecycle("[IJTestEventLogger] received $testEventType, created XML $xml")

        val ijLog = "<ijLog>$xml</ijLog>"

        println("ijlog: $xml")
        println(ijLog)
    }
}
