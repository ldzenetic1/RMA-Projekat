package etf.ri.rma.newsfeedapp.screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.SavedNewsRepository
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private fun parseDate(dateString: String): LocalDate? {
    return try {
        LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsFeedScreen(navController: NavController) {
    val context = LocalContext.current
    val newsDao = remember { NewsDAO.getInstance(context) }

    var newsItems by remember { mutableStateOf<List<NewsItem>>(emptyList())}
    var isLoading by remember { mutableStateOf(false)}
    var errorMessage by remember {mutableStateOf<String?>(null) }
    var selectedCategory by remember {mutableStateOf("Sve") }

    val coroutineScope = rememberCoroutineScope()

    val currentBackStackEntry = navController.currentBackStackEntry
    val savedStateHandle = currentBackStackEntry?.savedStateHandle
    var advancedFilterApplied by remember{ mutableStateOf(false) }
    var savedCategoryFromFilter by remember{ mutableStateOf("Sve") }
    var startDateMillis by remember {mutableStateOf<Long?>(null) }
    var endDateMillis by remember {mutableStateOf<Long?>(null) }
    var unwantedWords by remember {mutableStateOf<List<String>>(emptyList()) }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
    val loadNews: (String) -> Unit = {category ->
        isLoading = true
        errorMessage = null
        selectedCategory = category

        coroutineScope.launch {
            try {
                val fetchedNews = newsDao.getTopStoriesByCategory(category, isNetworkAvailable(context))

               val currentNewsInSelectedCategory = SavedNewsRepository.allNews()
                    .filter { it.category == category || category == "Sve" }
                    .toMutableList()

                val updatedNewsList = mutableListOf<NewsItem>()

                 val featuredFetchedNews = fetchedNews.filter { it.isFeatured && (category == "Sve" || it.category == category) }

                featuredFetchedNews.forEach { newFeaturedNews ->
                    val existingNewsIndex = currentNewsInSelectedCategory.indexOfFirst { it.uuid == newFeaturedNews.uuid }
                    if (existingNewsIndex != -1) {
                        val existingNews = currentNewsInSelectedCategory.removeAt(existingNewsIndex)
                        updatedNewsList.add(existingNews.copy(isFeatured = true))
                    } else {
                        updatedNewsList.add(newFeaturedNews.copy(isFeatured = true))
                    }
                }
                currentNewsInSelectedCategory.forEach { existingNews ->
                    if (updatedNewsList.none { it.uuid == existingNews.uuid }) {
                        updatedNewsList.add(existingNews.copy(isFeatured = false))
                    }
                }

                updatedNewsList.sortByDescending { it.isFeatured }

                newsItems = updatedNewsList
            } catch (e: Exception) {
                errorMessage = "Greška pri učitavanju vijesti: ${e.message}"
                e.printStackTrace()
                newsItems = SavedNewsRepository.getNewsWithCategory(category)
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        newsItems = SavedNewsRepository.allNews()
        loadNews(selectedCategory)
    }

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
            selectedCategory = savedCategoryFromFilter
            loadNews(savedCategoryFromFilter)
            loadNews(savedCategoryFromFilter)
            advancedFilterApplied = true
            savedStateHandle?.remove<String>("selectedCategory")
            savedStateHandle?.remove<Long?>("startDateMillis")
            savedStateHandle?.remove<Long?>("endDateMillis")
            savedStateHandle?.remove<ArrayList<String>>("unwantedWords")
        }
    }

    val filteredNews = remember(newsItems, selectedCategory, startDateMillis, endDateMillis, unwantedWords, advancedFilterApplied) {
        val categoryToFilter = selectedCategory
        val startDate = if (advancedFilterApplied) startDateMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()} else null
        val endDate = if (advancedFilterApplied) endDateMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()} else null
        val wordsToFilter = if (advancedFilterApplied) unwantedWords else emptyList()

        newsItems.filter { news->
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
                val categories = listOf("Sve", "Politika", "Sport", "Nauka", "Tehnologija", "Ljepota i zdravlje")
                categories.forEach {category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            loadNews(category)
                            advancedFilterApplied = false
                            startDateMillis = null
                            endDateMillis = null
                            unwantedWords = emptyList()
                        },
                        label = { Text(category)},
                        modifier = Modifier.testTag(
                            when (category) {
                                "Sve" -> "filter_chip_all"
                                "Politika" -> "filter_chip_pol"
                                "Sport" -> "filter_chip_spo"
                                "Nauka"-> "filter_chip-sci"
                                "Tehnologija" -> "filter_chip_tech"
                                "Ljepota i zdravlje" -> "filter_chip_none"
                                else -> ""
                            }
                        )
                    )
                }
                FilterChip(
                    selected = advancedFilterApplied,
                    onClick = {
                        currentBackStackEntry?.savedStateHandle?.set(
                            "newsfeed_selected_category",
                            selectedCategory
                        )
                        currentBackStackEntry?.savedStateHandle?.set("startDateMillis", startDateMillis)
                        currentBackStackEntry?.savedStateHandle?.set("endDateMillis", endDateMillis)
                        currentBackStackEntry?.savedStateHandle?.set("unwantedWords", ArrayList(unwantedWords))
                        navController.navigate("filters")
                    },
                    label = { Text("Više filtera ...") },
                    modifier = Modifier.testTag("filter_chip_more")
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                MessageCard(errorMessage!!)
            } else if (filteredNews.isEmpty()) {
                val message = if (advancedFilterApplied) {
                    "Nema pronađenih vijesti za odabrane filtere."
                } else if (selectedCategory != "Sve") {
                    "Nema pronađenih vijesti u kategoriji \"$selectedCategory\"."
                } else {
                    "Nema dostupnih vijesti."
                }
                MessageCard(message)
            } else {
                NewsList(newsItems = filteredNews){ newsId ->
                    navController.navigate("details/$newsId")
                }
            }
        }
    }
}