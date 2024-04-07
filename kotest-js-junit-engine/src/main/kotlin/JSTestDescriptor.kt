package io.kotest.runner.junit.platform

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import java.nio.file.Path

data class JSTestDescriptor(
    private val uniqueId: UniqueId,
    private val displayName: String,
    internal val jsDir: Path,
) : AbstractTestDescriptor(uniqueId, displayName) {
    override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
}
