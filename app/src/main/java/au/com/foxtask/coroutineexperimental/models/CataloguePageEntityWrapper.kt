package au.com.foxtask.coroutineexperimental.models

import com.google.gson.annotations.SerializedName

data class CataloguePageEntityWrapper(
    @SerializedName("items")
    val item: CataloguePageEntity
)