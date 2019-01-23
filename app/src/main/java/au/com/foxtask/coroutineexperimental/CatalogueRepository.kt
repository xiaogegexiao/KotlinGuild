package au.com.foxtask.coroutineexperimental

import android.content.Context
import au.com.foxtask.coroutineexperimental.models.CatalogueEntity
import au.com.foxtask.coroutineexperimental.models.StoreEntityListWrapper
import kotlinx.coroutines.Deferred

interface CatalogueRepository {
    suspend fun searchStoresBySuburbOrPostCode(suburbOrPostcode: String): Either<Failure, StoreEntityListWrapper>
    suspend fun getCatalogue(storeId: String, saleId: String): Either<Failure, CatalogueEntity>

    class Network(val context: Context, val catalogueApi: CatalogueApi) : CatalogueRepository {
        private inline fun <R> preCheckNetwork(
            networkBlock: () -> Either<Failure, R>
        ): Either<Failure, R> {
            return when (context.isConnected) {
                true -> networkBlock()
                else -> Either.Left(Failure.NetworkConnection())
            }
        }

        override suspend fun searchStoresBySuburbOrPostCode(suburbOrPostcode: String): Either<Failure, StoreEntityListWrapper> {
            return preCheckNetwork {
                request(catalogueApi.searchStoresBySuburbOrPostCode(suburbOrPostcode)) {
                    Either.Right(it)
                }
            }
        }

        override suspend fun getCatalogue(storeId: String, saleId: String): Either<Failure, CatalogueEntity> {
            return preCheckNetwork {
                request(catalogueApi.getCatalogue(storeId, saleId)) {
                    Either.Right(it)
                }
            }
        }

        private suspend fun <T, R> request(call: Deferred<T>, transform: (T) -> Either<Failure, R>): Either<Failure, R> {
            return try {
                val response = call.await()
                transform(response)
            } catch (exception: Exception) {
                exception.printStackTrace()
                Either.Left(Failure.ServerError())
            }
        }
    }
}