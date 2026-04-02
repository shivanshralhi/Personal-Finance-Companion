package com.plcoding.personalfinancecompanion.UserInterface.navigation


import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.plcoding.personalfinancecompanion.UserInterface.dashboard.DashboardScreen
import com.plcoding.personalfinancecompanion.UserInterface.goals.GoalsScreen
import com.plcoding.personalfinancecompanion.UserInterface.insights.InsightsScreen
import com.plcoding.personalfinancecompanion.UserInterface.transactions.TransactionsScreen
import com.plcoding.personalfinancecompanion.presentation.FinanceViewModel

@Composable
fun FinanceApp(viewModel: FinanceViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tabs = listOf(
        Screen.Dashboard,
        Screen.Transactions,
        Screen.Insights,
        Screen.Goals
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                tabs.forEach { screen ->
                    val icon = when (screen) {
                        is Screen.Dashboard -> Icons.Default.Home
                        is Screen.Transactions -> Icons.Default.List
                        is Screen.Insights -> Icons.Default.BarChart
                        is Screen.Goals -> Icons.Default.Flag
                    }

                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    padding = padding,
                    uiState = uiState
                )
            }
            composable(Screen.Transactions.route) {
                TransactionsScreen(
                    padding = padding,
                    uiState = uiState,
                    onSearchChange = viewModel::updateSearchQuery,
                    onTypeFilterChange = viewModel::updateTypeFilter,
                    onAddTransaction = viewModel::addTransaction,
                    onDeleteTransaction = viewModel::deleteTransaction,
                    onUpdateTransaction = viewModel::updateTransaction
                )
            }
            composable(Screen.Insights.route) { InsightsScreen(padding) }
            composable(Screen.Goals.route) {
                GoalsScreen(
                    padding = padding,
                    uiState = uiState,
                    onSaveGoal = viewModel::upsertMonthlySavingsGoal,
                    onDeleteGoal = viewModel::clearSavingsGoal
                )
            }
        }
    }
}