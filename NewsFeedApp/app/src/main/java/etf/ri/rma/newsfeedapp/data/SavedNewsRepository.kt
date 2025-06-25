package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.data.database.NewsWithTags
import etf.ri.rma.newsfeedapp.model.NewsItem

object SavedNewsRepository {
    private lateinit var database: NewsDatabase

    fun initialize(db: NewsDatabase) {
        database = db
    }

    suspend fun savedNews(news: NewsItem): Boolean {
        val saveResult = database.savedNewsDAO().saveNews(news)
        if (saveResult) {
            val savedNewsWithTags = database.savedNewsDAO().getNewsByUuidWithTags(news.uuid)
            savedNewsWithTags?.news?.id?.let {
                news.id = it
            }
        }
        return saveResult
    }

    suspend fun getNewsByUuid(uuid: String): NewsWithTags? {
        val dao = database.savedNewsDAO()
        return dao.getNewsByUuidWithTags(uuid)
    }

    suspend fun allNews(): List<NewsItem> {
        val dao = database.savedNewsDAO()
        return dao.allNews()
    }

    suspend fun getNewsWithCategory(category: String): List<NewsItem> {
        val dao = database.savedNewsDAO()
        return dao.getNewsWithCategory(category)
    }

    suspend fun addTags(tags: List<String>, newsId: Int): Int {
        val dao = database.savedNewsDAO()
        dao.getNewsById(newsId) ?: return 0
        return dao.addTags(tags, newsId)
    }

    suspend fun getTags(newsId: Int): List<String> {
        val dao = database.savedNewsDAO()
        return dao.getTags(newsId)
    }

    suspend fun getSimilarNews(tags: List<String>): List<NewsItem> {
        val dao = database.savedNewsDAO()
        return dao.getSimilarNews(tags)
    }
}
