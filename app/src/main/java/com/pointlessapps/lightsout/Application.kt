package com.pointlessapps.lightsout

import android.app.Application
import com.pointlessapps.lightsout.repositories.RepositoryRooms
import com.pointlessapps.lightsout.viewModels.ViewModelMain
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class Application : Application() {

    private val moduleApp = module {
        single { AppDatabase.get(get()) }
        single { RepositoryRooms(get()) }
        viewModel { ViewModelMain(get()) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@Application)
            modules(moduleApp)
        }
    }
}