package com.namiwallet

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NamiWalletApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize any app-level components here
    }
}
