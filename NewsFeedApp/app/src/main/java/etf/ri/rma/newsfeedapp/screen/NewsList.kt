package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.platform.testTag
import etf.ri.rma.newsfeedapp.model.NewsItem
import androidx.compose.ui.unit.dp

@Composable
fun NewsList(newsItems: List<NewsItem>, onNewsItemClick: (String) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.testTag("news_list")
    ) {
        items(newsItems, key = { it.id }) { news ->
            if (news.isFeatured) {
                FeaturedNewsCard(news){onNewsItemClick(news.id)}
            } else {
                StandardNewsCard(news){onNewsItemClick(news.id)}
            }
        }
    }
}