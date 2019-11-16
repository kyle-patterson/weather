package com.github.adrianhall.weather

import android.app.Application
import com.github.adrianhall.weather.repositories.LocationsRepository
import com.github.adrianhall.weather.repositories.SettingsRepository
import com.github.adrianhall.weather.services.StorageService
import com.github.adrianhall.weather.services.UserPreferencesStorageService
import com.github.adrianhall.weather.services.WeatherService
import com.github.adrianhall.weather.ui.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import timber.log.Timber

/**
 * Base class for maintaining global application state.  This is instantiated before any
 * other class when the process is started.
 */
@Suppress("unused")
class ApplicationWrapper: Application() {
    companion object {
        val appModule = module {
            // Services
            single { WeatherService(get())                                  }
            single { UserPreferencesStorageService(get()) as StorageService }
            single { LocationsRepository(get())                             }
            single { SettingsRepository(get())                              }

            // view models
            viewModel { MainViewModel(get(), get()) }
        }
    }

    /**
     * Called when the application is starting, before any activity, service, or receiver objects
     * (excluding content providers) have been created.
     */
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@ApplicationWrapper)
            modules(listOf(appModule))
        }
    }
}