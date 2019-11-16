package com.github.adrianhall.weather.utils

import com.github.adrianhall.weather.models.UserSettings

/**
 * Given a value with SI, convert to the appropriate value based on the user settings.
 */
class SIConverter {
    companion object {
        /**
         * SI Unit for Temperature is Celsius
         */
        fun temperature(v: Float, userSettings: UserSettings): Float =
            when (userSettings.temperatureUnit) {
                UserSettings.TemperatureUnit.CELSIUS -> v
                UserSettings.TemperatureUnit.FAHRENHEIT -> (v * 9 / 5) + 32.0f
                UserSettings.TemperatureUnit.KELVIN -> v + 273.15f
            }

        /**
         * SI Unit for Distance is Kilometers
         */
        fun distance(v: Float, userSettings: UserSettings): Float =
            when (userSettings.distanceUnit) {
                UserSettings.DistanceUnit.KILOMETERS -> v
                UserSettings.DistanceUnit.MILES -> v / 1.609f
            }

        /**
         * SI Unit for Speed is meters per second
         */
        fun speed(v: Float, userSettings: UserSettings): Float =
            when (userSettings.speedUnit) {
                UserSettings.SpeedUnit.METERS_PER_SECOND -> v
                UserSettings.SpeedUnit.MILES_PER_HOUR -> v * 2.237f
            }

        /**
         * SI Unit for Accumulation Intensity is mm/hour
         */
        fun precipitation(v: Float, userSettings: UserSettings): Float =
            when (userSettings.precipitationUnit) {
                UserSettings.IntensityUnit.MM_PER_HOUR -> v
                UserSettings.IntensityUnit.CM_PER_HOUR -> v / 10
                UserSettings.IntensityUnit.INCHES_PER_HOUR -> v / 25.4f
            }

        /**
         * SI Unit for Pressure is millibars or hectopascals
         */
        fun pressure(v: Float, userSettings: UserSettings): Float =
            when (userSettings.pressureUnit) {
                UserSettings.PressureUnit.MILLIBARS -> v
                UserSettings.PressureUnit.MMHG -> v / 1.333f
            }

        /**
         * SI Unit for accumulation is cm
         */
        fun accumulation(v: Float, userSettings: UserSettings): Float =
            when (userSettings.accumulationUnit) {
                UserSettings.AccumulationUnit.CENTIMETERS -> v
                UserSettings.AccumulationUnit.INCHES -> v / 2.54f
            }
    }
}