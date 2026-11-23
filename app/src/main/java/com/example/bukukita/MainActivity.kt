package com.example.bukukita

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bukukita.ui.auth.LoginScreen
import com.example.bukukita.ui.auth.RegisterScreen
import com.example.bukukita.ui.chatbot.ChatbotScreen
import com.example.bukukita.ui.components.BukuKitaBottomBar
import com.example.bukukita.ui.detail.BookDetailScreen
import com.example.bukukita.ui.home.HomeScreen
import com.example.bukukita.ui.profile.EditProfileScreen
import com.example.bukukita.ui.profile.ProfileScreen
import com.example.bukukita.ui.search.SearchScreen
import com.example.bukukita.ui.theme.AppLanguage
import com.example.bukukita.ui.theme.BukuKitaMotion
import com.example.bukukita.ui.theme.BukuKitaTheme
import com.example.bukukita.ui.theme.LanguagePreferenceStore
import com.example.bukukita.ui.theme.ThemeMode
import com.example.bukukita.ui.theme.ThemePreferenceStore
import com.example.bukukita.ui.theme.withAppLanguage
import com.example.bukukita.viewmodel.AuthViewModel
import com.example.bukukita.viewmodel.AuthViewModelFactory
import com.example.bukukita.viewmodel.ChatbotViewModel
import com.example.bukukita.viewmodel.ChatbotViewModelFactory
import java.util.Locale

private val TopLevelRoutes = setOf("home", "search", "chatbot", "profile")

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val savedLanguage = LanguagePreferenceStore.readSavedLanguage(newBase)
        Locale.setDefault(savedLanguage.locale)
        super.attachBaseContext(newBase.withAppLanguage(savedLanguage))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = applicationContext as BukuKitaApplication
            val themeStore = remember(app) { ThemePreferenceStore(app) }
            val themeMode by themeStore.themeMode.collectAsState()
            val appLanguage by app.languagePreferenceStore.language.collectAsState()

            BukuKitaTheme(
                themeMode = themeMode,
                appLanguage = appLanguage
            ) {
                BukuKitaApp(
                    themeMode = themeMode,
                    onThemeModeChange = themeStore::setThemeMode,
                    appLanguage = appLanguage,
                    onLanguageChange = app.languagePreferenceStore::setLanguage
                )
            }
        }
    }
}

@Composable
fun BukuKitaApp(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    appLanguage: AppLanguage = AppLanguage.INDONESIAN,
    onLanguageChange: (AppLanguage) -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as BukuKitaApplication
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(application.authRepository)
    )
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    // Freeze initial startDestination so NavHost does not rebuild its graph mid-transition on login/logout
    val initialRoute = remember { if (isLoggedIn) "home" else "login" }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentBaseRoute = currentDestination?.route?.substringBefore("?")
    val showBottomBar = currentBaseRoute in TopLevelRoutes

    val navigateToTopLevel: (String) -> Unit = { route ->
        if (currentBaseRoute != route) {
            navController.navigate(route) {
                popUpTo("home") {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        // Individual screens own statusBars/navigationBars so colored hero headers bleed edge-to-edge cleanly
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                BukuKitaBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = navigateToTopLevel
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = initialRoute,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            enterTransition = {
                val targetBase = targetState.destination.route?.substringBefore("?")
                val initialBase = initialState.destination.route?.substringBefore("?")
                if (targetBase in TopLevelRoutes && initialBase in TopLevelRoutes) {
                    fadeIn(tween(BukuKitaMotion.ContentCrossfadeMs, easing = BukuKitaMotion.StandardEasing))
                } else {
                    fadeIn(tween(BukuKitaMotion.ScreenEnterMs, easing = BukuKitaMotion.StandardEasing)) +
                        slideInHorizontally(tween(BukuKitaMotion.ScreenEnterMs, easing = BukuKitaMotion.StandardEasing)) { it / 18 }
                }
            },
            exitTransition = {
                fadeOut(tween(BukuKitaMotion.ScreenExitMs, easing = BukuKitaMotion.ExitEasing))
            },
            popEnterTransition = {
                fadeIn(tween(BukuKitaMotion.ScreenEnterMs, easing = BukuKitaMotion.StandardEasing))
            },
            popExitTransition = {
                fadeOut(tween(BukuKitaMotion.ScreenExitMs, easing = BukuKitaMotion.ExitEasing)) +
                    slideOutHorizontally(tween(BukuKitaMotion.ScreenExitMs, easing = BukuKitaMotion.ExitEasing)) { it / 18 }
            }
        ) {
            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    currentLanguage = appLanguage,
                    onLanguageChange = onLanguageChange,
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
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }
            composable("home") {
                HomeScreen(
                    onNavigateToDetail = { bookId -> navController.navigate("detail/$bookId") },
                    onNavigateToSearch = { navigateToTopLevel("search") },
                    onNavigateToChatbot = { navigateToTopLevel("chatbot") }
                )
            }
            composable("detail/{bookId}") { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                BookDetailScreen(
                    bookId = bookId,
                    onNavigateBack = { navController.popBackStack() },
                    onAskAssistant = { question ->
                        navController.navigate("chatbot?prefill=${Uri.encode(question)}")
                    }
                )
            }
            composable("search") {
                SearchScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { bookId -> navController.navigate("detail/$bookId") }
                )
            }
            composable(
                route = "chatbot?prefill={prefill}",
                arguments = listOf(
                    navArgument("prefill") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val prefill = backStackEntry.arguments?.getString("prefill").orEmpty()
                val chatbotViewModel: ChatbotViewModel = viewModel(
                    factory = ChatbotViewModelFactory(
                        application.chatRepository,
                        application.authRepository,
                        application.languagePreferenceStore
                    )
                )
                ChatbotScreen(
                    onNavigateBack = { navController.popBackStack() },
                    prefillQuery = prefill,
                    viewModel = chatbotViewModel
                )
            }
            composable("profile") {
                ProfileScreen(
                    authViewModel = authViewModel,
                    currentThemeMode = themeMode,
                    onThemeModeChange = onThemeModeChange,
                    currentLanguage = appLanguage,
                    onLanguageChange = onLanguageChange,
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
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
