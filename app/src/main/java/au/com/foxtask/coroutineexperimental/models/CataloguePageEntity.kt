package au.com.foxtask.coroutineexperimental.models

import com.google.gson.annotations.SerializedName

data class CataloguePageEntity(
    @SerializedName("pageLink")
    val pageImageLink: String,
    @SerializedName("width")
    val pageImageWidth: Int,
    @SerializedName("height")
    val pageImageHeight: Int,
    @SerializedName("map")
    val pageItems: List<CataloguePageItemEntity>
)