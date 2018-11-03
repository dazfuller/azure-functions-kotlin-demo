package com.elastacloud.functions

import com.microsoft.azure.storage.table.TableServiceEntity
import java.time.Instant

class LogDataEntity constructor() : TableServiceEntity() {
    var message: String = ""
    var timestamp: Instant = Instant.EPOCH

    constructor(assetId: String, messageId: String) : this() {
        this.partitionKey = assetId
        this.rowKey = messageId
    }

    constructor(source: LogData) : this(source.assetId, source.messageId) {
        message = source.message ?: ""
        timestamp = Instant.parse(source.timestamp)
    }
}