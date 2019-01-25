package au.com.foxtask.coroutineexperimental

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.otaliastudios.zoom.ZoomLayout

class CustomZoomLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ZoomLayout(context, attrs, defStyleAttr) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Measure ourselves as MATCH_PARENT
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == View.MeasureSpec.UNSPECIFIED || heightMode == View.MeasureSpec.UNSPECIFIED) {
            throw RuntimeException("must be used with fixed dimensions (e.g. match_parent)")
        }
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)

        // Measure our child as unspecified.
        measureChildren(MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.UNSPECIFIED))
    }
}