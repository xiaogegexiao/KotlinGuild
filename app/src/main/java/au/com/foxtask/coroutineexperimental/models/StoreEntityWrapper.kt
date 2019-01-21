package au.com.foxtask.coroutineexperimental.models

import com.google.gson.annotations.SerializedName

data class StoreEntityWrapper(
    @SerializedName("items")
    val item: StoreEntity
)