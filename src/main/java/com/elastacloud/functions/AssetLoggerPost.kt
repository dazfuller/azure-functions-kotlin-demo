package com.elastacloud.functions

import com.microsoft.azure.functions.*
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger

class AssetLoggerPost {
    @FunctionName("AssetLoggerPost")
    fun run(
            @HttpTrigger(name = "request", methods = [HttpMethod.POST], authLevel = AuthorizationLevel.FUNCTION, route = "asset/data")
            request: HttpRequestMessage<LogData>,
            executionContext: ExecutionContext): HttpResponseMessage {
        val logger = executionContext.logger
        logger.info("Recording logging message")

        val entity = LogDataEntity(request.body)
        val connectionString = System.getenv("StorageConnectionString")
        val tableName = System.getenv("TableName")

        val tableClient = TableClient(connectionString, tableName)
        tableClient.saveLogMessage(entity)

        return request.createResponseBuilder(HttpStatus.OK)
                .build()
    }
}
