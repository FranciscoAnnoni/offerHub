package com.example.offerhub

import android.app.Application
import com.example.offerhub.contexts.ApplicationContextProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KelineApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationContextProvider.initialize(applicationContext)
    }
}