package com.example.sobesai

import android.app.Application
import com.example.sobesai.data.local.initDataStoreContext
import com.example.sobesai.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initDataStoreContext(this)
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}