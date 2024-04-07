package io.kotest.js_messages

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString.Companion.decodeBase64
import okio.use


val kxsBinary: KxsBinaryFormat = KxsBinaryFormat()

/**
 * Efficient binary format.
 *
 * See https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/formats.md#efficient-binary-format
 */
class KxsBinaryFormat(
    override val serializersModule: SerializersModule = EmptySerializersModule()
) : BinaryFormat, StringFormat {

    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
        encodeToBuffer(serializer, value).use { buffer ->
            buffer.readByteArray()
        }

    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        Buffer().write(bytes).use { buffer ->
            decodeFromBuffer(deserializer, buffer)
        }

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String =
        encodeToBuffer(serializer, value).use { buffer ->
            buffer.readByteString().base64()
        }

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        val source = string.decodeBase64() ?: error("Failed to deserialize value. Base64 decode failed $string")
        return Buffer().write(source).use { buffer ->
            decodeFromBuffer(deserializer, buffer)
        }
    }

    private fun <T> encodeToBuffer(serializer: SerializationStrategy<T>, value: T): Buffer {
        val output = Buffer()
        val encoder = KxsDataOutputEncoder(output, serializersModule)
        encoder.encodeSerializableValue(serializer, value)
        return output
    }

    private fun <T> decodeFromBuffer(deserializer: DeserializationStrategy<T>, input: Buffer): T {
        val decoder = KxsDataInputDecoder(input, serializersModule)
        return decoder.decodeSerializableValue(deserializer)
    }
}

private val byteArraySerializer = ByteArraySerializer()


@OptIn(ExperimentalSerializationApi::class)
class KxsDataOutputEncoder(
    private val output: BufferedSink,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : AbstractEncoder() {

    override fun encodeBoolean(value: Boolean): Unit = output.run { writeByte(if (value) 1 else 0) }
    override fun encodeByte(value: Byte): Unit = output.run { writeByte(value.toInt()) }
    override fun encodeChar(value: Char): Unit = output.run { writeUtf8CodePoint(value.code) }
    override fun encodeDouble(value: Double): Unit = output.run { writeLong(value.toRawBits()) }
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int): Unit = output.run { writeInt(index) }
    override fun encodeFloat(value: Float): Unit = output.run { writeInt(value.toRawBits()) }
    override fun encodeInt(value: Int): Unit = output.run { writeInt(value) }
    override fun encodeLong(value: Long): Unit = output.run { writeLong(value) }
    override fun encodeShort(value: Short): Unit = output.run { writeShort(value.toInt()) }
    override fun encodeString(value: String): Unit = output.run {
        encodeCompactSize(value.length)
        writeUtf8(value)
    }

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int
    ): CompositeEncoder {
        encodeCompactSize(collectionSize)
        return this
    }

    override fun encodeNull() = encodeBoolean(false)
    override fun encodeNotNullMark() = encodeBoolean(true)

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        when (serializer.descriptor) {
            byteArraySerializer.descriptor -> encodeByteArray(value as ByteArray)
            else -> super.encodeSerializableValue(serializer, value)
        }
    }

    private fun encodeByteArray(bytes: ByteArray) {
        encodeCompactSize(bytes.size)
        output.write(bytes)
    }

    private fun encodeCompactSize(value: Int) {
        if (value < 0xff) {
            output.writeByte(value)
        } else {
            output.writeByte(0xff)
            output.writeInt(value)
        }
    }
}


@OptIn(ExperimentalSerializationApi::class)
class KxsDataInputDecoder(
    private val input: BufferedSource,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
    private var elementsCount: Int = 0,
) : AbstractDecoder() {

    private var elementIndex = 0

    override fun decodeBoolean(): Boolean =
        when (val value = input.readByte().toInt()) {
            1 -> true
            0 -> false
            else -> error("failed to decode boolean. Expected 0 or 1, but got $value")
        }

    override fun decodeByte(): Byte = input.readByte()
    override fun decodeChar(): Char = input.readUtf8CodePoint().toChar()
    override fun decodeDouble(): Double = Double.fromBits(input.readLong())
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = input.readInt()
    override fun decodeFloat(): Float = Float.fromBits(input.readInt())
    override fun decodeInt(): Int = input.readInt()
    override fun decodeLong(): Long = input.readLong()
    override fun decodeShort(): Short = input.readShort()
    override fun decodeString(): String {
        val size = decodeCompactSize()
        return input.readUtf8(size.toLong())
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return if (elementIndex == elementsCount) {
            CompositeDecoder.DECODE_DONE
        } else {
            elementIndex++
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
        KxsDataInputDecoder(input, serializersModule, descriptor.elementsCount)

    override fun decodeSequentially(): Boolean = true

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int =
        decodeCompactSize().also { elementsCount = it }

    override fun decodeNotNullMark(): Boolean = decodeBoolean()

    override fun <T> decodeSerializableValue(
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T {
        @Suppress("UNCHECKED_CAST")
        return when (deserializer.descriptor) {
            byteArraySerializer.descriptor -> decodeByteArray() as T
            else -> super.decodeSerializableValue(deserializer, previousValue)
        }
    }

    private fun decodeByteArray(): ByteArray {
        val bytes = ByteArray(decodeCompactSize())
        input.readFully(bytes)
        return bytes
    }

    private fun decodeCompactSize(): Int {
        val byte = input.readByte().toInt() and 0xff
        return if (byte < 0xff) byte else input.readInt()
    }
}
