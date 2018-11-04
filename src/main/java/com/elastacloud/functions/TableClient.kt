package com.elastacloud.functions

import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.table.CloudTable
import com.microsoft.azure.storage.table.CloudTableClient
import com.microsoft.azure.storage.table.TableOperation
import com.microsoft.azure.storage.table.TableQuery

class TableClient(connectionString: String, private val tableName: String) {
    private val client: CloudTableClient
    init {
        val storageAccount = CloudStorageAccount.parse(connectionString)
        client = storageAccount.createCloudTableClient()
    }

    companion object {
        const val PARTITION_NAME = "PartitionKey"
    }

    fun saveLogMessage(data: LogDataEntity) {
        val tableReference = getTableReference()
        val insertOperation = TableOperation.insertOrReplace(data)
        tableReference.execute(insertOperation)
    }

    fun getAssetLogs(assetId: String): Iterable<LogData> {
        val tableReference = getTableReference()
        val partitionFilter = TableQuery.generateFilterCondition(
                PARTITION_NAME,
                TableQuery.QueryComparisons.EQUAL,
                assetId
        )

        val partitionQuery = TableQuery.from(LogDataEntity::class.java)
                .where(partitionFilter)

        return tableReference.execute(partitionQuery).map { LogData(
                it.partitionKey,
                it.rowKey,
                it.message,
                it.loggedDate.toString())
        }
    }

    private fun getTableReference(): CloudTable {
        val tableReference = client.getTableReference(tableName)
        tableReference.createIfNotExists()
        return tableReference
    }
}