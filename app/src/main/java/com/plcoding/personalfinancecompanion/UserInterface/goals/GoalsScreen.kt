package com.plcoding.personalfinancecompanion.UserInterface.goals


import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.unit.dp
import com.plcoding.personalfinancecompanion.presentation.FinanceUiState
import java.time.YearMonth
@Composable
fun GoalsScreen(
    padding: PaddingValues,
    uiState: FinanceUiState,
    onSaveGoal: (Double) -> Unit,
    onDeleteGoal: () -> Unit
) {
    var goalInput by rememberSaveable(uiState.savingsGoal?.targetAmount) {
        mutableStateOf(uiState.savingsGoal?.targetAmount?.toString().orEmpty())
    }
    val goalError = when {
        goalInput.isBlank() -> "Goal amount is required"
        goalInput.toDoubleOrNull() == null -> "Enter a valid number"
        goalInput.toDoubleOrNull()?.let { it <= 0.0 } == true -> "Goal amount must be greater than 0"
        else -> null
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Monthly Savings Goal", style = MaterialTheme.typography.headlineSmall)
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.semantics { contentDescription = "Loading savings goal" })
            return
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Set a target for ${YearMonth.now()} and track progress from this month's income and expenses.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = goalInput,
                    onValueChange = { goalInput = it },
                    label = { Text("Goal amount") },
                    supportingText = { if (goalError != null) Text(goalError) },
                    isError = goalError != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Savings goal amount input" }
                )

                val parsedGoal = goalInput.toDoubleOrNull()
                val canSave = goalError == null

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { parsedGoal?.let(onSaveGoal) },
                        enabled = canSave,
                        modifier = Modifier.sizeIn(minHeight = 48.dp)) {
                        Text(if (uiState.savingsGoal == null) "Save Goal" else "Update Goal")
                    }

                    if (uiState.savingsGoal != null) {
                        Button(onClick = onDeleteGoal,
                            modifier = Modifier.sizeIn(minHeight = 48.dp)) {
                            Text("Remove Goal")
                        }
                    }
                }
            }
        }

        if (uiState.savingsGoal != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Progress: ${uiState.savingsProgressPercent}%",
                        style = MaterialTheme.typography.titleMedium
                    )
                    LinearProgressIndicator(
                        progress = { uiState.savingsProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Savings goal progress indicator" }
                    )
                    Text(
                        text = "Saved this month: $${"%.2f".format(uiState.currentMonthSavings)} of $${"%.2f".format(uiState.savingsGoal.targetAmount)}"
                    )

                    if (uiState.hasReachedSavingsGoal) {
                        Text(
                            text = "🏅 Goal unlocked! You've reached your monthly savings target.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        val nextMilestone = uiState.nextSavingsMilestone
                        Text(
                            text = if (nextMilestone != null) {
                                "🎯 Next milestone: $nextMilestone%"
                            } else {
                                "Keep going — you're making progress."
                            }
                        )
                    }
                }
            }
        } else {
            Text("No monthly savings goal set yet.")
        }
    }
}