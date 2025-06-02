package etf.ri.rma.newsfeedapp.data.network.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ImagaApiService {
    @GET("v2/tags")
    suspend fun getImageTags(
        @Query("image_url") imageUrl: String
    ): retrofit2.Response<TaggingResponse>
}

data class TaggingResponse(
    val result: ResultData
)

data class ResultData(
    val tags: List<TagData>
)

data class TagData(
    val tag: Map<String, String>,
    val confidence: Double
)