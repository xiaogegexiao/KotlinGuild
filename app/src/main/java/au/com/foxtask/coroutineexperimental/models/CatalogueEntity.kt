package au.com.foxtask.coroutineexperimental.models

import com.google.gson.annotations.SerializedName

data class CatalogueEntity(
    @SerializedName("saleName")
    val saleName: String,
    @SerializedName("items")
    val cataloguePageItems: List<CataloguePageEntityWrapper>
)