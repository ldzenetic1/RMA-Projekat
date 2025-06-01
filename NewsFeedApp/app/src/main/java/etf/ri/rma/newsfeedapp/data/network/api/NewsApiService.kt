package etf.ri.rma.newsfeedapp.data.network.api

import retrofit2.http.GET
import retrofit2.http.Query

data class NewsApiResponse(
    val data: List<NewsApiArticle>
)

data class NewsApiArticle(
    val uuid: String,
    val title: String,
    val snippet: String,
    val image_url: String?,
    val categories: List<String>,
    val is_featured: Boolean?,
    val source: String,
    val published_at: String
)

interface NewsApiService {
    @GET("v1/news/top")
    suspend fun getTopStoriesByCategory(
        @Query("api_token") apiToken: String,
        @Query("categories") categories: String,
        @Query("limit") limit: Int = 3
    ): NewsApiResponse

    @GET("v1/news/similar")
    suspend fun getSimilarStories(
        @Query("api_token") token: String,
        @Query("uuid") uuid: String
    ): NewsApiResponse
}