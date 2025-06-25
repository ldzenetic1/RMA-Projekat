package etf.ri.rma.newsfeedapp.data.network

import android.content.Context
import etf.ri.rma.newsfeedapp.data.SavedNewsRepository
import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import okhttp3.Credentials
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap
import okhttp3.OkHttpClient

class ImagaDAO(private val context: Context) {

    private val IMAGGA_API_KEY = "acc_4331b36d29360e6"
    private val IMAGGA_API_SECRET = "9ff0792132ee2f59613ae9aef222e25c"
    private val IMAGGA_BASE_URL = "https://api.imagga.com"

    private val defaultCredentials = Credentials.basic(IMAGGA_API_KEY, IMAGGA_API_SECRET)

    private lateinit var api: ImagaApiService

    private val imageTagsCache = ConcurrentHashMap<String, ArrayList<String>>()

    companion object {
        @Volatile
        private var INSTANCE: ImagaDAO? = null

        fun getInstance(context: Context): ImagaDAO =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ImagaDAO(context.applicationContext).also { INSTANCE = it }
            }
    }

    init {
        if (!this::api.isInitialized) {
            val defaultOkHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", defaultCredentials)
                        .build()
                    chain.proceed(request)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(IMAGGA_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(defaultOkHttpClient)
                .build()
            api = retrofit.create(ImagaApiService::class.java)
        }
    }

    fun setApiService(service: ImagaApiService) {
        this.api = service
    }

    suspend fun getTags(imageUrl: String, newsId: Int?, isConnected: Boolean): ArrayList<String> {
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            throw InvalidImageURLException("Neispravan URL format: $imageUrl")
        }
        if (imageTagsCache.containsKey(imageUrl)) {
            return imageTagsCache[imageUrl] ?: arrayListOf()
        }
        if (newsId != null) {
            val tagsFromDb = SavedNewsRepository.getTags(newsId)
            if (tagsFromDb.isNotEmpty()) {
                val tagsArrayList = ArrayList(tagsFromDb)
                imageTagsCache[imageUrl] = tagsArrayList
                return tagsArrayList
            }
        }

        if (isConnected) {
            try {
                val response = api.getImageTags(imageUrl)
                if (response.isSuccessful) {
                    val tags = response.body()?.result?.tags?.mapNotNull { it.tag["en"] }?.let{ ArrayList(it) } ?: arrayListOf()
                    imageTagsCache[imageUrl] = tags

                    if (newsId != null) {
                        SavedNewsRepository.addTags(tags, newsId!!)
                    }
                    return tags
                } else {
                    throw InvalidImageURLException("Greška prilikom dohvatanja tagova za sliku: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw InvalidImageURLException("Greška prilikom dohvatanja tagova za sliku: ${e.message}")
            }
        } else {
           return arrayListOf()
        }
    }
}
