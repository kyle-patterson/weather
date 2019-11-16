package com.github.adrianhall.weather.ui.main

import android.annotation.SuppressLint
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.adrianhall.weather.R
import com.github.adrianhall.weather.models.UserLocation
import com.github.adrianhall.weather.models.UserSettings
import com.github.adrianhall.weather.models.Weather
import com.github.adrianhall.weather.services.WeatherService
import com.github.adrianhall.weather.utils.SIConverter
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * The adapter for the locations list recycler-view.  This follows the normal pattern, with some
 * additions:
 *
 * 1) You can set a global click-on-row listener with setOnClickListener()
 * 2) You can reset the entire list of locations with setLocationList()
 */
class LocationsAdapter(private val weatherService: WeatherService): RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {
    private val locations = ArrayList<UserLocation>()
    private var onClickListener: ((UserLocation?, Int) -> Unit)? = null
    private var userSettings = UserSettings()

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.  This new ViewHolder should be constructed with a new View that can represent
     * the items of the given type. You can either create a new View manually or inflate it from
     * an XML layout file.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.locations_list_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = locations.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindLocation(locations[position], userSettings, weatherService)
        holder.setOnClickListener { item -> onClickListener?.invoke(item, position) }
    }

    /**
     * Updates the list of items within the list.
     */
    fun setLocationList(list: List<UserLocation>) {
        locations.clear()
        locations.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * Updates the user settings
     */
    fun setUserSettings(userSettings: UserSettings) {
        this.userSettings = userSettings
        notifyDataSetChanged()
    }

    /**
     * Sets the onClick listener that is called when a row is pressed.
     */
    fun setOnClickListener(onClickListener: (UserLocation?, Int) -> Unit) {
        this.onClickListener = onClickListener
    }

    /**
     * The view holder for each element of the list.
     */
    @SuppressLint("SetTextI18n")
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val handler = Handler(itemView.context.mainLooper)
        private var currentLocation: UserLocation? = null
        private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)

        // Widget locations
        private val conditionsImageView = itemView.findViewById<ImageView>(R.id.location_item_conditions)
        private val displayNameTextView = itemView.findViewById<TextView>(R.id.location_item_display_name)
        private val temperatureTextView = itemView.findViewById<TextView>(R.id.location_item_temperature)
        private val timestampTextView   = itemView.findViewById<TextView>(R.id.location_item_timestamp)
        private val loadingProgress     = itemView.findViewById<ProgressBar>(R.id.location_item_loading)

        /**
         * Binds the information within the provided location to the UI for the view
         */
        fun bindLocation(location: UserLocation, userSettings: UserSettings, weatherService: WeatherService) {
            currentLocation = location
            displayNameTextView.text = location.displayName
            weatherService.getCurrentForecast(location) { weather, exception ->
                if (exception != null) {
                    Timber.e(exception, "Error receiving weather")
                }
                handler.post { bindWeather(weather, userSettings) }
            }
        }

        /**
         * Once the weather arrives, bind the weather to the UI
         */
        fun bindWeather(weather: Weather?, userSettings: UserSettings) {
            loadingProgress.visibility = View.INVISIBLE
            weather?.currently?.run {
                getConditionDrawable(this.icon)?.run { conditionsImageView.setImageResource(this) }

                temperatureTextView.text = "%.0f°".format(SIConverter.temperature(this.temperature, userSettings))
                temperatureTextView.visibility = View.VISIBLE

                Timber.d("Time zone = ${weather.timezone}")
                timeFormatter.timeZone = TimeZone.getTimeZone(weather.timezone)
                Timber.d("Converted = ${timeFormatter.timeZone}")
                timestampTextView.text = timeFormatter.format(this.time)
                timestampTextView.visibility = View.VISIBLE
            }
        }

        /**
         * Converts the "icon" that is received from Dark Sky API into a background for the
         * whole image.
         */
        private fun getConditionDrawable(condition: String): Int? = when(condition) {
            "clear-day"             -> R.drawable.weather_clear_day
            "clear-night"           -> R.drawable.weather_clear_night
            "rain"                  -> R.drawable.weather_rain
            "snow"                  -> R.drawable.weather_snow
            "sleet"                 -> R.drawable.weather_sleet
            "wind"                  -> R.drawable.weather_windy
            "fog"                   -> R.drawable.weather_fog
            "cloudy"                -> R.drawable.weather_cloudy
            "partly-cloudy-day"     -> R.drawable.weather_partly_cloudy_day
            "partly-cloudy-night"   -> R.drawable.weather_partly_cloudy_night
            else                    -> null
        }

        /**
         * Sets the click handler for the entire row.
         */
        fun setOnClickListener(onClickListener: (location: UserLocation?) -> Unit) {
            itemView.setOnClickListener { onClickListener.invoke(currentLocation) }
        }
    }

}