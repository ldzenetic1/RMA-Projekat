package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.NewsData
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private fun parseDate(dateString: String): LocalDate {
    return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NewsFeedScreen(navController: NavController) {
    val allNews = remember { NewsData.getAllNews() }
    val currentBackStackEntry = navController.currentBackStackEntry
    val savedStateHandle = currentBackStackEntry?.savedStateHandle
    var localSelectedCategory by remember { mutableStateOf("Sve") }
    var advancedFilterApplied by remember { mutableStateOf(false) }
    var savedCategoryFromFilter by remember { mutableStateOf("Sve") }
    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDateMillis by remember { mutableStateOf<Long?>(null) }
    var unwantedWords by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(savedStateHandle) {
        val categoryFromFilter = savedStateHandle?.get<String>("selectedCategory")
        val startFromFilter = savedStateHandle?.get<Long?>("startDateMillis")
        val endFromFilter = savedStateHandle?.get<Long?>("endDateMillis")
        val wordsFromFilter = savedStateHandle?.get<ArrayList<String>>("unwantedWords")
        if (categoryFromFilter != null || startFromFilter != null || endFromFilter != null || wordsFromFilter != null) {
            savedCategoryFromFilter = categoryFromFilter ?: "Sve"
            startDateMillis = startFromFilter
            endDateMillis = endFromFilter
            unwantedWords = wordsFromFilter ?: emptyList()
            localSelectedCategory = savedCategoryFromFilter
            advancedFilterApplied = true
            savedStateHandle?.remove<String>("selectedCategory")
            savedStateHandle?.remove<Long?>("startDateMillis")
            savedStateHandle?.remove<Long?>("endDateMillis")
            savedStateHandle?.remove<ArrayList<String>>("unwantedWords")
        }
    }
    val filteredNews = remember(allNews, localSelectedCategory, startDateMillis, endDateMillis, unwantedWords, advancedFilterApplied) {
        val categoryToFilter = localSelectedCategory
        val startDate = if (advancedFilterApplied) startDateMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() } else null
        val endDate = if (advancedFilterApplied) endDateMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() } else null
        val wordsToFilter = if (advancedFilterApplied) unwantedWords else emptyList()
        allNews.filter { news ->
            val categoryMatch = categoryToFilter == "Sve" || news.category == categoryToFilter
            val dateMatch = if (startDate != null && endDate != null) {
                val newsDate = parseDate(news.publishedDate)
                newsDate != null && !newsDate.isBefore(startDate) && !newsDate.isAfter(endDate)
            } else {
                true
            }
            val unwantedMatch = wordsToFilter.none { unwanted ->
                news.title.contains(unwanted, ignoreCase = true) || news.snippet.contains(unwanted, ignoreCase = true)
            }
            categoryMatch && dateMatch && unwantedMatch
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            FlowRow(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("Sve", "Politika", "Sport", "Nauka/tehnologija", "Ljepota i zdravlje")
                categories.forEach { category ->
                    FilterChip(
                        selected = localSelectedCategory == category,
                        onClick = {
                            localSelectedCategory = category
                            advancedFilterApplied = false
                            startDateMillis = null
                            endDateMillis = null
                            unwantedWords = emptyList()
                        },
                        label = { Text(category) },
                        modifier = Modifier.testTag(
                            when (category) {
                                "Sve" -> "filter_chip_all"
                                "Politika" -> "filter_chip_pol"
                                "Sport" -> "filter_chip_spo"
                                "Nauka/tehnologija" -> "filter_chip_sci"
                                "Ljepota i zdravlje" -> "filter_chip_none"
                                else -> ""
                            }
                        )
                    )
                }
                FilterChip(
                    selected = false,
                    onClick = {
                        currentBackStackEntry?.savedStateHandle?.set(
                            "newsfeed_selected_category",
                            localSelectedCategory
                        )
                        navController.navigate("filters")
                    },
                    label = { Text("Više filtera ...") },
                    modifier = Modifier.testTag("filter_chip_more")
                )
            }
            if (filteredNews.isEmpty()) {
                val message = if (advancedFilterApplied) {
                    "Nema pronađenih vijesti za odabrane filtere."
                } else if (localSelectedCategory != "Sve") {
                    "Nema pronađenih vijesti u kategoriji \"$localSelectedCategory\"."
                } else {
                    "Nema dostupnih vijesti."
                }
                MessageCard(message)
            } else {
                NewsList(newsItems = filteredNews) { newsId ->
                    navController.navigate("details/$newsId")
                }
            }
        }
    }
}