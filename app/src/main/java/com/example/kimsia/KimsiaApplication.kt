package com.example.kimsia

import android.app.Application
import com.example.kimsia.di.AppContainer

class KimsiaApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
