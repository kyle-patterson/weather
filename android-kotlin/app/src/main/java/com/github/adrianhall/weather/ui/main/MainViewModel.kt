package com.github.adrianhall.weather.ui.main

import androidx.lifecycle.ViewModel
import com.github.adrianhall.weather.models.UserLocation
import com.github.adrianhall.weather.repositories.LocationsRepository
import com.github.adrianhall.weather.repositories.SettingsRepository

/**
 * View-Model for the MainActivity.
 */
class MainViewModel(private val locationsRepository: LocationsRepository, settingsRepository: SettingsRepository): ViewModel() {
    /**
     * Observable list of all the favorite locations
     */
    val favoriteLocations = locationsRepository.favoriteLocations

    /**
     * Observable for the user settings
     */
    val userSettings = settingsRepository.userSettings

    /**
     * Add a new location.  In this case, this is add/edit.
     */
    fun addFavoriteLocation(location: UserLocation)    = locationsRepository.upsertFavoriteLocation(location)

    /**
     * Update a new location.  If the location does not exist, create it.
     */
    fun updateFavoriteLocation(location: UserLocation) = locationsRepository.upsertFavoriteLocation(location)

    /**
     * Remove a location from the list.
     */
    fun removeFavoriteLocation(location: UserLocation) = locationsRepository.removeFavoriteLocation(location)
}