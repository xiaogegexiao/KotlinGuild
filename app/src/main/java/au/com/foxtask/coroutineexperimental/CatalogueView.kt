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
        Log.d(CatalogueView::class.java.simpleName, "new width: $w, height: $h, old width: $oldw, old height: $oldh")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (originHeight == 0f || originWidth == 0f || widthSize == 0 || heightSize == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        if (widthSize / heightSize > originWidth / originHeight) {
            setMeasuredDimension((originWidth / originHeight * height).toInt(), heightSize)
        } else {
            setMeasuredDimension(widthSize, (originHeight / originWidth * width).toInt())
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount <= 0) {
            return
        }
        Log.d(CatalogueView::class.java.simpleName, "current ratio is ${(r - l).toFloat() / (b - t).toFloat()} while origin ratio is ${originWidth / originHeight}")
        rectList?.let { list ->
            val width = (r - l).toDouble()
            val height = (b - t).toDouble()
            for (i in 1 until childCount - 1) {
                val child = getChildAt(i)
                child.layout(
                    Math.ceil(width / originWidth * list[i].left.toDouble()).toInt(),
                    Math.ceil(height / originHeight * list[i].top.toDouble()).toInt(),
                    Math.ceil(width / originWidth * list[i].right.toDouble()).toInt(),
                    Math.ceil(height / originHeight * list[i].bottom.toDouble()).toInt()
                )
            }
        }
    }

    fun addClickableAreas(rects: List<Rect>, width: Float, height: Float) {
        rectList = rects
        originWidth = width
        originHeight = height
        removeAllViews()

        LayoutInflater.from(context).inflate(R.layout.item_catalogue_image, this)
        for (rect in rects) {
            LayoutInflater.from(context).inflate(R.layout.item_catalogue_button, this)
        }
    }
}