package com.plcoding.personalfinancecompanion.UserInterface.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plcoding.personalfinancecompanion.Domain.Model.TransactionType
import com.plcoding.personalfinancecompanion.presentation.FinanceUiState
import java.util.Locale
import kotlin.math.max

@Composable
fun InsightsScreen(
    padding: PaddingValues,
    uiState: FinanceUiState
) {
    if (uiState.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                text = "Insights",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState.errorMessage != null) {
            item {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        if (uiState.transactions.isEmpty()) {
            item {
                EmptyInsightCard("No transactions yet", "Add transactions to unlock personalized insights.")
            }
        } else if (!uiState.hasEnoughInsightData) {
            item {
                EmptyInsightCard(
                    "More data needed",
                    "Add at least a few transactions across different days to unlock spending insights."
                )
            }
        }
        else {
            item {
                InsightCard(
                    title = "Highest Spending Category",
                    body = "${uiState.highestSpendingCategory ?: "N/A"} • ${formatAmount(uiState.highestSpendingCategoryAmount)}"
                )
            }

            item {
                WeeklyComparisonCard(
                    thisWeek = uiState.thisWeekExpense,
                    lastWeek = uiState.lastWeekExpense
                )
            }

            item {
                InsightCard(
                    title = "Most Frequent Activity",
                    body = "Category: ${uiState.frequentCategory ?: "N/A"}\nType: ${uiState.frequentTransactionType?.displayName() ?: "N/A"}"
                )
            }
        }
    }
}

@Composable
private fun EmptyInsightCard(title: String, body: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InsightCard(title: String, body: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun WeeklyComparisonCard(thisWeek: Double, lastWeek: Double) {
    val maxValue = max(thisWeek, lastWeek).takeIf { it > 0.0 } ?: 1.0
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("This Week vs Last Week", style = MaterialTheme.typography.titleMedium)
            ComparisonRow(label = "This week", value = thisWeek, maxValue = maxValue, color = MaterialTheme.colorScheme.primary)
            ComparisonRow(label = "Last week", value = lastWeek, maxValue = maxValue, color = MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
private fun ComparisonRow(label: String, value: Double, maxValue: Double, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(formatAmount(value), style = MaterialTheme.typography.bodyMedium)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth((value / maxValue).toFloat().coerceIn(0.05f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            ) {}
        }
    }
}

private fun formatAmount(value: Double): String = "$" + String.format(Locale.getDefault(), "%,.2f", value)

private fun TransactionType.displayName(): String =
    when (this) {
        TransactionType.INCOME -> "Income"
        TransactionType.EXPENSE -> "Expense"
    }
