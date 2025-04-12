package etf.ri.rma.newsfeedapp.model

data class NewsItem(
    val id: String,
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    val category: String,
    val isFeatured: Boolean,
    val source: String,
    val publishedDate: String
)
