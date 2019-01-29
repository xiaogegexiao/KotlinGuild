package au.com.foxtask.coroutineexperimental

import android.content.Context
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import au.com.foxtask.coroutineexperimental.models.CataloguePageEntityWrapper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_catalogue_page.view.*


class CataloguePagerAdapter(private val context: Context) : PagerAdapter() {
    private val picasso = Picasso.get()
    private var cataloguePageList: MutableList<CataloguePageEntityWrapper> = arrayListOf()
    private val handler = Handler()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        var layout = container.getChildAt(position)
        if (layout == null) {
            layout = inflater.inflate(R.layout.layout_catalogue_page, container, false) as ViewGroup
            container.addView(layout)
        }
        cataloguePageList[position].apply {
            layout.tag = this
            layout.catalogue_view.addClickableAreas(
                item.pageItems.filter {
                    it.itemId != null && it.coordinates != null
                }.map {
                    it.coordinates!!
                },
                item.pageImageWidth.toFloat(),
                item.pageImageHeight.toFloat()
            )
            layout.page_index_text_view.text = position.toString()
            layout.page_index_text_view.visibility = View.VISIBLE
            layout.catalogue_view_layout.visibility = View.GONE
            handler.postAtTime(
                {
                    picasso.load(item.pageImageLink).tag(item.pageImageLink)
                        .into(layout.catalogue_view.getChildAt(0) as ImageView)
                    layout.page_index_text_view.visibility = View.GONE
                    layout.catalogue_view_layout.visibility = View.VISIBLE
                },
                position,
                SystemClock.uptimeMillis() + 1000
            )
        }
        return cataloguePageList[position]
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.tag == `object`
    }

    override fun getCount(): Int {
        return cataloguePageList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        handler.removeCallbacksAndMessages(position)
        picasso.cancelTag((`object` as CataloguePageEntityWrapper).item.pageImageLink)
    }

    fun updateCataloguePages(list: List<CataloguePageEntityWrapper>) {
        cataloguePageList.clear()
        cataloguePageList.addAll(list)
        notifyDataSetChanged()
    }
}