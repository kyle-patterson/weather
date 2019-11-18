package com.github.adrianhall.weather.utils

import com.github.adrianhall.weather.R

class WeatherConditions {
    companion object {
        fun getWeatherDrawable(icon: String): Int? = when(icon) {
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
    }
}