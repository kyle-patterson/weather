package com.github.adrianhall.weather.services

import android.content.Context
import timber.log.Timber

/**
 * Explicit implementation of the storage service such that the data is stored in user
 * preferences.   This will only work for small amounts of data as the maximum size of
 * a user preference is 8K and that includes the JSON encoding.
 */
class UserPreferencesStorageService(context: Context): StorageService {
    companion object {
        private var PREFS_FILE = UserPreferencesStorageService::class.java.canonicalName + ".prefs"
    }
    private val preferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    /**
     * Loads the data from the specified key.
     *
     * @param key the key to restore the data from
     * @param onComplete completion handler
     */
    override fun load(key: String, onComplete: (String?, Exception?) -> Unit) {
        Timber.d("Loading JSON from key $key")
        val json = preferences.getString(key, null)
        onComplete.invoke(json, null)
    }

    /**
     * Saves the data to the specified key
     *
     * @param key the key to save the data to
     * @param json the data to store in the key
     * @param onComplete completion handler
     */
    override fun save(key: String, json: String, onComplete: (Exception?) -> Unit) {
        Timber.d("Saving JSON to key $key")
        try {
            preferences.edit().putString(key, json).apply()
            onComplete.invoke(null)
        } catch (error: Exception) {
            onComplete.invoke(error)
        }
    }
}