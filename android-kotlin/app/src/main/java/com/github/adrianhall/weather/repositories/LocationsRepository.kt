package com.github.adrianhall.weather.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.adrianhall.weather.models.UserLocation
import com.github.adrianhall.weather.services.StorageService
import timber.log.Timber

/**
 * Repository for the locations that the user wants to follow.  These are primarily displayed
 * within the main activity as a list of locations.
 */
class LocationsRepository(private val storageService: StorageService) {
    companion object {
        private const val FAVORITES_KEY = "favorites"
    }

    private val mFavoriteLocations: MutableLiveData<List<UserLocation>> = MutableLiveData(emptyList())

    // JSON Serializer / Deserializer
    private val mapper = ObjectMapper().registerKotlinModule().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    init {
        loadFavoriteLocations()
    }

    /**
     * The list of favorite locations
     */
    val favoriteLocations: LiveData<List<UserLocation>> = mFavoriteLocations

    /**
     * Loads the favorite locations object from the common store.  This is done asynchronously,
     * updating the list of favorite locations observable when complete.
     */
    private fun loadFavoriteLocations() {
        Timber.d("ENTER: loadFavoriteLocations()")
        storageService.load(FAVORITES_KEY) { jsonStr, error ->
            Timber.d("LoadFavoriteLocations: Callback returned")
            when {
                jsonStr != null -> {
                    Timber.d("LoadFavoriteLocations: ... and we got some data!")
                    try {
                        val locations = mapper.readValue<List<UserLocation>>(jsonStr)
                        mFavoriteLocations.postValue(locations)
                    } catch (decodeError: Exception) {
                        Timber.e(decodeError, "Error during JSON deserialization")
                        mFavoriteLocations.postValue(UserLocation.defaultLocations)
                    }
                }
                error != null -> {
                    Timber.e(error, "Error during loading of data")
                    mFavoriteLocations.postValue(UserLocation.defaultLocations)
                }
                else -> {
                    Timber.d("No data received - initializing (probably)")
                    mFavoriteLocations.postValue(UserLocation.defaultLocations)
                }
            }
        }
    }

    /**
     * Saves the new favorite locations object to the common store.  This is done asynchronously
     * to prevent UI problems.
     */
    private fun saveFavoriteLocations(locations: List<UserLocation>) {
        Timber.d("ENTER: saveFavoriteLocations()")
        try {
            val jsonStr = mapper.writeValueAsString(locations)
            storageService.save(FAVORITES_KEY, jsonStr) { error ->
                Timber.d("saveFavoriteLocations(): Callback returned")
                if (error != null) {
                    Timber.e(error, "Error during saving of data")
                }
            }
        } catch (encodeError: Exception) {
            Timber.e(encodeError, "Error during JSON serialization")
        }
    }

    /**
     * Adds or updates a new location to the list of favorite locations
     */
    fun upsertFavoriteLocation(location: UserLocation) {
        Timber.d("ENTER: upsertFavoriteLocation()")
        val oldList = mFavoriteLocations.value!!
        val newList = ArrayList<UserLocation>()
        if (oldList.contains(location)) {
            Timber.d("--> provided item is already in the list - replacing")
            newList.addAll(oldList.map { item -> if (item.id == location.id) location else item })
        } else {
            Timber.d("--> provided item is in the list already - adding")
            newList.addAll(oldList)
            newList.add(location)
        }

        Timber.d("--> posting new list of favorite locations to the observable")
        mFavoriteLocations.postValue(newList)
        Timber.d("--> saving list of favorite locations to the backing store")
        saveFavoriteLocations(newList)
    }

    /**
     * Removes an item from the list of favorite locations
     */
    fun removeFavoriteLocation(location: UserLocation) {
        Timber.d("ENTER: removeFavoriteLocation()")
        val oldList = mFavoriteLocations.value!!
        if (oldList.contains(location)) {
            Timber.d("--> provided item is in the list - removing")
            val newList = oldList.filter { item -> item.id != location.id }
            Timber.d("--> posting new list of favorite locations to the observable")
            mFavoriteLocations.postValue(newList)
            Timber.d("--> saving list of favorite locations to the backing store")
            saveFavoriteLocations(newList)
        } else {
            Timber.d("--> provided item is not in the list - silently ignoring")
        }
    }
}