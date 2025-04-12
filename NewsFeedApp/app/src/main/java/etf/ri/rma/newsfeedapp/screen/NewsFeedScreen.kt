package etf.ri.rma.newsfeedapp.screen

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.screen.*
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun NewsFeedScreen() {
    var selectedCategory by remember { mutableStateOf("Sve") }
    val allNews = remember { NewsData.getAllNews() }

    val filteredNews = allNews.filter {
        selectedCategory == "Sve" || it.category == selectedCategory
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {

        CategoryFilterChips(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredNews.isEmpty()) {
            MessageCard("Nema pronađenih vijesti u kategoriji \"$selectedCategory\"")
        } else {
            NewsList(newsItems = filteredNews)
        }
    }
}
