package au.com.foxtask.coroutineexperimental

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import au.com.foxtask.coroutineexperimental.models.CataloguePageEntityWrapper
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.layout_catalogue_page.view.*
import java.lang.Exception


class CataloguePagerAdapter(private val context: Context) : PagerAdapter() {
    private val picasso = Picasso.get()
    private var cataloguePageList: MutableList<CataloguePageEntityWrapper> = arrayListOf()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.layout_catalogue_page, container, false) as ViewGroup
        container.addView(layout)
        return cataloguePageList[position].apply {
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
            picasso.load(item.pageImageLink).tag(item.pageImageLink).into(object: Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    layout.catalogue_view.background = BitmapDrawable(context.resources, bitmap)
                }
            })
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.tag == `object`
    }

    override fun getCount(): Int {
        return cataloguePageList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        picasso.cancelTag((`object` as CataloguePageEntityWrapper).item.pageImageLink)
    }

    fun updateCataloguePages(list: List<CataloguePageEntityWrapper>) {
        cataloguePageList.clear()
        cataloguePageList.addAll(list)
        notifyDataSetChanged()
    }
}