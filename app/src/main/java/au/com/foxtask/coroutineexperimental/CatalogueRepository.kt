package au.com.foxtask.coroutineexperimental

import android.content.Context
import au.com.foxtask.coroutineexperimental.models.StoreEntityListWrapper
import kotlinx.coroutines.Deferred

interface CatalogueRepository {
    companion object {
        private val RETAILER_ID = "126"
        private val RETAILER_API_KEY = "w00lw0rth5A48E69B9C93E236B"
        private val FORMAT = "json"
    }

    suspend fun searchStoresBySuburbOrPostCode(suburbOrPostcode: String): Either<Failure, StoreEntityListWrapper>

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

        private suspend fun <T, R> request(call: Deferred<T>, transform: (T) -> Either<Failure, R>): Either<Failure, R> {
            return try {
                val response = call.await()
                transform(response)
            } catch (exception: Exception) {
                Either.Left(Failure.ServerError())
            }
        }
    }
}