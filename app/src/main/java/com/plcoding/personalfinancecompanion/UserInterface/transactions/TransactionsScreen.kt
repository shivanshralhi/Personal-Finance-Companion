package com.plcoding.personalfinancecompanion.UserInterface.transactions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plcoding.personalfinancecompanion.Domain.Model.Transaction
import com.plcoding.personalfinancecompanion.Domain.Model.TransactionType
import com.plcoding.personalfinancecompanion.presentation.FinanceUiState
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.UUID
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment


@Composable
fun TransactionsScreen(
    padding: PaddingValues,
    uiState: FinanceUiState,
    onSearchChange: (String) -> Unit,
    onTypeFilterChange: (String) -> Unit,
    onAddTransaction: (Transaction) -> Unit,
    onDeleteTransaction: (String) -> Unit,
    onUpdateTransaction: (Transaction) -> Unit
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var notes by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf(TransactionType.EXPENSE) }
    var editingTransactionId by rememberSaveable { mutableStateOf<String?>(null) }

    val amountError = remember(amount) {
        when {
            amount.isBlank() -> "Amount is required"
            amount.toDoubleOrNull() == null -> "Enter a valid number"
            amount.toDoubleOrNull()?.let { it <= 0.0 } == true -> "Amount must be greater than 0"
            else -> null
        }
    }
    val categoryError = remember(category) {
        if (category.isBlank()) "Category is required" else null
    }
    val dateError = remember(date) {
        if (parseDateOrNull(date) == null) "Use YYYY-MM-DD format" else null
    }

    val isValidInput = amountError == null && categoryError == null && dateError == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp, vertical = 12.dp).imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)


    ) {

        Text("Transactions", style = MaterialTheme.typography.headlineSmall)

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }


        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Search by category or notes") },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Search transactions input" }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("ALL", "INCOME", "EXPENSE").forEach { filter ->
                Button(onClick = { onTypeFilterChange(filter) },
                    modifier = Modifier
                        .sizeIn(minHeight = 48.dp)
                        .semantics { contentDescription = "Filter $filter transactions" }) {
                    val selectedPrefix = if (uiState.selectedTypeFilter == filter) "✓ " else ""
                    Text("$selectedPrefix$filter")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    supportingText = { if (amountError != null) Text(amountError) },
                    isError = amountError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    supportingText = { if (categoryError != null) Text(categoryError) },
                    isError = categoryError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    supportingText = { if (dateError != null) Text(dateError) },
                    isError = dateError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { selectedType = TransactionType.INCOME },
                        modifier = Modifier.sizeIn(minHeight = 48.dp)
                    ) {
                        Text("Income")
                    }
                    Button(
                        onClick = { selectedType = TransactionType.EXPENSE },
                        modifier = Modifier.sizeIn(minHeight = 48.dp)
                    ) {
                        Text("Expense")
                    }
                }
                Text("Selected: ${selectedType.name}")

                Button(
                    onClick = {
                        val tx = Transaction(
                            id = editingTransactionId ?: UUID.randomUUID().toString(),
                            amount = amount.toDouble(),
                            type = selectedType,
                            category = category.trim(),
                            date = parseDateOrNull(date) ?: LocalDate.now(),
                            notes = notes.trim()
                        )

                        if (editingTransactionId == null) {
                            onAddTransaction(tx)
                        } else {
                            onUpdateTransaction(tx)
                        }

                        amount = ""
                        category = ""
                        date = LocalDate.now().toString()
                        notes = ""
                        selectedType = TransactionType.EXPENSE
                        editingTransactionId = null
                    },
                    enabled = isValidInput,
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 48.dp)
                        .semantics { contentDescription = "Save transaction" }
                ) {
                    Text(if (editingTransactionId == null) "Add Transaction" else "Update Transaction")
                }
            }
        }

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.semantics { contentDescription = "Loading transactions" })
            }

            uiState.visibleTransactions.isEmpty() -> {
                Text("No transactions yet. Add your first transaction above.")
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.visibleTransactions, key = { it.id }) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = { onDeleteTransaction(transaction.id) },
                            onEdit = {
                                editingTransactionId = transaction.id
                                amount = transaction.amount.toString()
                                category = transaction.category
                                date = transaction.date.toString()
                                notes = transaction.notes
                                selectedType = transaction.type
                            }
                        )
                    }

                }
            }
        }
    }
}
@Composable
private fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("${transaction.type.name}: ${transaction.amount}")
            Text("Category: ${transaction.category}")
            Text("Date: ${transaction.date}")
            if (transaction.notes.isNotBlank()) {
                Text("Notes: ${transaction.notes}")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEdit, modifier = Modifier.sizeIn(minHeight = 48.dp)) { Text("Edit") }
                Button(onClick = onDelete, modifier = Modifier.sizeIn(minHeight = 48.dp)) { Text("Delete") }
            }
        }
    }
}

private fun parseDateOrNull(input: String): LocalDate? =
    try {
        LocalDate.parse(input)
    } catch (_: DateTimeParseException) {
        null
    }
