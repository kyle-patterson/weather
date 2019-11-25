package com.github.adrianhall.weather

import android.app.Application
import com.facebook.FacebookSdk
import com.github.adrianhall.weather.auth.AuthenticationRepository
import com.github.adrianhall.weather.auth.FacebookLoginManager
import com.github.adrianhall.weather.repositories.FavoritesRepository
import com.github.adrianhall.weather.services.FilePreferencesService
import com.github.adrianhall.weather.services.StorageService
import com.github.adrianhall.weather.services.WeatherService
import com.github.adrianhall.weather.ui.DetailsViewModel
import com.github.adrianhall.weather.ui.FavoritesViewModel
import com.github.adrianhall.weather.ui.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber
import com.azure.data.*
import com.azure.data.model.PermissionMode
import com.github.adrianhall.weather.models.FavoriteCity
import java.io.Console

@Suppress("unused")
class ApplicationWrapper : Application() {
    companion object {
        val services = module {
            single { FacebookLoginManager() }
            single { WeatherService(get()) }
            single { FilePreferencesService(get()) as StorageService }
            single { AuthenticationRepository() }
            single { FavoritesRepository(get(), get()) }
        }

        val viewModels = module {
            viewModel { LoginViewModel(get())     }
            viewModel { FavoritesViewModel(get()) }
            viewModel { DetailsViewModel(get(), get())   }
        }
    }

    override fun onCreate() {
        val endpoint = "kylepaweather"
        val key = ""

        super.onCreate()

        AzureData.configure(applicationContext, endpoint, key, PermissionMode.All)


        // Logging initialization
        if (BuildConfig.DEBUG) {
            // Initialize console logging
            Timber.plant(Timber.DebugTree())
        }

        // Initialize the Facebook SDK
        FacebookSdk.fullyInitialize()

        // Initialize dependency injection
        startKoin {
            androidLogger()
            androidContext(this@ApplicationWrapper)
            modules(listOf(services, viewModels))
        }
    }
}
