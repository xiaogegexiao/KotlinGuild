package au.com.foxtask.coroutineexperimental.models

import com.google.gson.annotations.SerializedName

data class StoreEntityListWrapper(
    @SerializedName("items")
    val itemList: List<StoreEntityWrapper>
)