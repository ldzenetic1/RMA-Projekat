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
import androidx.compose.ui.graphics.Color
import etf.ri.rma.newsfeedapp.R
import coil.compose.AsyncImage

@Composable
fun FeaturedNewsCard(item: NewsItem, onClick: () -> Unit) {
    Card(onClick = onClick,
        colors = CardDefaults.cardColors(
        containerColor = Color(0xFFFFFACD)),
        modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()) {
                AsyncImage(
                    model = item.imageUrl ?: R.drawable.news, // Ako nema URL-a, koristi default sliku
                    contentDescription = "Featured image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.news), // Placeholder dok se slika učitava
                    error = painterResource(id = R.drawable.news) // Slika u slučaju greške
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
