package au.com.foxtask.coroutineexperimental

import au.com.foxtask.coroutineexperimental.models.StoreEntityListWrapper
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogueApi {
    @GET("regions/search")
    fun searchStoresBySuburbOrPostCode(
        @Query("id") retailerId: String
    ): Deferred<StoreEntityListWrapper>
}