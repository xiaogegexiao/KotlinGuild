package au.com.foxtask.coroutineexperimental

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup

class CatalogueView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {
    var originWidth = 0f
    var originHeight = 0f
    var rectList: List<Rect>? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        requestLayout()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount <= 0) {
            return
        }
        Log.d("CatalogueView", "changed $changed, l $l, t $t, r $r, b $b")
        rectList?.let { list ->
            val width = (r - l).toDouble()
            val height = (b - t).toDouble()
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.layout(
                    (width / originWidth * list[i].left.toFloat()).toInt(),
                    (height / originHeight * list[i].top.toFloat()).toInt(),
                    (width / originWidth * list[i].right.toFloat()).toInt(),
                    (height / originHeight * list[i].bottom.toFloat()).toInt()
                )
            }
        }
    }

    fun addClickableAreas(rects: List<Rect>, width: Float, height: Float) {
        rectList = rects
        originWidth = width
        originHeight = height
        removeAllViews()
        for (rect in rects) {
            LayoutInflater.from(context).inflate(R.layout.item_catalogue_button, this)
        }
    }
}