package au.com.foxtask.coroutineexperimental

import android.app.Application
import android.graphics.Rect
import androidx.lifecycle.MutableLiveData
import au.com.foxtask.coroutineexperimental.base.BaseViewModel
import au.com.foxtask.coroutineexperimental.models.CatalogueEntity
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Job
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CatalogueViewModel(application: Application) : BaseViewModel(application) {
    companion object {
        private val RETAILER_ID = "126"
        private val RETAILER_API_KEY = "w00lw0rth5A48E69B9C93E236B"
        private val FORMAT = "json"
    }

    var catalogueLiveData: MutableLiveData<CatalogueEntity> = MutableLiveData()

    val gson = GsonBuilder()
        .registerTypeAdapter(Rect::class.java, object : TypeAdapter<Rect>() {
            override fun read(`in`: JsonReader?): Rect? {
                return if (`in`?.hasNext() == true) {
                    val coordsStr = `in`.nextString()
                    coordsStr.split(" ", ",").map {
                        it.filter { char -> char in '0'..'9' }.toInt()
                    }.foldIndexed(Rect()) { index, rect, coord ->
                        when (index) {
                            0 -> rect.left = coord
                            1 -> rect.top = coord
                            2 -> rect.right = coord
                            else -> rect.bottom = coord
                        }
                        rect
                    }
                } else {
                    null
                }
            }

            override fun write(out: JsonWriter?, value: Rect?) {
                out?.let { writer ->
                    writer.beginObject()
                    value?.flattenToString()?.split(" ")?.joinToString(",")?.let {
                        writer.value(it)
                    } ?: writer.nullValue()
                    writer.endObject()
                }
            }

        })
        .serializeNulls()
        .create()
    val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val original = chain.request()
        val originalHttpUrl = original.url()

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("apikey", RETAILER_API_KEY)
            .addQueryParameter("format", FORMAT)
            .build()

        // Request customization: add request headers
        val requestBuilder = original.newBuilder()
            .url(url)

        val request = requestBuilder.build()
        chain.proceed(request)
    }.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }).build()

    val catalogueApi = Retrofit
        .Builder()
        .baseUrl("https://webservice.salefinder.com.au/index.php/api/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(CatalogueApi::class.java)

    val catalogueRepository = CatalogueRepository.Network(application, catalogueApi)

    private val getCatalogueCase: GetCatalogueCase = GetCatalogueCase(catalogueRepository)

    fun getCatalogue(
        storeId: String,
        saleId: String,
        onPreExecute: () -> Unit = {},
        onPostExecute: () -> Unit = {}
    ): Job {
        return getCatalogueCase(
            GetCatalogueCase.GetCatalogueParams(storeId, saleId),
            mBgScope,
            mFgScope,
            onPreExecute = onPreExecute,
            onPostExecute = onPostExecute
        ) {
            it.either(::handleFailure, ::handleCatalogue)
        }
    }

    private fun handleCatalogue(catalogueEntity: CatalogueEntity) {
        catalogueLiveData.postValue(catalogueEntity)
    }
}