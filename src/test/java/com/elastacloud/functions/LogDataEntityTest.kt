package com.elastacloud.functions

import org.joda.time.DateTime
import java.util.*
import kotlin.test.*

class LogDataEntityTest {
    companion object {
        fun parseDate(dateString: String): Date {
            return DateTime.parse(dateString).toDate()
        }
    }

    @Test
    fun `Default constructor creates an empty object`() {
        val entity = LogDataEntity()

        assertEquals(parseDate("1970-01-01T00:00:00.000Z"), entity.loggedDate)
        assertTrue { entity.message.isEmpty() }
    }

    @Test
    fun `Creation with string data sets service entity properties`() {
        val assetId = "000001"
        val messageId = "01010101"

        val entity = LogDataEntity(assetId, messageId)

        assertEquals(assetId, entity.partitionKey)
        assertEquals(messageId, entity.rowKey)
        assertEquals(parseDate("1970-01-01T00:00:00.000Z"), entity.loggedDate)
        assertTrue { entity.message.isEmpty() }
    }

    @Test
    fun `Creation from valid LogData entity`() {
        val sourceDate = parseDate("2018-09-13T17:12:13.000Z")
        val sourceEntity = LogData("001001", "1234", "Test message", sourceDate)
        val entity = LogDataEntity(sourceEntity)

        assertEquals("001001", entity.partitionKey)
        assertEquals("1234", entity.rowKey)
        assertEquals(sourceDate, entity.loggedDate)
        assertTrue { entity.message.isNotBlank() }
        assertEquals(sourceEntity.message, entity.message)
    }

    @Test
    fun `Creation with null message defaults to empty string`() {
        val sourceEntity = LogData("001001", "1234", null, parseDate("2018-01-01"))
        val entity = LogDataEntity(sourceEntity)

        assertTrue { entity.message.isEmpty() }
    }
}