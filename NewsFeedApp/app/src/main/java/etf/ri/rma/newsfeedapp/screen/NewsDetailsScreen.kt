package etf.ri.rma.newsfeedapp.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.NewsData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs


private fun parseDate(dateString: String): LocalDate {
    return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailsScreen(navController: NavController, newsId: String?) {
    val allNews = remember { NewsData.getAllNews() }
    val newsItem = remember(newsId) { allNews.find { it.id == newsId } }
    val relatedNews = remember(newsItem) {
        if (newsItem == null) return@remember emptyList()
        val currentItemDate = parseDate(newsItem.publishedDate)
        allNews
            .filter { it.id != newsItem.id && it.category == newsItem.category }
            .mapNotNull { related ->
                val relatedDate = parseDate(related.publishedDate)
                if (relatedDate != null) {
                    val diff = ChronoUnit.DAYS.between(currentItemDate, relatedDate)
                    Triple(related, diff, abs(diff))
                } else {
                    null
                }
            }
            .sortedWith(compareBy({ it.third }, { it.first.title }))
            .take(2)
            .map { it.first }
    }
    BackHandler(enabled = true) {
        navController.popBackStack("newsFeed", inclusive = false)
    }
    if (newsItem == null) {
        Scaffold(topBar = { TopAppBar(title = { Text("Greška") }) }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                MessageCard("Vijest nije pronađena.")
            }
        }
        return
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalji vijesti") }) },
        bottomBar = {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("details_close_button")
            ) { Text("Zatvori detalje") }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text(text = newsItem.title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.testTag("details_title")) }
            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }
            item { Text(text = newsItem.snippet, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.testTag("details_snippet")) }
            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }
            item {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Kategorija: ${newsItem.category}", modifier = Modifier.testTag("details_category"))
                    Text("Izvor: ${newsItem.source}", modifier = Modifier.testTag("details_source"))
                }
            }
            item { Text("Datum objave: ${newsItem.publishedDate}", modifier = Modifier.testTag("details_date")) }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Povezane vijesti iz iste kategorije:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (relatedNews.isEmpty()) {
                item { Text("Nema povezanih vijesti.") }
            } else {
                items(relatedNews.size) { index ->
                    val related = relatedNews[index]
                    val testTag = "related_news_title_${index + 1}"
                    Text(
                        text = "${related.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { navController.navigate("details/${related.id}") }
                            .testTag(testTag)
                            .padding(vertical = 4.dp)
                            .fillMaxWidth()
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
