package com.cosmic.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cosmic.ui.screens.splash.SplashScreen
import com.cosmic.ui.screens.home.HomeScreen
import com.cosmic.ui.screens.editor.EditorScreen
import com.cosmic.ui.screens.terminal.TerminalScreen
import com.cosmic.ui.screens.git.GitScreen
import com.cosmic.ui.screens.settings.SettingsScreen
import java.net.URLDecoder

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        // 🌌 SPLASH
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // 🏠 HOME
        composable(Routes.HOME) {
            HomeScreen(
                onOpenFile = { fileName ->
                    navController.navigate(Routes.editor(fileName))
                },
                onOpenTerminal = {
                    navController.navigate(Routes.TERMINAL)
                },
                onOpenGit = {
                    navController.navigate(Routes.GIT)
                },
                onOpenSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        // ✍️ EDITOR (FIXED + URL DECODED)
        composable(
            route = Routes.EDITOR,
            arguments = listOf(
                navArgument("filePath") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val encoded = backStackEntry.arguments?.getString("filePath") ?: ""
            val filePath = URLDecoder.decode(encoded, "UTF-8")

            EditorScreen(
                fileName = filePath,
                onBack = { navController.popBackStack() }
            )
        }

        // 💻 TERMINAL
        composable(Routes.TERMINAL) {
            TerminalScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 🔧 GIT
        composable(Routes.GIT) {
            GitScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ⚙️ SETTINGS
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
