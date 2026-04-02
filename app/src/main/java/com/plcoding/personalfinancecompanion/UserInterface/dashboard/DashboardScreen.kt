package com.plcoding.personalfinancecompanion.UserInterface.dashboard


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import com.plcoding.personalfinancecompanion.presentation.FinanceUiState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plcoding.personalfinancecompanion.Domain.Model.TransactionType
import kotlin.math.max

@Composable
fun DashboardScreen(padding: PaddingValues,
                    uiState: FinanceUiState,
                    savingsGoal: Double = 5000.0) {
    val currentBalance = uiState.currentBalance
    val totalIncome = uiState.totalIncome
    val totalExpense = uiState.totalExpense

    val savingsProgress = if (savingsGoal > 0) {
        (currentBalance / savingsGoal).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }

    val categorySpending = uiState.transactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.category }
        .mapValues { (_, txs) -> txs.sumOf { it.amount } }
        .toList()
        .sortedByDescending { (_, amount) -> amount }

    val maxAmount = categorySpending.maxOfOrNull { it.second } ?: 0.0
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Overview",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Current Balance",
                amount = currentBalance,
                valueColor = if (currentBalance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Income",
                amount = totalIncome,
                valueColor = Color(0xFF2E7D32)
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Expense",
                amount = totalExpense,
                valueColor = Color(0xFFC62828)
            )
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Savings Goal", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${formatAmount(currentBalance)} / ${formatAmount(savingsGoal)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                LinearProgressIndicator(
                    progress = { savingsProgress },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Category Spending", style = MaterialTheme.typography.titleMedium)

                if (categorySpending.isEmpty()) {
                    Text(
                        text = "No expense data yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    categorySpending.forEach { (category, amount) ->
                        val widthFraction = if (maxAmount == 0.0) 0f else (amount / maxAmount).toFloat()
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(category, style = MaterialTheme.typography.bodyMedium)
                                Text(formatAmount(amount), style = MaterialTheme.typography.bodyMedium)
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
                                        .fillMaxWidth(max(0.05f, widthFraction))
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(MaterialTheme.colorScheme.primary)
                                ) {}
                            }
                        }
                    }
                }
            }
        }
    }

    }
@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: Double,
    valueColor: Color
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(
                text = formatAmount(amount),
                style = MaterialTheme.typography.titleMedium,
                color = valueColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun formatAmount(value: Double): String = "$" + String.format("%,.2f", value)

