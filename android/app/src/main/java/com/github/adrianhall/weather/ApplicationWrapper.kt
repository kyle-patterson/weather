package com.github.adrianhall.weather

import android.app.Application
import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
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
import com.fasterxml.jackson.databind.DeserializationFeature
import com.github.adrianhall.weather.models.Weather
import okhttp3.*
import java.io.IOException
import android.os.StrictMode
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



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

        private var instance: ApplicationWrapper? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val endpoint = "kylepaweather"
        val key = ""

        instance = this

        super.onCreate()

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
