package etf.ri.rma.newsfeedapp.screen


import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.screen.*
import etf.ri.rma.newsfeedapp.model.NewsItem


@Composable
fun NewsFeedScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        var selectedCategory by remember { mutableStateOf("Sve") }
        val allNews = remember { NewsData.getAllNews() }

        val filteredNews = allNews.filter {
            selectedCategory == "Sve" || it.category == selectedCategory
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {

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
}

@Composable
fun getCategoryColor(selectedCategory: String): Color {
    return when (selectedCategory) {
        "Sport" -> Color(0xFFADD8E6)
        "Politika" -> Color(0xFFE6E6FA)
        "Nauka/tehnologija" -> Color(0xFF90EE90)
        else -> {Color(0xFFCCC2DC)}
    }
}

