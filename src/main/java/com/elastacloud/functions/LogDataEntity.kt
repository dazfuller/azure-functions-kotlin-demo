package com.elastacloud.functions

import com.microsoft.azure.storage.table.TableServiceEntity
import org.joda.time.DateTime
import java.util.Date

class LogDataEntity constructor() : TableServiceEntity() {
    var message: String = ""
    var loggedDate: Date = DateTime.parse("1970-01-01").toDate()

    constructor(assetId: String, messageId: String) : this() {
        this.partitionKey = assetId
        this.rowKey = messageId
    }

    constructor(source: LogData) : this(source.assetId, source.messageId) {
        message = source.message ?: ""
        loggedDate = DateTime.parse(source.loggedDate).toDate()
    }
}

