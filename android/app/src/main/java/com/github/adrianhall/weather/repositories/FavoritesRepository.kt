package com.github.adrianhall.weather.repositories

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.azure.data.AzureData
import com.azure.data.*
import com.azure.data.model.PermissionMode
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.adrianhall.weather.ApplicationWrapper
import com.github.adrianhall.weather.auth.AuthenticationRepository
import com.github.adrianhall.weather.models.FavoriteCity
import com.github.adrianhall.weather.models.FavoriteCityList
import com.github.adrianhall.weather.services.StorageService
import org.jetbrains.anko.doAsync
import java.net.*
import timber.log.Timber
import kotlin.concurrent.thread
import kotlinx.coroutines.*
import okhttp3.internal.wait


class FavoritesRepository(private val storageService: StorageService, private val authenticationRepository: AuthenticationRepository) {
    private val mapper = ObjectMapper().registerKotlinModule()
    private var mFavorites: MutableLiveData<List<FavoriteCity>> = MutableLiveData()
    var favoriteCities: LiveData<List<FavoriteCity>> = mFavorites

    init {
        loadCities()
    }

    /**
     * Load the list of favorite cities from the backing store, posting it to the favoriteCities
     * observable live data object.
     *
     * It's ok to do this in an async method since we are posting to the observable.
     */
    private fun loadCities() {
        Timber.d("BEGIN: Loading cities from backing store")
        val accessToken = authenticationRepository.user.value!!.accessToken

        ConfigureCosmos()

        AzureData.getDocuments("tempCollection","fakeDB", FavoriteCityList::class.java){ favorites ->
            var resources = favorites.resource
            if (resources != null && resources.count != 0) {
                var jsonStr = resources.items[0].cityListJson
                // Ignore things we don't want to understand.
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                val cities = mapper.readValue<List<FavoriteCity>>(jsonStr)
                mFavorites.postValue(cities)
            } else {
                // Fall-through for no data / errors
                /*val seattle = FavoriteCity(47.60357, -122.32945, "Seattle, USA")
                val london = FavoriteCity(51.509865, -0 - 0.118092, "London, UK")*/
                mFavorites.postValue(ArrayList<FavoriteCity>())
            }
        }

        Timber.d("END: Loading cities from backing store")
    }

    private fun ConfigureCosmos() {
        if (!AzureData.isConfigured) {

            var endpoint = "kylepaweather"
            var key = getResourceToken()

            AzureData.configure(ApplicationWrapper.applicationContext(), endpoint, key, PermissionMode.All)
        }
    }

    private fun getResourceToken() : String
    {
        val user_id = authenticationRepository.user.value!!.properties["user_id"]
        var uri = "https://tokenbroker-dev-as.azurewebsites.net/api/token/"+user_id

        return URL(uri).getText()
    }

    fun URL.getText(): String {
        return openConnection().run {
            this as HttpURLConnection
            inputStream.bufferedReader().readText()
        }
    }

    /**
     * Saves the list of favorite cities to the backing store.  no need to post it back to the
     * list, but you should ensure only one "save" operation is running at any given time.
     */
    private fun saveCities(cities: List<FavoriteCity>) {
        Timber.d("BEGIN: Saving cities to backing store")
        val accessToken = authenticationRepository.user.value!!.accessToken
        val jsonStr = mapper.writeValueAsString(cities)
        Timber.d("JSON = $jsonStr")

        val userid : String = authenticationRepository.user.value!!.properties["user_id"]!!
        var cityListObj = FavoriteCityList(id = userid, userid = userid, cityListJson = jsonStr )

        ConfigureCosmos()
        if(cities.isEmpty())
        {
            AzureData.deleteDocument(partitionKey = userid, collectionId = "tempCollection", databaseId = "fakeDB", documentId = userid){}
        }
        else
        {
            AzureData.createOrUpdateDocument(document = cityListObj, partitionKey = userid, collectionId = "tempCollection", databaseId = "fakeDB"){}
        }

        /*storageService.saveJson(jsonStr, accessToken) { error ->
            if (error != null) Timber.e(error)
        }*/
        Timber.d("END: Saving cities to backing store")
    }

    /**
     * Add an item to the favorites list
     */
    fun addCity(city: FavoriteCity) {
        Timber.d("AddCity: city = ${city.displayName}")
        // Check to see if the location already exists
        if (cityIsFavorite(city)) {
            Timber.d("City is already in the list")
            return
        }

        // Construct the new list
        val cityList = ArrayList<FavoriteCity>()
        favoriteCities.value?.let { cityList.addAll(it) }
        cityList.add(city)

        // Save to disk
        saveCities(cityList)

        // Post the new list to the observable
        mFavorites.postValue(cityList)
    }

    /**
     * Remove an item from the favorites list
     */
    fun removeCity(city: FavoriteCity) {
        Timber.d("RemoveCity: city = ${city.displayName}")
        // Check to see if the location exists
        if (!cityIsFavorite(city)) {
            Timber.d("City is not in the list...")
            return
        }

        // Construct the new list
        val cityList = ArrayList<FavoriteCity>()
        val filteredList = favoriteCities.value?.filter { fave -> fave.toLocationString() != city.toLocationString() }
        filteredList?.let { cityList.addAll(it) }

        // Save to disk
        saveCities(cityList)

        // Post the new list to the observable
        mFavorites.postValue(cityList)
    }

    /**
     * Returns true if the provided city is a favorite already
     */
    fun cityIsFavorite(city: FavoriteCity): Boolean {
        val f = favoriteCities.value?.filter { fave -> fave.toLocationString() == city.toLocationString() }
        return f?.isNotEmpty() ?: false
    }
}