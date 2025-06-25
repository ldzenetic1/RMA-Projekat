package etf.ri.rma.newsfeedapp.data

import NewsEntity
import NewsTagCrossRef
import TagEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import etf.ri.rma.newsfeedapp.data.database.NewsWithTags
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.TagValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.ArrayList

@Dao
interface SavedNewsDAO {

    @Query("SELECT EXISTS(SELECT 1 FROM News WHERE uuid = :uuid LIMIT 1)")
    suspend fun checkNewsExists(uuid: String): Boolean

    @Transaction
    @Query("SELECT * FROM News WHERE uuid = :uuid")
    suspend fun getNewsByUuidWithTags(uuid: String): NewsWithTags?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: NewsEntity): Long

    @Transaction
    suspend fun saveNews(news: NewsItem): Boolean {
        if (checkNewsExists(news.uuid)) return false

        val newsEntity = NewsEntity(
            uuid = news.uuid,
            title = news.title,
            snippet = news.snippet,
            imageUrl = news.imageUrl,
            category = news.category,
            isFeatured = news.isFeatured,
            source = news.source,
            publishedDate = news.publishedDate
        )
        val id = insertNews(newsEntity)
        return id != -1L
    }

    @Transaction
    @Query("SELECT * FROM News")
    suspend fun getAllNewsWithTags(): List<NewsWithTags>

    @Transaction
    suspend fun allNews(): List<NewsItem> {
        val newsWithTagsList = getAllNewsWithTags()
        return newsWithTagsList.map { newsWithTags ->
            NewsItem(
                id = newsWithTags.news.id,
                uuid = newsWithTags.news.uuid,
                title = newsWithTags.news.title,
                snippet = newsWithTags.news.snippet,
                imageUrl = newsWithTags.news.imageUrl,
                category = newsWithTags.news.category,
                isFeatured = newsWithTags.news.isFeatured,
                source = newsWithTags.news.source,
                publishedDate = newsWithTags.news.publishedDate,
                imageTags = ArrayList(newsWithTags.tags.map { TagValue(it.value) })
            )
        }
    }

    @Query("SELECT T.value FROM Tags AS T JOIN NewsTags AS NT ON T.id = NT.tagsId WHERE NT.newsId = :newsId")
    suspend fun getTags(newsId: Int): List<String>

    @Transaction
    @Query("SELECT * FROM News WHERE category = :category")
    suspend fun getNewsWithCategoryAndTags(category: String): List<NewsWithTags>

    @Transaction
    suspend fun getNewsWithCategory(category: String): List<NewsItem> {
        val newsWithTagsList = getNewsWithCategoryAndTags(category)
        return newsWithTagsList.map { newsWithTags ->
            NewsItem(
                id = newsWithTags.news.id,
                uuid = newsWithTags.news.uuid,
                title = newsWithTags.news.title,
                snippet = newsWithTags.news.snippet,
                imageUrl = newsWithTags.news.imageUrl,
                category = newsWithTags.news.category,
                isFeatured = newsWithTags.news.isFeatured,
                source = newsWithTags.news.source,
                publishedDate = newsWithTags.news.publishedDate,
                imageTags = ArrayList(newsWithTags.tags.map { TagValue(it.value) })
            )
        }
    }

    @Query("SELECT id FROM Tags WHERE value = :tagValue")
    suspend fun getTagIdByValue(tagValue: String): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewsTag(newsTag: NewsTagCrossRef)

    @Query("SELECT * FROM News WHERE id = :id")
    suspend fun getNewsById(id: Int): NewsEntity?

    @Transaction
    suspend fun addTags(tags: List<String>, newsId: Int): Int {
        var newTagsAddedCount = 0

        for (tagValue in tags) {
            var tagId = getTagIdByValue(tagValue)
            if (tagId == null) {
                val newId = insertTag(TagEntity(value = tagValue))
                if (newId != -1L) {
                    tagId = newId.toInt()
                    newTagsAddedCount++
                } else {
                    continue
                }
            }
            insertNewsTag(NewsTagCrossRef(newsId = newsId, tagsId = tagId!!))
        }
        return newTagsAddedCount
    }
    @Transaction
    @Query("""
        SELECT N.* FROM News AS N
        JOIN NewsTags AS NT ON N.id = NT.newsId
        JOIN Tags AS T ON NT.tagsId = T.id
        WHERE T.id IN (:tagIds)
        GROUP BY N.id
    """)
    suspend fun getNewsByTagIds(tagIds: List<Int>): List<NewsWithTags>

    @Transaction
    suspend fun getSimilarNews(tags: List<String>): List<NewsItem> {
        val tagIds = tags.mapNotNull { getTagIdByValue(it) }
        if (tagIds.isEmpty()) return emptyList()

        val similarNewsWithTags = getNewsByTagIds(tagIds)

        return similarNewsWithTags.map { newsWithTags ->
            NewsItem(
                id = newsWithTags.news.id,
                uuid = newsWithTags.news.uuid,
                title = newsWithTags.news.title,
                snippet = newsWithTags.news.snippet,
                imageUrl = newsWithTags.news.imageUrl,
                category = newsWithTags.news.category,
                isFeatured = newsWithTags.news.isFeatured,
                source = newsWithTags.news.source,
                publishedDate = newsWithTags.news.publishedDate,
                imageTags = ArrayList(newsWithTags.tags.map { TagValue(it.value) })
            )
        }.sortedByDescending {
            try {
                LocalDate.parse(it.publishedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            } catch (e: Exception) {
                LocalDate.MIN
            }
        }
    }
}
