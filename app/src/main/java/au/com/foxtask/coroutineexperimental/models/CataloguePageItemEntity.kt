package au.com.foxtask.coroutineexperimental.models

import android.graphics.Rect
import com.google.gson.annotations.SerializedName

data class CataloguePageItemEntity(
    @SerializedName("coords")
    val coordinates: Rect?,
    @SerializedName("itemId")
    val itemId: String?,
    @SerializedName("itemSKU")
    val itemSKU: String?,
    @SerializedName("itemName")
    val itemName: String?
)