package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.R
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun StandardNewsCard(item: NewsItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)) {
        Row(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier.size(80.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.news),
                    contentDescription = "Standard image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Text(item.snippet, style = MaterialTheme.typography.bodyMedium)
                Row {
                    Text(item.source, style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item.publishedDate, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
