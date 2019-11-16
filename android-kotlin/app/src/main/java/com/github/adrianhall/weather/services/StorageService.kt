package com.github.adrianhall.weather.services

/**
 * Interface that the system calls the load and save data.  Each data is stored as a string (so
 * encoding to JSON prior to calling the storage service is a good idea).
 */
interface StorageService {
    /**
     * Loads the data from the specified key.
     *
     * @param key the key to restore the data from
     * @param onComplete completion handler
     */
    fun load(key: String, onComplete: (String?, Exception?) -> Unit)

    /**
     * Saves the data to the specified key
     *
     * @param key the key to save the data to
     * @param json the data to store in the key
     * @param onComplete completion handler
     */
    fun save(key: String, json: String, onComplete: (Exception?) -> Unit)
}