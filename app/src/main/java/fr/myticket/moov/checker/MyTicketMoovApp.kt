package fr.myticket.moov.checker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyTicketMoovApp:Application() {

    override fun onCreate() {
        super.onCreate()

    }
}