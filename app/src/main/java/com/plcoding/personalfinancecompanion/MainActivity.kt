package com.plcoding.personalfinancecompanion


import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.plcoding.personalfinancecompanion.reminders.ReminderNotificationHelper
import com.plcoding.personalfinancecompanion.reminders.ReminderScheduler
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
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ReminderNotificationHelper.createChannel(this)
        requestNotificationPermissionIfNeeded()
        ReminderScheduler.scheduleDailyReminder(this)
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
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
}

}


