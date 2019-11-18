package com.github.adrianhall.weather.ui.main


import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.github.adrianhall.weather.R
import com.github.adrianhall.weather.models.UserLocation
import com.github.adrianhall.weather.models.UserSettings
import com.github.adrianhall.weather.models.Weather
import com.github.adrianhall.weather.services.WeatherService
import com.github.adrianhall.weather.utils.SIConverter
import com.github.adrianhall.weather.utils.WeatherConditions
import kotlinx.android.synthetic.main.fragment_current_location.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

/**
 * Fragment that displays the current location, with a click-handler to bring up the detailed
 * weather forecast for the current location.
 */
class CurrentLocationFragment : Fragment(), LocationListener {
    companion object {
        /**
         * Minimum amount of time between location updates (in seconds)
         */
        private const val MIN_TIME = 600L // 10 minutes

        /**
         * Minimum distance between location updates (in meters)
         */
        private const val MIN_DISTANCE = 1000.0f // 1km
    }

    private lateinit var locationManager: LocationManager
    private val geocoder = Geocoder(context, Locale.US)
    private val vm by viewModel<CurrentLocationViewModel>()
    private val weatherService by inject<WeatherService>()

    private lateinit var currentLocation: UserLocation

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_current_location, container, false)

        try {
            locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this)
        } catch (error: SecurityException) {
            Timber.e(error, "Registration for location updates failed")
        }

        return view
    }

    /**
     * Bind the weather to the UI
     */
    private fun bindWeather(location: UserLocation, weather: Weather) {
        val userSettings = vm.userSettings.value ?: UserSettings()

        current_location_display_name.text = location.displayName
        current_location_loading.visibility = View.INVISIBLE
        weather.currently?.run {
            WeatherConditions.getWeatherDrawable(this.icon)?.let { drawable ->
                current_location_conditions.setImageResource(drawable)
            }
            current_location_temperature.text = "%.0f°".format(SIConverter.temperature(this.temperature, userSettings))
            current_location_temperature.visibility = View.VISIBLE
        }

        // Bind the click handler
        view?.setOnClickListener { onLocationPressed(location) }
    }

    /**
     * Event handler, called when the current weather is pressed
     */
    private fun onLocationPressed(location: UserLocation) {
        Timber.d("CLICK: location")
        // TODO: Implement move-to-details functionality
    }

    /**
     * Called when the location has changed.
     * @param location The new location, as a Location object.
     */
    override fun onLocationChanged(location: Location?) {
        if (location == null) {
            Timber.d("Location update - but no location provided")
            return
        }
        Timber.d("Location Update: %.5f,%.5f".format(location.latitude, location.longitude))
        val places = geocoder.getFromLocation(location.latitude, location.longitude, 2)
        if (places.isEmpty()) {
            Timber.d("Location update - no geocoder results")
            return
        }
        val displayName =
            if (places[0].featureName != null) places[0].featureName
            else "%s, %s".format(places[0].locality, places[0].countryName)
        currentLocation = UserLocation(location.latitude, location.longitude, displayName)

        weatherService.getCurrentForecast(currentLocation) { weather, exception ->
            when {
                exception != null -> Timber.e(exception, "Error receiving current weather forecast")
                weather != null   -> bindWeather(currentLocation, weather)
                else              -> Timber.e("No weather and no error received")
            }
        }
    }

    /**
     * This callback will never be invoked (deprecated, but still has to be implemented)
     */
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     * update.
     */
    override fun onProviderEnabled(provider: String?) { /* Do nothing */ }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     * update.
     */
    override fun onProviderDisabled(provider: String?) { /* Do nothing */ }
}
