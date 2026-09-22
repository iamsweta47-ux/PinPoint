package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppThemeMode
import com.example.data.local.UserPreferencesRepository
import com.example.ui.details.HospitalDetailScreen
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.navigation.Screen
import com.example.ui.settings.SettingsScreen
import com.example.ui.static.AboutScreen
import com.example.ui.static.ContactScreen
import com.example.ui.static.PrivacyPolicyScreen
import com.example.ui.static.TermsOfUseScreen
import com.example.ui.theme.PincodeHospitalTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var preferencesRepository: UserPreferencesRepository
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferencesRepository = UserPreferencesRepository.getInstance(applicationContext)
        homeViewModel = ViewModelProvider(
            this,
            HomeViewModel.Factory(applicationContext)
        )[HomeViewModel::class.java]

        setContent {
            val currentTheme by preferencesRepository.themeMode.collectAsStateWithLifecycle(initialValue = AppThemeMode.SYSTEM)
            val coroutineScope = rememberCoroutineScope()

            val isDark = when (currentTheme) {
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            PincodeHospitalTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                currentTheme = currentTheme,
                                onToggleTheme = {
                                    val nextTheme = if (isDark) AppThemeMode.LIGHT else AppThemeMode.DARK
                                    coroutineScope.launch {
                                        preferencesRepository.setThemeMode(nextTheme)
                                    }
                                },
                                onHospitalClick = { hospital ->
                                    navController.navigate(Screen.HospitalDetail.createRoute(hospital.id))
                                },
                                onNavigateToSettings = {
                                    navController.navigate(Screen.Settings.route)
                                }
                            )
                        }

                        composable(Screen.HospitalDetail.route) { backStackEntry ->
                            val hospitalId = backStackEntry.arguments?.getString("hospitalId")
                            // Use the currently selected hospital from state
                            val hospital = homeUiState.selectedHospital

                            HospitalDetailScreen(
                                hospital = hospital,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                currentTheme = currentTheme,
                                onThemeSelect = { selectedTheme ->
                                    coroutineScope.launch {
                                        preferencesRepository.setThemeMode(selectedTheme)
                                    }
                                },
                                onClearHistory = {
                                    homeViewModel.clearAllHistory()
                                },
                                onNavigateToPrivacy = {
                                    navController.navigate(Screen.PrivacyPolicy.route)
                                },
                                onNavigateToTerms = {
                                    navController.navigate(Screen.TermsOfUse.route)
                                },
                                onNavigateToAbout = {
                                    navController.navigate(Screen.About.route)
                                },
                                onNavigateToContact = {
                                    navController.navigate(Screen.Contact.route)
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.PrivacyPolicy.route) {
                            PrivacyPolicyScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.TermsOfUse.route) {
                            TermsOfUseScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.About.route) {
                            AboutScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Contact.route) {
                            ContactScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

