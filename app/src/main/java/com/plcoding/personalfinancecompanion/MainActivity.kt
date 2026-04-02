package com.plcoding.personalfinancecompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plcoding.personalfinancecompanion.UserInterface.navigation.FinanceApp
import com.plcoding.personalfinancecompanion.presentation.FinanceViewModel
import com.plcoding.personalfinancecompanion.ui.theme.PersonalFinanceCompanionTheme
import com.plcoding.personalfinancecompanion.Data.Local.FinanceDatabase
import com.plcoding.personalfinancecompanion.Data.Repository.RoomFinanceRepository
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = RoomFinanceRepository(FinanceDatabase.getInstance(applicationContext).financeDao())
        setContent {
            PersonalFinanceCompanionTheme {
                val financeViewModel: FinanceViewModel = viewModel(
                    factory = FinanceViewModel.factory(repository)
                )
                FinanceApp(financeViewModel)
            }
        }
    }
}

