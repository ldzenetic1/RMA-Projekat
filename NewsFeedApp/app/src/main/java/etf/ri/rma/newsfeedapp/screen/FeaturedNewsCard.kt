package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.model.NewsItem
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import etf.ri.rma.newsfeedapp.R

@Composable
fun FeaturedNewsCard(item: NewsItem) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.news),
                    contentDescription = "Featured image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.title, style = MaterialTheme.typography.titleLarge)
            Text(item.snippet, style = MaterialTheme.typography.bodyMedium)
            Row {
                Text(item.source, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.width(8.dp))
                Text(item.publishedDate, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
