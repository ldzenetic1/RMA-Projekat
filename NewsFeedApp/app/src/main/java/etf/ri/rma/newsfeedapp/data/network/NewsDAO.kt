package etf.ri.rma.newsfeedapp.data.network

import android.content.Context
import etf.ri.rma.newsfeedapp.data.NewsDatabase
import etf.ri.rma.newsfeedapp.data.SavedNewsRepository
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidUUIDException
import etf.ri.rma.newsfeedapp.model.NewsItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiArticle
import etf.ri.rma.newsfeedapp.data.getInitialNews
import okhttp3.OkHttpClient
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class NewsDAO (private val context: Context) {

    private val NEWS_API_TOKEN = "p36HMCF7NSf5SMNGqH5lmsZ0cCTHrzB8QYRZYXxd"
    private val NEWS_BASE_URL = "https://api.thenewsapi.com/"

    private val defaultOkHttpClient = OkHttpClient.Builder().build()
    private lateinit var api: NewsApiService

    private val lastApiCallTime = ConcurrentHashMap<String, Long>()
    private val API_CALL_THRESHOLD_SECONDS = 30L

    private val allCachedNews = ConcurrentHashMap<String, NewsItem>()
    private val headlinesBySourceCache = ConcurrentHashMap<String, List<NewsItem>>()

    private val initialNews = getInitialNews()

    init {
        if (!this::api.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(NEWS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(defaultOkHttpClient)
                .build()
            api = retrofit.create(NewsApiService::class.java)
        }
        initialNews.forEach { newsItem ->
            allCachedNews[newsItem.uuid] = newsItem
        }
        SavedNewsRepository.initialize(NewsDatabase.getDatabase(context))
    }

    companion object {
        @Volatile
        private var INSTANCE: NewsDAO? = null

        fun getInstance(context: Context): NewsDAO =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: NewsDAO(context.applicationContext).also { INSTANCE = it }
            }
    }
    fun setApiService(service: NewsApiService) {
        this.api = service
    }

    private fun mapLocalCategoryToApiCategory(localCategory: String): String? {
        return when (localCategory) {
            "Sve" -> null
            "Politika", "politics" -> "politics"
            "Sport", "sports" -> "sports"
            "Nauka", "sci" -> "science"
            "Tehnologija", "tech" -> "tech"
            "Ljepota i zdravlje" -> "health"
            else -> null
        }
    }

    private fun mapApiCategoryToLocalCategory(apiCategory: String): String {
        return when (apiCategory) {
            "politics" -> "Politika"
            "sports" -> "Sport"
            "science" -> "Nauka"
            "tech" -> "Tehnologija"
            "health" -> "Ljepota i zdravlje"
            "business" -> "Posao"
            "entertainment" -> "Zabava"
            "travel" -> "Putovanja"
            else -> "Ostalo"
        }
    }

    private fun NewsApiArticle.toNewsItem(): NewsItem {
        val mappedCategory = categories.firstOrNull()?.let { mapApiCategoryToLocalCategory(it) } ?: "Ostalo"
        return NewsItem(
            uuid = this.uuid,
            title = this.title,
            snippet = this.snippet,
            imageUrl = this.image_url,
            category = mappedCategory,
            isFeatured = false,
            source = this.source,
            publishedDate = this.published_at.substringBefore("T").split("-").reversed().joinToString("-"),
            imageTags = arrayListOf()
        )
    }

    suspend fun getTopStoriesByCategory(category: String, isConnected: Boolean): List<NewsItem> {
        val apiCategory = mapLocalCategoryToApiCategory(category)
        if (apiCategory == null) {
            return SavedNewsRepository.getNewsWithCategory(category)
        }

        val currentTime = System.currentTimeMillis()
        val lastCall = lastApiCallTime[category] ?: 0L
        val needsRefresh = (currentTime - lastCall) > TimeUnit.SECONDS.toMillis(API_CALL_THRESHOLD_SECONDS)

        if (isConnected && needsRefresh) {
            try {
                val response = api.getTopStoriesByCategory(NEWS_API_TOKEN, apiCategory, 3)
                val newStories = response.data.map { it.toNewsItem() }

                newStories.forEach { newsItem ->
                    SavedNewsRepository.savedNews(newsItem)
                    allCachedNews[newsItem.uuid] = newsItem
                }
                lastApiCallTime[category] = currentTime
                return newStories
            } catch (e: Exception) {
                e.printStackTrace()
                println("Greska prilikom dohvatanja vijesti iz kategorije $category: ${e.message}")
                return SavedNewsRepository.getNewsWithCategory(mapApiCategoryToLocalCategory(apiCategory))
            }
        } else {
            val localCategory = mapApiCategoryToLocalCategory(apiCategory)
            return SavedNewsRepository.getNewsWithCategory(localCategory)
        }
    }

    fun getAllStories(): List<NewsItem> {
        return allCachedNews.values.toList()
    }

    private val similarStoriesCache = ConcurrentHashMap<String, List<NewsItem>>()

    suspend fun getSimilarStories(uuid: String, isConnected: Boolean): List<NewsItem> {
        if (!uuid.matches(Regex("^[0-9a-fA-F0-9-]{36}$"))) {
            throw InvalidUUIDException("Neispravan UUID format: $uuid")
        }

        similarStoriesCache[uuid]?.let { return it }

        val originalNewsFromCache = allCachedNews[uuid]
        val originalNewsId = originalNewsFromCache?.id ?: (SavedNewsRepository.getNewsByUuid(uuid)?.news?.id ?: -1)

        if (originalNewsId != -1) {
            val tagsForOriginalNews = SavedNewsRepository.getTags(originalNewsId)
            if (tagsForOriginalNews.isNotEmpty()) {
                val similarFromDb = SavedNewsRepository.getSimilarNews(tagsForOriginalNews.take(2))
                if (similarFromDb.isNotEmpty()) {
                    similarStoriesCache[uuid] = similarFromDb
                    return similarFromDb
                }
            }
        }
        if(isConnected) {
            try {
                val response = api.getSimilarStories(NEWS_API_TOKEN, uuid)
                val similar = response.data.map { it.toNewsItem() }
                similarStoriesCache[uuid] = similar

                similar.forEach { item ->
                    SavedNewsRepository.savedNews(item)
                    allCachedNews.putIfAbsent(item.uuid, item)
                }
                return similar
            } catch (e: Exception) {
                e.printStackTrace()

                val original = allCachedNews[uuid]
                    ?: throw InvalidUUIDException("UUID ne postoji u lokalnoj listi: $uuid")

                val sameCategoryNews = allCachedNews.values.filter {
                    it.uuid != uuid && it.category == original.category
                }
                val fallbackSimilar = sameCategoryNews.take(2)
                similarStoriesCache[uuid] = fallbackSimilar
                return fallbackSimilar
            }
        }
         else{
             val original = allCachedNews[uuid]
                 ?: throw InvalidUUIDException("UUID ne postoji u lokalnoj listi:  $uuid")
             val sameCategoryNews = allCachedNews.values.filter {
                 it.uuid != uuid && it.category == original.category
             }
            val fallbackSimilar = sameCategoryNews.take(2)
            similarStoriesCache[uuid] = fallbackSimilar
            return fallbackSimilar
        }
    }
    suspend fun getHeadlinesBySource(source: String, isConnected: Boolean): List<NewsItem> {
        headlinesBySourceCache[source]?.let { return it }

        if(isConnected) {
            try {
                val response = api.getHeadlinesBySource(NEWS_API_TOKEN, source)
                val headlines = response.data.map { it.toNewsItem() }
                headlinesBySourceCache[source] = headlines
                headlines.forEach { item ->
                    SavedNewsRepository.savedNews(item)
                    allCachedNews.putIfAbsent(item.uuid, item)
                }
                return headlines
            } catch (e: Exception) {
                e.printStackTrace()
                val fallbackHeadlines = SavedNewsRepository.allNews().filter { it.source == source }
                headlinesBySourceCache[source] = fallbackHeadlines
                return fallbackHeadlines
            }
        } else {
            val fallbackHeadlines = SavedNewsRepository.allNews().filter { it.source == source }
            headlinesBySourceCache[source] = fallbackHeadlines
            return fallbackHeadlines
        }
    }
    fun clearCacheForTesting() {
        lastApiCallTime.clear()
        allCachedNews.clear()
        headlinesBySourceCache.clear()
        initialNews.forEach { newsItem ->
            allCachedNews[newsItem.uuid] = newsItem
        }
    }
}