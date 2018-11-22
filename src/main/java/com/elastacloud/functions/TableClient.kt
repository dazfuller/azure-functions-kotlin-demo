package com.elastacloud.functions

import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.table.CloudTable
import com.microsoft.azure.storage.table.CloudTableClient
import com.microsoft.azure.storage.table.TableOperation
import com.microsoft.azure.storage.table.TableQuery
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class TableClient(connectionString: String, private val tableName: String) {
    private val client: CloudTableClient

    init {
        val storageAccount = CloudStorageAccount.parse(connectionString)
        client = storageAccount.createCloudTableClient()
    }

    companion object {
        const val PARTITION_NAME = "PartitionKey"
    }

    suspend fun saveLogMessage(data: LogDataEntity) = GlobalScope.async {
        val tableReference = getTableReference()
        val insertOperation = TableOperation.insertOrReplace(data)
        tableReference.await().execute(insertOperation)
    }

    suspend fun getAssetLogs(assetId: String) = GlobalScope.async {
        val tableReference = getTableReference()
        val partitionFilter = TableQuery.generateFilterCondition(
                PARTITION_NAME,
                TableQuery.QueryComparisons.EQUAL,
                assetId
        )

        val partitionQuery = TableQuery.from(LogDataEntity::class.java)
                .where(partitionFilter)

        val tableResult = async {
            tableReference.await().execute(partitionQuery).map {
                LogData(
                        it.partitionKey,
                        it.rowKey,
                        it.message,
                        it.loggedDate)
            }
        }

        tableResult.await()
    }

    private suspend fun getTableReference() = GlobalScope.async<CloudTable> {
        val tableReference = async {
            val reference = client.getTableReference(tableName)
            reference.createIfNotExists()
            reference
        }

        tableReference.await()
    }
}