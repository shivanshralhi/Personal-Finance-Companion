package com.plcoding.personalfinancecompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plcoding.personalfinancecompanion.UserInterface.navigation.FinanceApp
import com.plcoding.personalfinancecompanion.presentation.FinanceViewModel
import com.plcoding.personalfinancecompanion.UserInterface.navigation.FinanceApp
import com.plcoding.personalfinancecompanion.ui.theme.PersonalFinanceCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonalFinanceCompanionTheme {
                val financeViewModel: FinanceViewModel = viewModel()
                FinanceApp(financeViewModel)
            }
        }
    }
}

