package com.example.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.CraftingDetailBottomSheet
import com.example.ui.screens.CalculationsListScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.viewmodel.CraftingViewModel
import kotlinx.coroutines.flow.collectLatest

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object CalculationsList : Screen("calculations_list")
    data object Settings : Screen("settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppNavigation(
    viewModel: CraftingViewModel,
    navController: NavHostController = rememberNavController()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Listen to ViewModel events for snackbars
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(300)
                    )
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(300)
                    )
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(300)
                    )
                }
            ) {
                // Screen 1: Dashboard
                composable(route = Screen.Dashboard.route) {
                    DashboardScreen(
                        state = state,
                        onPremiumToggled = { viewModel.setPremium(it) },
                        onRrrSelected = { viewModel.setRrr(it) },
                        onRefreshClicked = { viewModel.refreshPrices(showUserFeedback = true) },
                        onCalculationClicked = { viewModel.openDetail(it) },
                        onNavigateToCalculations = {
                            navController.navigate(Screen.CalculationsList.route)
                        },
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }

                // Screen 2: Detailed Calculations List
                composable(route = Screen.CalculationsList.route) {
                    CalculationsListScreen(
                        state = state,
                        onSearchChanged = { viewModel.setSearchQuery(it) },
                        onFamilySelected = { viewModel.setFamilyFilter(it) },
                        onTierSelected = { viewModel.setTierFilter(it) },
                        onEnchantmentSelected = { viewModel.setEnchantmentFilter(it) },
                        onToggleOnlyProfitable = { viewModel.toggleOnlyProfitable() },
                        onSortSelected = { viewModel.setSortBy(it) },
                        onOpenCalculator = { viewModel.openDetail(it) },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Screen 3: Settings
                composable(route = Screen.Settings.route) {
                    SettingsScreen(
                        state = state,
                        onFamilyTrackedToggled = { family, isTracked ->
                            viewModel.setFamilyTracked(family, isTracked)
                        },
                        onClearCacheClicked = { viewModel.clearAllCache() },
                        onRefreshClicked = { viewModel.refreshPrices(showUserFeedback = true) },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            // Interactive Simulator Modal Sheet (Available globally on click)
            state.activeDetailCalculation?.let { activeCalc ->
                CraftingDetailBottomSheet(
                    calculation = activeCalc,
                    onDismiss = { viewModel.closeDetail() },
                    onSaveOverride = { override ->
                        viewModel.savePriceOverride(override)
                    },
                    onResetOverride = { itemId ->
                        viewModel.resetPriceOverride(itemId)
                    }
                )
            }
        }
    }
}
