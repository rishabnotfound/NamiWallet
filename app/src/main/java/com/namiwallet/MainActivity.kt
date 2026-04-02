package com.namiwallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.namiwallet.security.SecureStorage
import com.namiwallet.ui.navigation.NamiNavGraph
import com.namiwallet.ui.navigation.Screen
import com.namiwallet.ui.theme.NamiWalletTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var secureStorage: SecureStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by remember {
                mutableStateOf(
                    when (secureStorage.getTheme()) {
                        "dark" -> true
                        "light" -> false
                        else -> null // System default
                    }
                )
            }

            NamiWalletTheme(
                darkTheme = isDarkTheme ?: androidx.compose.foundation.isSystemInDarkTheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Determine start destination based on wallet state
                    val startDestination = if (secureStorage.isWalletSetupComplete()) {
                        Screen.Home.route
                    } else {
                        Screen.Welcome.route
                    }

                    NamiNavGraph(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
