package com.example.bukukita

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bukukita.ui.chatbot.ChatbotScreen
import com.example.bukukita.ui.profile.ProfileScreen
import com.example.bukukita.ui.profile.EditProfileScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bukukita.ui.detail.BookDetailScreen
import com.example.bukukita.ui.home.HomeScreen
import com.example.bukukita.ui.search.SearchScreen
import com.example.bukukita.ui.theme.BukuKitaTheme
import com.example.bukukita.viewmodel.ChatbotViewModelFactory
import com.example.bukukita.viewmodel.AuthViewModel
import com.example.bukukita.viewmodel.AuthViewModelFactory
import com.example.bukukita.ui.auth.LoginScreen
import com.example.bukukita.ui.auth.RegisterScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BukuKitaTheme {
                BukuKitaApp()
            }
        }
    }
}

@Composable
fun BukuKitaApp() {
    val context = LocalContext.current
    val application = context.applicationContext as BukuKitaApplication
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(application.authRepository)
    )
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val currentRoute = currentDestination?.route
            
            // Tampilkan bottom bar hanya pada route utama
            val bottomBarRoutes = listOf("home", "search", "chatbot", "profile")
            if (bottomBarRoutes.contains(currentRoute)) {
                Surface(
                    modifier = Modifier.shadow(
                        elevation = 16.dp, 
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    val items = listOf(
                        BottomNavItem(stringResource(R.string.nav_home), "home", Icons.Default.Home),
                        BottomNavItem(stringResource(R.string.nav_search), "search", Icons.Default.Search),
                        BottomNavItem(stringResource(R.string.nav_chatbot), "chatbot", Icons.AutoMirrored.Filled.Chat),
                        BottomNavItem(stringResource(R.string.nav_profile), "profile", Icons.Default.Person)
                    )

                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        items.forEach { item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.title) },
                                label = {
                                    Text(
                                        item.title,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (currentDestination?.hierarchy?.any { it.route == item.route } == true) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                },
                                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = false
                                        }
                                        launchSingleTop = true
                                        restoreState = false
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "home" else "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable("home") {
                HomeScreen(
                    onNavigateToDetail = { bookId -> navController.navigate("detail/$bookId") },
                    onNavigateToSearch = { navController.navigate("search") },
                    onNavigateToChatbot = { navController.navigate("chatbot") }
                )
            }
            composable("detail/{bookId}") { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                BookDetailScreen(
                    bookId = bookId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("search") {
                SearchScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { bookId -> navController.navigate("detail/$bookId") }
                )
            }
            composable("chatbot") {
                val context = LocalContext.current
                val application = context.applicationContext as BukuKitaApplication
                val viewModel: com.example.bukukita.viewmodel.ChatbotViewModel = viewModel(
                    factory = ChatbotViewModelFactory(application.chatRepository)
                )
                ChatbotScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
            composable("profile") {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true } // Clear entire backstack
                        }
                    },
                    onNavigateToEditProfile = {
                        navController.navigate("edit_profile")
                    }
                )
            }
            composable("edit_profile") {
                EditProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}