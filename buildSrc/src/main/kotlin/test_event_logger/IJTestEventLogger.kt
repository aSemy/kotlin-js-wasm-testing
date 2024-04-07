package test_event_logger

import org.gradle.api.tasks.testing.*
import org.gradle.kotlin.dsl.support.normaliseLineSeparators
import org.gradle.kotlin.dsl.support.useToRun
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class IJTestEventLogger(
    private val testReportDir: File?,
) : TestListener, TestOutputListener {

    private val testEventXmlBuilder = TestEventXmlBuilder()

    override fun beforeSuite(suite: TestDescriptor) {
        logTestEvent(TestEventType.BEFORE_SUITE, suite, null, null)
    }

    override fun afterSuite(suite: TestDescriptor, result: TestResult?) {
        logTestEvent(TestEventType.AFTER_SUITE, suite, null, result)
    }

    override fun beforeTest(testDescriptor: TestDescriptor) {
        logTestEvent(TestEventType.BEFORE_TEST, testDescriptor, null, null)
    }

    override fun afterTest(testDescriptor: TestDescriptor, result: TestResult?) {
        logTestEvent(TestEventType.AFTER_TEST, testDescriptor, null, result)
    }

    override fun onOutput(testDescriptor: TestDescriptor, outputEvent: TestOutputEvent?) {
        logTestEvent(TestEventType.ON_OUTPUT, testDescriptor, outputEvent, null)
    }

    private fun logTestEvent(
        testEventType: TestEventType,
        testDescriptor: TestDescriptor,
        testEvent: TestOutputEvent?,
        testResult: TestResult?,
    ) {
        val xml = testEventXmlBuilder
            .build(testEventType, testDescriptor, testEvent, testResult)
            .normaliseLineSeparators()

//    logger.lifecycle("[IJTestEventLogger] received $testEventType, created XML $xml")

        val ijLog = "<ijLog>$xml</ijLog>"

        println(ijLog)

        if (testReportDir != null) {
            appendTestLogFile(testReportDir, ijLog)
        }
    }

    companion object {

        @Synchronized // make a worker? or build service?
        private fun appendTestLogFile(testReportDir: File, ijLog: String) {

            // group log files together by time so Gradle doesn't get flooded with tiny files,
            // and large files are too big to work with.
            val filename: String = TimeUnit.MILLISECONDS
                .toSeconds(System.currentTimeMillis())
                .toString()
                .dropLast(2)

            val testXmlFile: File = testReportDir.resolve(filename + IJ_TEST_EVENT_LOG_FILE_EXT)

            testXmlFile.createNewFile()

//      logger.lifecycle("[IJTestEventLogger] appending xml to file $testXmlFile")

            FileOutputStream(testXmlFile, true).bufferedWriter().useToRun {
                appendLine(ijLog)
            }
        }
    }
}


const val IJ_TEST_EVENT_LOG_FILE_EXT = ".ij_test.log"
