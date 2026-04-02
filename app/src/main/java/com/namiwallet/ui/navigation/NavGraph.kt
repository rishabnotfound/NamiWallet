package com.namiwallet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.namiwallet.ui.screens.onboarding.CreateWalletScreen
import com.namiwallet.ui.screens.onboarding.ImportWalletScreen
import com.namiwallet.ui.screens.onboarding.VerifyMnemonicScreen
import com.namiwallet.ui.screens.onboarding.WelcomeScreen
import com.namiwallet.ui.screens.receive.ReceiveScreen
import com.namiwallet.ui.screens.send.SendScreen
import com.namiwallet.ui.screens.settings.SettingsScreen
import com.namiwallet.ui.screens.wallet.HomeScreen
import com.namiwallet.ui.screens.wallet.TransactionDetailScreen

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object CreateWallet : Screen("create_wallet")
    object ImportWallet : Screen("import_wallet")
    object VerifyMnemonic : Screen("verify_mnemonic/{mnemonic}") {
        fun createRoute(mnemonic: String): String = "verify_mnemonic/$mnemonic"
    }
    object Home : Screen("home")
    object Send : Screen("send?chain={chain}") {
        fun createRoute(chain: String? = null): String = if (chain != null) "send?chain=$chain" else "send"
    }
    object Receive : Screen("receive?chain={chain}") {
        fun createRoute(chain: String? = null): String = if (chain != null) "receive?chain=$chain" else "receive"
    }
    object TransactionDetail : Screen("transaction/{txHash}?chain={chain}") {
        fun createRoute(txHash: String, chain: String): String = "transaction/$txHash?chain=$chain"
    }
    object Settings : Screen("settings")
}

@Composable
fun NamiNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding Flow
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onCreateWallet = { navController.navigate(Screen.CreateWallet.route) },
                onImportWallet = { navController.navigate(Screen.ImportWallet.route) }
            )
        }

        composable(Screen.CreateWallet.route) {
            CreateWalletScreen(
                onMnemonicGenerated = { mnemonic ->
                    navController.navigate(Screen.VerifyMnemonic.createRoute(mnemonic))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ImportWallet.route) {
            ImportWalletScreen(
                onWalletImported = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.VerifyMnemonic.route,
            arguments = listOf(navArgument("mnemonic") { type = NavType.StringType })
        ) { backStackEntry ->
            val mnemonic = backStackEntry.arguments?.getString("mnemonic") ?: ""
            VerifyMnemonicScreen(
                mnemonic = mnemonic,
                onVerified = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Main Wallet Flow
        composable(Screen.Home.route) {
            HomeScreen(
                onSendClick = { chain -> navController.navigate(Screen.Send.createRoute(chain)) },
                onReceiveClick = { chain -> navController.navigate(Screen.Receive.createRoute(chain)) },
                onTransactionClick = { txHash, chain ->
                    navController.navigate(Screen.TransactionDetail.createRoute(txHash, chain))
                },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            route = Screen.Send.route,
            arguments = listOf(
                navArgument("chain") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val chain = backStackEntry.arguments?.getString("chain")
            SendScreen(
                initialChain = chain,
                onTransactionSent = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Receive.route,
            arguments = listOf(
                navArgument("chain") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val chain = backStackEntry.arguments?.getString("chain")
            ReceiveScreen(
                initialChain = chain,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(
                navArgument("txHash") { type = NavType.StringType },
                navArgument("chain") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val txHash = backStackEntry.arguments?.getString("txHash") ?: ""
            val chain = backStackEntry.arguments?.getString("chain") ?: ""
            TransactionDetailScreen(
                txHash = txHash,
                chain = chain,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
