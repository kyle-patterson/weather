package com.github.adrianhall.weather.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.adrianhall.weather.models.UserSettings
import com.github.adrianhall.weather.services.StorageService
import timber.log.Timber

/**
 * The repository for the user settings, allowing the user to load and save settings.
 */
class SettingsRepository(private val storageService: StorageService) {
    companion object {
        private const val SETTINGS_KEY = "settings"
    }

    private val mUserSettings: MutableLiveData<UserSettings> = MutableLiveData(UserSettings())

    // JSON Serializer / Deserializer
    private val mapper = ObjectMapper().registerKotlinModule().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    init {
        loadUserSettings()
    }

    /**
     * The user settings
     */
    val userSettings: LiveData<UserSettings> = mUserSettings

    /**
     * Loads the user settings object from the common store.  This is done asynchronously and
     * updates the userSettings observable when complete.
     */
    private fun loadUserSettings() {
        Timber.d("ENTER: loadUserSettings()")
        storageService.load(SETTINGS_KEY) { jsonStr, error ->
            Timber.d("loadUserSettings(): Callback returned")
            when {
                jsonStr != null -> {
                    Timber.d("loadUserSettings(): ... and we got some data!")
                    try {
                        val userSettings = mapper.readValue<UserSettings>(jsonStr)
                        mUserSettings.postValue(userSettings)
                    } catch (decodeError: Exception) {
                        Timber.e(decodeError, "Error during JSON deserialization")
                        mUserSettings.postValue(UserSettings())
                    }
                }
                error != null -> {
                    Timber.e(error, "Error during loading of data")
                    mUserSettings.postValue(UserSettings())
                }
                else -> {
                    Timber.d("No data received - initializing (probably)")
                    mUserSettings.postValue(UserSettings())
                }
            }
        }
    }

    /**
     * Saves the new user settings object to the common store.  This is done asynchronously to
     * avoid UI problems.
     */
    private fun saveUserSettings(userSettings: UserSettings) {
        Timber.d("ENTER: saveUserSettings()")
        try {
            val jsonStr = mapper.writeValueAsString(userSettings)
            storageService.save(SETTINGS_KEY, jsonStr) { error ->
                Timber.d("saveUserSettings(): Callback returned")
                if (error != null) {
                    Timber.e(error, "Error during saving of data")
                }
            }
        } catch (encodeError: Exception) {
            Timber.e(encodeError, "Error during JSON serialization")
        }
    }

    /**
     * Sets the user settings for the user.
     */
    fun updateUserSettings(userSettings: UserSettings) {
        Timber.d("ENTER: updateUserSettings()")
        Timber.d("--> posting new user settings to the observable")
        mUserSettings.postValue(userSettings)
        Timber.d("--> saving user settings to the backing store")
        saveUserSettings(userSettings)
    }
}