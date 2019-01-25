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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        Log.d(CatalogueView::class.java.simpleName, "$widthSize, $heightSize")
        if (originHeight == 0f || originWidth == 0f || widthSize == 0 || heightSize == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            if (widthSize / heightSize > originWidth / originHeight) {
                widthSize = (originWidth / originHeight * heightSize).toInt()
            } else {
                heightSize = (originHeight / originWidth * widthSize).toInt()
            }
            setMeasuredDimension(widthSize, heightSize)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount <= 0) {
            return
        }

        val imageView = getChildAt(0)
        imageView.layout(0, 0, r - l, b - t)
        rectList?.let { list ->
            val width = (r - l).toDouble()
            val height = (b - t).toDouble()
            for (i in 0 until childCount - 1) {
                val child = getChildAt(i + 1)
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