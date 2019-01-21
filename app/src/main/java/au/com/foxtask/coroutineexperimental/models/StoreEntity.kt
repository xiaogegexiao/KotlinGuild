package au.com.foxtask.coroutineexperimental.models

import com.google.gson.annotations.SerializedName

data class StoreEntity(
    @SerializedName("storeId")
    val storeId: String,
    @SerializedName("postcode")
    val postcode: String,
    @SerializedName("displayName")
    val storeDisplayName: String,
    @SerializedName("tag")
    val state: String
)
