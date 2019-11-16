package com.github.adrianhall.weather.services

import android.content.Context
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.adrianhall.weather.R
import com.github.adrianhall.weather.models.UserLocation
import com.github.adrianhall.weather.models.Weather
import okhttp3.*
import timber.log.Timber
import java.io.IOException
import java.lang.RuntimeException

/**
 * Implementation of the Weather Forecast API by Dark Sky (https://darksky.net/dev/docs)
 */
class WeatherService(context: Context) {
    companion object {
        // The base URI for the Dark Sky API
        private const val BASE_URI = "https://api.darksky.net/forecast"

        // The exclusions if we just want the current weather
        private const val CURRENT_WEATHER_EXCLUDE="minutely,hourly,daily,flags"

        // The exclusions if we want a full forecast
        private const val FULL_WEATHER_EXCLUDE="flags"

        // The time to live for the current forecast
        private const val CURRENT_FORECAST_LIFETIME = 300 // 5 minutes

        // The time to live for the full forecast
        private const val FULL_FORECAST_LIFETIME = 300 // 5 minutes
    }

    private val apiKey = context.getString(R.string.dark_sky_api_key)
    private val httpClient = OkHttpClient()
    private val jsonMapper = ObjectMapper().registerKotlinModule().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    // Cache information
    private val cachedCurrentWeather = HashMap<String,CachedWeather>()
    private val cachedFullWeather = HashMap<String,CachedWeather>()

    /**
     * Get the current forecast only for a location.  If the current forecast has been received
     * since the CURRENT_FORECAST_LIFETIME, then just return the previous value.
     */
    fun getCurrentForecast(location: UserLocation, callback: (Weather?, Exception?) -> Unit) {
        val uri = getUri(location.latitude, location.longitude, CURRENT_WEATHER_EXCLUDE)
        val currentTime = System.currentTimeMillis()

        // Consult the cache - if it's present, then use the cache
        cachedCurrentWeather[uri]?.run {
            if (receivedTime + CURRENT_FORECAST_LIFETIME < currentTime) {
                callback.invoke(weather, null)
                return
            }
        }

        fetchWeather(uri) { weather, exception ->
            if (exception == null && weather != null) {
                cachedCurrentWeather[uri] = CachedWeather(currentTime, weather)
            }
            callback.invoke(weather, exception)
        }
    }

    /**
     * Get the full forecast only for a location.  If the current forecast has been received
     * since the FULL_FORECAST_LIFETIME, then just return the previous value.
     */
    fun getFullForecast(location: UserLocation, callback: (Weather?, Exception?) -> Unit) {
        val uri = getUri(location.latitude, location.longitude, FULL_WEATHER_EXCLUDE)
        val currentTime = System.currentTimeMillis()

        // Consult the cache - if it's present, then use the cache
        cachedFullWeather[uri]?.run {
            if (receivedTime + FULL_FORECAST_LIFETIME < currentTime) {
                callback.invoke(weather, null)
                return
            }
        }

        fetchWeather(uri) { weather, exception ->
            if (exception == null && weather != null) {
                cachedFullWeather[uri] = CachedWeather(currentTime, weather)
            }
            callback.invoke(weather, exception)
        }
    }

    private fun fetchWeather(uri: String, callback: (Weather?, Exception?) -> Unit) {
        Timber.d("Fetching Weather @ $uri")
        val request = Request.Builder().url(uri).build()
        httpClient.newCall(request).enqueue(object : Callback {
            /**
             * Called when the request could not be executed due to cancellation, a connectivity problem or
             * timeout. Because networks can fail during an exchange, it is possible that the remote server
             * accepted the request before the failure.
             */
            override fun onFailure(call: Call, e: IOException) {
                Timber.e(e, "HTTP Request Failure")
                callback.invoke(null, e)
            }

            /**
             * Called when the HTTP response was successfully returned by the remote server. The callback may
             * proceed to read the response body with [Response.body]. The response is still live until its
             * response body is [closed][ResponseBody]. The recipient of the callback may consume the response
             * body on another thread.
             *
             * Note that transport-layer success (receiving a HTTP response code, headers and body) does not
             * necessarily indicate application-layer success: `response` may still indicate an unhappy HTTP
             * response code like 404 or 500.
             */
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Timber.e("Response status code = ${response.code} - failed")
                    callback.invoke(null, RuntimeException("Weather is not available (R_UNSUCCESSFUL)"))
                } else {
                    val jsonStr = response.body?.string()
                    if (jsonStr == null) {
                        Timber.e("Response: status code = ${response.code} - no body")
                        callback.invoke(null, RuntimeException("Weather is not available (R_NODATA)"))
                    } else {
                        try {
                            val weather = jsonMapper.readValue<Weather>(jsonStr)
                            callback.invoke(weather, null)
                        } catch (ex: Exception) {
                            Timber.e(ex,"Error decoding response")
                            callback.invoke(null, ex)
                        }
                    }
                }
            }
        })
    }

    /**
     * Returns the actual URI that we need to do a GET on to retrieve the weather report
     */
    private fun getUri(latitude: Double, longitude: Double, exclude: String="minutely,flags"): String
            = "%s/%s/%.5f,%.5f?exclude=%s&lang=en&units=si".format(BASE_URI, apiKey, latitude, longitude, exclude)

    // Cache entry class
    data class CachedWeather(val receivedTime: Long, val weather: Weather)
}