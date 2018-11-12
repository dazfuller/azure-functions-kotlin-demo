package com.elastacloud.functions

import com.microsoft.azure.storage.table.TableServiceEntity
import org.joda.time.DateTime
import java.util.Date

class LogDataEntity constructor() : TableServiceEntity() {
    var message: String = ""
    var loggedDate: Date = DateTime.parse("1970-01-01T00:00:00.000Z").toDate()

    constructor(assetId: String, messageId: String) : this() {
        this.partitionKey = assetId
        this.rowKey = messageId
    }

    constructor(source: LogData) : this(source.assetId, source.messageId) {
        message = source.message ?: ""
        loggedDate = source.loggedDate
    }
}

