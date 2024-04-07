package io.kotest.js_messages

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import okio.ByteString
import okio.ByteString.Companion.decodeBase64
import kotlin.jvm.JvmInline


@Serializable
data class TestEvent(
    val type: TestEventType,
    val descriptor: TestDescriptor,
    val output: TestOutput? = null,
    val result: TestResult? = null,
)


@Serializable
data class TestDescriptor(
    val id: String,
    val name: String,
    val displayName: String = name,
    val className: String? = null,
    val isComposite: Boolean = false,
    val parent: TestDescriptor? = null,
)


@Serializable
data class TestOutput(
    val destination: Destination,
    val message: String,
) {
    enum class Destination {
        StdOut, StdErr
    }
}


@Serializable
data class TestResult(
    val result: ResultType,
    val failures: List<TestFailure> = emptyList(),
    val exceptions: List<String> = emptyList(), // TODO wrapper for Throwable
    /** start time, in milliseconds since the epoch. */
    val startTime: Long? = null,
    /** time, in milliseconds since the epoch. */
    val endTime: Long? = null,
    val testCount: Long = 0,
    val successfulTestCount: Long = 0,
    val failedTestCount: Long = 0,
    val skippedTestCount: Long = 0,
) {

    val exception: String? = exceptions.firstOrNull()

    @Serializable
    enum class ResultType {
        Success, Failure, Skipped
    }

    @Serializable
    data class TestFailure(
        val causes: List<TestFailure> = emptyList(),
        val rawFailure: String?, // TODO wrapper for Throwable
        val details: TestFailureDetails? = null,
    )

    @Serializable
    data class TestFailureDetails(
        val message: String? = null,
        val className: String? = null,
        val stacktrace: String? = null,
        val isAssertionFailure: Boolean = false,
        val isFileComparisonFailure: Boolean = false,
        val expectedContent: ByteStringBase64 = ByteString.EMPTY,
        val actualContent: ByteStringBase64 = ByteString.EMPTY,
        val expected: String? = null,
        val actual: String? = null,
    )
}


@Serializable
// https://github.com/JetBrains/intellij-community/blob/6faed1607b9d86aa29c1d8e02e1f2c3dfac27eea/plugins/gradle/java/src/execution/test/runner/events/TestEventType.java
enum class TestEventType(val id: String) {

    AfterSuite("afterSuite"),
    AfterTest("afterTest"),
    BeforeSuite("beforeSuite"),
    BeforeTest("beforeTest"),
    ConfigurationError("configurationError"),
    OnOutput("onOutput"),
    ReportLocation("reportLocation"),
    Unknown("unknown"),
    ;

    companion object {
        fun fromId(id: String): TestEventType =
            entries.firstOrNull { it.id == id } ?: Unknown
    }
}


internal typealias ByteStringBase64 =
        @Serializable(with = ByteStringBase64Serializer::class) ByteString

internal object ByteStringBase64Serializer : KSerializer<ByteString> {

    @Serializable
    @JvmInline
    private value class Delegate(val value: String)

    override val descriptor: SerialDescriptor = Delegate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ByteString {
        val delegate = decoder.decodeSerializableValue(Delegate.serializer())
        return delegate.value.decodeBase64() ?: error("Error decoding Base64 ${delegate.value}")
    }

    override fun serialize(encoder: Encoder, value: ByteString) {
        val delegate = Delegate(value.utf8())
        encoder.encodeSerializableValue(Delegate.serializer(), delegate)
    }
}
