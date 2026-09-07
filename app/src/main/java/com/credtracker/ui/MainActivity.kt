package com.credtracker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.credtracker.CredTrackerApp
import com.credtracker.data.model.BillStatement
import com.credtracker.data.model.CreditCard
import com.credtracker.ui.components.QuickPayModal
import com.credtracker.ui.screens.*
import com.credtracker.ui.theme.BgObsidian
import com.credtracker.ui.theme.CredTrackerTheme
import com.credtracker.worker.BillSyncWorker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CredTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgObsidian
                ) {
                    AppNavigation()
                }
            }
        }
    }

    private fun triggerManualSync() {
        val syncRequest = OneTimeWorkRequestBuilder<BillSyncWorker>().build()
        WorkManager.getInstance(this).enqueue(syncRequest)
    }

    @Composable
    private fun AppNavigation() {
        val navController = rememberNavController()
        val repository = CredTrackerApp.instance.repository

        val cards by repository.allCards.collectAsState(initial = emptyList())
        val pendingBills by repository.pendingBills.collectAsState(initial = emptyList())
        val totalOutstanding by repository.totalOutstandingDue.collectAsState(initial = 0.0)

        var selectedCardForDetail by remember { mutableStateOf<CreditCard?>(null) }
        var activePayModalBill by remember { mutableStateOf<Pair<BillStatement, CreditCard?>?>(null) }

        NavHost(navController = navController, startDestination = "dashboard") {
            composable("dashboard") {
                DashboardScreen(
                    cards = cards,
                    pendingBills = pendingBills,
                    totalOutstanding = totalOutstanding ?: 0.0,
                    onCardClick = { card ->
                        selectedCardForDetail = card
                        navController.navigate("card_detail")
                    },
                    onPayClick = { bill, card ->
                        activePayModalBill = Pair(bill, card)
                    },
                    onAddCardClick = {
                        navController.navigate("add_card")
                    },
                    onSettingsClick = {
                        navController.navigate("settings")
                    },
                    onPermissionsClick = {
                        navController.navigate("permissions")
                    },
                    onSyncClick = {
                        triggerManualSync()
                    }
                )
            }

            composable("card_detail") {
                val card = selectedCardForDetail
                if (card != null) {
                    val cardBills by repository.allBills.collectAsState(initial = emptyList())
                    val filteredBills = cardBills.filter { it.cardId == card.id }

                    CardDetailScreen(
                        card = card,
                        bills = filteredBills,
                        onBackClick = { navController.popBackStack() },
                        onPayClick = { bill ->
                            activePayModalBill = Pair(bill, card)
                        },
                        onDeleteCard = { toDelete ->
                            lifecycleScope.launch {
                                repository.deleteCard(toDelete)
                                navController.popBackStack()
                            }
                        }
                    )
                }
            }

            composable("add_card") {
                AddCardScreen(
                    onBackClick = { navController.popBackStack() },
                    onSaveCard = { newCard ->
                        lifecycleScope.launch {
                            repository.saveCard(newCard)
                        }
                    }
                )
            }

            composable("permissions") {
                PermissionSetupScreen(
                    onBackClick = { navController.popBackStack() },
                    onSyncTriggered = {
                        triggerManualSync()
                    }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() },
                    onForceSyncClick = {
                        triggerManualSync()
                    }
                )
            }
        }

        // Quick Pay Modal Bottom Sheet
        val currentPayTarget = activePayModalBill
        if (currentPayTarget != null) {
            QuickPayModal(
                bill = currentPayTarget.first,
                billerVpa = currentPayTarget.second?.upiBillerVpa,
                onDismiss = { activePayModalBill = null },
                onMarkAsPaid = { billToMark ->
                    lifecycleScope.launch {
                        repository.markBillAsPaid(billToMark.id)
                    }
                }
            )
        }
    }
}
