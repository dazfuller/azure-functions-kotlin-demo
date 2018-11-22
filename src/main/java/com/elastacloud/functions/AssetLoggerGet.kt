package com.elastacloud.functions

import com.microsoft.azure.functions.*
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import kotlinx.coroutines.runBlocking

class AssetLoggerGet {
    @FunctionName("AssetLoggerGet")
    fun run(
            @HttpTrigger(name = "request", methods = [HttpMethod.GET], authLevel = AuthorizationLevel.FUNCTION, route = "asset/data")
            request: HttpRequestMessage<String?>,
            executionContext: ExecutionContext) = runBlocking<HttpResponseMessage> {
        val logger = executionContext.logger
        logger.info("Retrieving logging message")

        // Get the asset Id from the query string
        val assetId = request.queryParameters.getOrDefault("assetId", "")

        // If no asset Id is provided then return an error
        if (assetId.isBlank()) {
            logger.warning("Asset id not specified")
            return@runBlocking request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("No asset id specified in the URL query parameter")
                    .build()
        }

        // Get the storage connection details
        val connectionString = System.getenv("StorageConnectionString")
        val tableName = System.getenv("TableName")

        // Query the store for asset messages
        val tableClient = TableClient(connectionString, tableName)
        val logEntries = tableClient.getAssetLogs(assetId).await()

        // If no messages were found then return a 404 error response
        if (!logEntries.any()) {
            return@runBlocking request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("No data found for asset $assetId")
                    .build()
        }

        return@runBlocking request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(logEntries)
                .build()
    }
}