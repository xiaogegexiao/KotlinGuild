package au.com.foxtask.coroutineexperimental

import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import au.com.foxtask.coroutineexperimental.models.CatalogueEntity
import kotlinx.android.synthetic.main.activity_catalogue.*


class CatalogueActivity : AppCompatActivity(), ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener {
    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, CatalogueActivity::class.java))
        }
    }

    private lateinit var viewModel: CatalogueViewModel
    private lateinit var cataloguePagerAdapter: CataloguePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogue)
        cataloguePagerAdapter = CataloguePagerAdapter(this)
        pager.adapter = cataloguePagerAdapter
        pager.addOnPageChangeListener(this)
        viewpager_seekbar.setOnSeekBarChangeListener(this)
        viewModel = ViewModelProviders.of(this).get(CatalogueViewModel::class.java)

        listenToCatalogueUpdates()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCatalogue("4785", "26116")
    }

    private fun listenToCatalogueUpdates() {
        viewModel.catalogueLiveData.observe(this, Observer {
            updateCatalogues(it)
        })
        viewModel.failureLiveData.observe(this, Observer {

        })
    }

    private fun updateCatalogues(catalogueEntity: CatalogueEntity) {
        cataloguePagerAdapter.updateCataloguePages(catalogueEntity.cataloguePageItems)
        viewpager_seekbar.max = catalogueEntity.cataloguePageItems.size - 1
        viewpager_seekbar.progress = 0
    }

    // for listening to view pager
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        viewpager_seekbar.progress = position
    }

    // for listening to seek bar
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            pager.setCurrentItem(progress, true)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}
