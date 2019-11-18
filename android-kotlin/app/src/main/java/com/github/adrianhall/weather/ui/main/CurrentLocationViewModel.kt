package com.github.adrianhall.weather.ui.main

import androidx.lifecycle.ViewModel
import com.github.adrianhall.weather.repositories.SettingsRepository

class CurrentLocationViewModel(settingsRepository: SettingsRepository): ViewModel() {
    /**
     * Observable for the user settings
     */
    val userSettings = settingsRepository.userSettings
}