package com.ElOuedUniv.maktaba.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ElOuedUniv.maktaba.ui.components.MaktabaBottomBar
import com.ElOuedUniv.maktaba.ui.screens.*
import com.ElOuedUniv.maktaba.ui.theme.MaktabaColors

// ── Routes ────────────────────────────────────────────────────────

sealed class Screen(val route: String) {
    data object BookList   : Screen("book_list")
    data object Favorites  : Screen("favorites")
    data object Statistics : Screen("statistics")
    data object AddBook    : Screen("add_book")

    data object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: Long) = "book_detail/$bookId"
    }
    data object EditBook : Screen("edit_book/{bookId}") {
        fun createRoute(bookId: Long) = "edit_book/$bookId"
    }
}

// Screens that show the bottom bar
private val bottomBarScreens = setOf(
    Screen.BookList.route,
    Screen.Favorites.route,
    Screen.Statistics.route,
)

// ── Nav Graph ─────────────────────────────────────────────────────

@Composable
fun MaktabaNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = MaktabaColors.Bg,
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                MaktabaBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(Screen.BookList.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaktabaColors.Bg)
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.BookList.route
            ) {
                composable(Screen.BookList.route) {
                    BookListScreen(
                        onBookClick  = { id -> navController.navigate(Screen.BookDetail.createRoute(id)) },
                        onAddClick   = { navController.navigate(Screen.AddBook.route) },
                        onFavsClick  = { navController.navigate(Screen.Favorites.route) },
                        onStatsClick = { navController.navigate(Screen.Statistics.route) }
                    )
                }

                composable(
                    route = Screen.BookDetail.route,
                    arguments = listOf(navArgument("bookId") { type = NavType.LongType })
                ) { back ->
                    val id = back.arguments!!.getLong("bookId")
                    BookDetailScreen(
                        bookId = id,
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(Screen.EditBook.createRoute(id)) }
                    )
                }

                composable(Screen.AddBook.route) {
                    AddEditBookScreen(
                        onSaved  = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.EditBook.route,
                    arguments = listOf(navArgument("bookId") { type = NavType.LongType })
                ) { back ->
                    val id = back.arguments!!.getLong("bookId")
                    AddEditBookScreen(
                        bookId   = id,
                        onSaved  = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() }
                    )
                }

                composable(Screen.Favorites.route) {
                    FavoritesScreen(
                        onBookClick = { id -> navController.navigate(Screen.BookDetail.createRoute(id)) },
                        onBack      = { navController.popBackStack() }
                    )
                }

                composable(Screen.Statistics.route) {
                    StatisticsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
