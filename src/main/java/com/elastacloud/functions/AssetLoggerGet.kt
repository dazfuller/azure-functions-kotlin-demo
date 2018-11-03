package com.elastacloud.functions

import com.microsoft.azure.functions.*
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger

class AssetLoggerGet {
    @FunctionName("AssetLoggerGet")
    fun run(
            @HttpTrigger(name = "request", methods = [HttpMethod.GET], authLevel = AuthorizationLevel.FUNCTION, route = "asset/data")
            request: HttpRequestMessage<String?>,
            executionContext: ExecutionContext): HttpResponseMessage {
        val logger = executionContext.logger
        logger.info("Retrieving logging message")

        val assetId = request.queryParameters.getOrDefault("assetId", "")

        if (assetId.isBlank()) {
            logger.warning("Asset id not specified")
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("No asset id specified in the URL query parameter")
                    .build()
        }

        val connectionString = System.getenv("StorageConnectionString")
        val tableName = System.getenv("TableName")

        val tableClient = TableClient(connectionString, tableName)
        val logEntries = tableClient.getAssetLogs(assetId)

        if (!logEntries.any()) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("No data found for asset $assetId")
                    .build()
        }

        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(logEntries)
                .build()
    }
}