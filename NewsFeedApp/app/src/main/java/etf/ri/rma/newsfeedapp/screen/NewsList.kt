package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.platform.testTag
import etf.ri.rma.newsfeedapp.model.NewsItem
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text

@Composable
fun NewsList(newsItems: List<NewsItem>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.testTag("news_list")
    ) {
        items(newsItems) { news ->
            if (news.isFeatured) {
                FeaturedNewsCard(news)
            } else {
                StandardNewsCard(news)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class) //zbog FlowRow
@Composable
fun CategoryFilterChips(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 25.dp)
            .fillMaxWidth()
    ) {
        FilterChip(
            selected = selectedCategory == "Sve",
            onClick = { onCategorySelected("Sve") },
            label = { Text("Sve") },
            modifier = Modifier.testTag("filter_chip_all")
        )
        FilterChip(
            selected = selectedCategory == "Politika",
            onClick = { onCategorySelected("Politika") },
            label = { Text("Politika") },
            modifier = Modifier.testTag("filter_chip_pol")
        )
        FilterChip(
            selected = selectedCategory == "Sport",
            onClick = { onCategorySelected("Sport") },
            label = { Text("Sport") },
            modifier = Modifier.testTag("filter_chip_spo")
        )
        FilterChip(
            selected = selectedCategory == "Nauka/tehnologija",
            onClick = { onCategorySelected("Nauka/tehnologija") },
            label = { Text("Nauka/tehnologija") },
            modifier = Modifier.testTag("filter_chip_sci")
        )
        FilterChip(
            selected = selectedCategory == "Ljepota i zdravlje",
            onClick = { onCategorySelected("Ljepota i zdravlje") },
            label = { Text("Ljepota i zdravlje") },
            modifier = Modifier.testTag("filter_chip_none")
        )
    }
}
