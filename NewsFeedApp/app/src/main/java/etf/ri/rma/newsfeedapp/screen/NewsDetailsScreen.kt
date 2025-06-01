package etf.ri.rma.newsfeedapp.screen


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.R
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.launch
import coil.compose.AsyncImage

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailsScreen(navController: NavController, newsId: String?) {

    val newsItem = remember(newsId) {
        newsId?.let { NewsDAO.getInstance().getAllStories().find{ item -> item.uuid == it } }
    }

    var imageTags by remember { mutableStateOf<ArrayList<String>>(arrayListOf())}
    var isLoadingTags by remember {mutableStateOf(false) }
    var imageTagsError by remember {mutableStateOf<String?>(null) }

    var similarNews by remember { mutableStateOf<List<NewsItem>>(emptyList())}
    var isLoadingSimilarNews by remember{ mutableStateOf(false) }
    var similarNewsError by remember {mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(newsId) {
        if (newsItem != null) {
            newsItem.imageUrl?.let { url->
                isLoadingTags = true
                imageTagsError = null
                coroutineScope.launch {
                    try {
                        val tags = ImagaDAO.getInstance().getTags(url)
                        imageTags = tags
                    } catch (e: Exception) {
                        imageTagsError = "Greška pri učitavanju tagova: ${e.message}"
                        e.printStackTrace()
                    } finally {
                        isLoadingTags = false
                    }
                }
            }
            isLoadingSimilarNews = true
            similarNewsError = null
            coroutineScope.launch {
                try {
                    val news = NewsDAO.getInstance().getSimilarStories(newsItem.uuid)
                    similarNews = news
                } catch (e: Exception) {
                    similarNewsError = "Greška pri učitavanju sličnih vijesti: ${e.message}"
                    e.printStackTrace()
                } finally {
                    isLoadingSimilarNews = false
                }
            }
        }
    }

    BackHandler(enabled = true) {
        navController.popBackStack("newsFeed", inclusive = false)
    }

    if (newsItem == null) {
        Scaffold(topBar = { TopAppBar(title = { Text("Greška") }) }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)){
                MessageCard("Vijest nije pronađena.")
            }
        }
        return
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalji vijesti") }) },
        bottomBar = {
            Button(
                onClick = { navController.popBackStack()},
                modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("details_close_button")
            ) { Text("Zatvori detalje") }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text(text = newsItem.title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.testTag("details_title"))}
            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }

            newsItem.imageUrl?.let { imageUrl->
                item {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Slika vijesti",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.news),
                        error = painterResource(id = R.drawable.news)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item { Text(text = newsItem.snippet, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.testTag("details_snippet"))}
            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }
            item {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Kategorija: ${newsItem.category}", modifier = Modifier.testTag("details_category"))
                    Text("Izvor: ${newsItem.source}", modifier = Modifier.testTag("details_source"))
                }
            }
            item { Text("Datum objave: ${newsItem.publishedDate}", modifier = Modifier.testTag("details_date"))}

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tagovi slike:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (isLoadingTags) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else if (imageTagsError != null) {
                    Text("Greška pri učitavanju tagova: ${imageTagsError}", color = MaterialTheme.colorScheme.error)
                } else if (imageTags.isEmpty()) {
                    Text("Nema dostupnih tagova za ovu sliku.")
                } else {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        imageTags.forEach{ tag ->
                            AssistChip(onClick = { /* Nothing */ }, label = { Text(tag) })
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Povezane vijesti iz iste kategorije:", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (isLoadingSimilarNews) {
                item { CircularProgressIndicator(modifier = Modifier.size(24.dp)) }
            } else if (similarNewsError != null) {
                item { Text("Greška pri učitavanju sličnih vijesti: ${similarNewsError}", color = MaterialTheme.colorScheme.error)}
            } else if (similarNews.isEmpty()) {
                item { Text("Nema povezanih vijesti.") }
            } else {
                items(similarNews.size) {index ->
                    val related = similarNews[index]
                    val testTag = "related_news_title_${index + 1}"
                    Text(
                        text = related.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable{ navController.navigate("details/${related.uuid}") }
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