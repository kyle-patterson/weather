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
        fun temperature(v: Double, userSettings: UserSettings): Double =
            when (userSettings.temperatureUnit) {
                UserSettings.TemperatureUnit.CELSIUS -> v
                UserSettings.TemperatureUnit.FAHRENHEIT -> (v * 9 / 5) + 32.0
                UserSettings.TemperatureUnit.KELVIN -> v + 273.15
            }

        /**
         * SI Unit for Distance is Kilometers
         */
        fun distance(v: Double, userSettings: UserSettings): Double =
            when (userSettings.distanceUnit) {
                UserSettings.DistanceUnit.KILOMETERS -> v
                UserSettings.DistanceUnit.MILES -> v / 1.609
            }

        /**
         * SI Unit for Speed is meters per second
         */
        fun speed(v: Double, userSettings: UserSettings): Double =
            when (userSettings.speedUnit) {
                UserSettings.SpeedUnit.METERS_PER_SECOND -> v
                UserSettings.SpeedUnit.MILES_PER_HOUR -> v * 2.237
            }

        /**
         * SI Unit for Accumulation Intensity is mm/hour
         */
        fun precipitation(v: Double, userSettings: UserSettings): Double =
            when (userSettings.precipitationUnit) {
                UserSettings.IntensityUnit.MM_PER_HOUR -> v
                UserSettings.IntensityUnit.CM_PER_HOUR -> v / 10
                UserSettings.IntensityUnit.INCHES_PER_HOUR -> v / 25.4
            }

        /**
         * SI Unit for Pressure is millibars or hectopascals
         */
        fun pressure(v: Double, userSettings: UserSettings): Double =
            when (userSettings.pressureUnit) {
                UserSettings.PressureUnit.MILLIBARS -> v
                UserSettings.PressureUnit.MMHG -> v / 1.333
            }

        /**
         * SI Unit for accumulation is cm
         */
        fun accumulation(v: Double, userSettings: UserSettings): Double =
            when (userSettings.accumulationUnit) {
                UserSettings.AccumulationUnit.CENTIMETERS -> v
                UserSettings.AccumulationUnit.INCHES -> v / 2.54
            }
    }
}