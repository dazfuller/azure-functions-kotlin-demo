package com.elastacloud.functions

import org.joda.time.DateTime
import java.util.*

class LogData(
    val assetId: String = "",
    val messageId: String = "",
    val message: String? = null,
    val loggedDate: Date = DateTime.parse("1970-01-01").toDate()
)

