package etf.ri.rma.newsfeedapp.model

import java.util.ArrayList

data class NewsItem(
    var id: Int = 0,
    val uuid: String,
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    val category: String,
    val isFeatured: Boolean,
    val source: String,
    val publishedDate: String,
    val imageTags: ArrayList<TagValue> = arrayListOf()
)
