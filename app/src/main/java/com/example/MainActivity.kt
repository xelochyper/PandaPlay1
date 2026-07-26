package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.MainViewModel
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveryScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ModScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme

enum class BottomTab(val route: String, val title: String, val icon: ImageVector) {
    HOME("tab_home", "Beranda", Icons.Default.Home),
    MOD("tab_mod", "MOD", Icons.Default.DirectionsBus),
    LIVERY("tab_livery", "Livery", Icons.Default.Palette),
    PROFILE("tab_profile", "Profil", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                PandaPlayApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PandaPlayApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                isLoggedIn = isLoggedIn,
                onNavigateNext = {
                    if (isLoggedIn) {
                        navController.navigate("main") {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainContainerScreen(
                viewModel = viewModel,
                onItemClick = { itemId ->
                    navController.navigate("detail/$itemId")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "detail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: "m101"
            DetailScreen(
                itemId = itemId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainContainerScreen(
    viewModel: MainViewModel,
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color(0xFF0F172A),
                tonalElevation = 6.dp
            ) {
                BottomTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) Color(0xFF4F46E5) else Color(0xFF94A3B8)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF4F46E5) else Color(0xFF94A3B8)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFFEEF2FF)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FF))
        ) {
            when (selectedTab) {
                BottomTab.HOME -> HomeScreen(viewModel = viewModel, onItemClick = onItemClick)
                BottomTab.MOD -> ModScreen(viewModel = viewModel, onItemClick = onItemClick)
                BottomTab.LIVERY -> LiveryScreen(viewModel = viewModel, onItemClick = onItemClick)
                BottomTab.PROFILE -> ProfileScreen(viewModel = viewModel, onLogout = onLogout)
            }
        }
    }
}
