package etf.ri.rma.newsfeedapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import etf.ri.rma.newsfeedapp.screen.FilterScreen
import etf.ri.rma.newsfeedapp.screen.NewsDetailsScreen
import etf.ri.rma.newsfeedapp.screen.NewsFeedScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "newsFeed"
    ) {
        composable(route = "newsFeed") {
            NewsFeedScreen(navController = navController)
        }
        composable(route = "filters") {
            FilterScreen(navController = navController)
        }
        composable(
            route = "details/{newsId}",
            arguments = listOf(navArgument("newsId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId")
            NewsDetailsScreen(
                navController = navController,
                newsId = newsId
            )
        }
    }
}