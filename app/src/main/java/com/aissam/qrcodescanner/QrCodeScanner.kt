package com.aissam.qrcodescanner

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QrCodeScanner:Application() {

    override fun onCreate() {
        super.onCreate()

    }
}