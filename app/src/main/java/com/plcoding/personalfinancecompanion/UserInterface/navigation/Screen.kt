package com.plcoding.personalfinancecompanion.UserInterface.navigation

sealed class Screen(val route: String, val title: String) {
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object Transactions : Screen("transactions", "Transactions")
    data object Insights : Screen("insights", "Insights")
    data object Goals : Screen("goals", "Goals")
}