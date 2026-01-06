package com.example.bookexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bookexplorer.data.local.FavoritesDataStore
import com.example.bookexplorer.data.repository.BookRepository
import com.example.bookexplorer.ui.screens.BookDetailScreen
import com.example.bookexplorer.ui.screens.FavoritesScreen
import com.example.bookexplorer.ui.screens.HomeScreen
import com.example.bookexplorer.ui.theme.BookExplorerTheme
import com.example.bookexplorer.ui.viewmodel.DetailViewModel
import com.example.bookexplorer.ui.viewmodel.FavoritesViewModel
import com.example.bookexplorer.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val favoritesDataStore = FavoritesDataStore(applicationContext)
        val repository = BookRepository(favoritesDataStore)

        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val isDarkTheme by mainViewModel.isDarkTheme.collectAsState()

            BookExplorerTheme(darkTheme = isDarkTheme) {
                BookApp(repository, mainViewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Favorites : Screen("favorites", "Favorites", Icons.Filled.Favorite)
    object Detail : Screen("detail/{workId}", "Detail") {
        fun createRoute(workId: String) = "detail/$workId"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookApp(repository: BookRepository, mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen = listOf(Screen.Home, Screen.Favorites, Screen.Detail).find { currentDestination?.route?.startsWith(it.route) == true }
    val isDarkTheme by mainViewModel.isDarkTheme.collectAsState()

    val viewModelFactory = remember {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
                    modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> FavoritesViewModel(repository) as T
                    modelClass.isAssignableFrom(DetailViewModel::class.java) -> DetailViewModel(repository) as T
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }

    val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
    val favoritesViewModel: FavoritesViewModel = viewModel(factory = viewModelFactory)
    val detailViewModel: DetailViewModel = viewModel(factory = viewModelFactory)

    Scaffold(
        topBar = {
             TopAppBar(
                title = { Text(currentScreen?.title ?: "Book Explorer") },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { mainViewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.ModeNight,
                            contentDescription = "Toggle Theme"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                listOf(Screen.Home, Screen.Favorites).forEach { screen ->
                    NavigationBarItem(
                        icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onBookClick = { workId, _ ->
                        navController.navigate(Screen.Detail.createRoute(workId))
                    }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = favoritesViewModel,
                    onBookClick = { workId, _ ->
                        navController.navigate(Screen.Detail.createRoute(workId))
                    }
                )
            }
            composable(Screen.Detail.route) { backStackEntry ->
                val workId = backStackEntry.arguments?.getString("workId") ?: return@composable
                BookDetailScreen(
                    workId = workId,
                    viewModel = detailViewModel
                )
            }
        }
    }
}
