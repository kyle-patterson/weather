package com.github.adrianhall.weather.models

/**
 * Representation of the user settings.  These are set in the settings activity by the user
 * and saved to user preferences.
 */
class UserSettings {
    var temperatureUnit    = TemperatureUnit.FAHRENHEIT
    var distanceUnit       = DistanceUnit.MILES
    var speedUnit          = SpeedUnit.MILES_PER_HOUR
    var precipitationUnit  = IntensityUnit.INCHES_PER_HOUR
    var pressureUnit       = PressureUnit.MILLIBARS
    var accumulationUnit   = AccumulationUnit.INCHES

    // Below here are the enums that describe the settings we support.  Note that each
    // setting for a display unit needs a corresponding function for conversion in the
    // SIConverter class.
    enum class TemperatureUnit {
        CELSIUS,
        FAHRENHEIT,
        KELVIN
    }

    enum class DistanceUnit {
        MILES,
        KILOMETERS
    }

    enum class SpeedUnit {
        METERS_PER_SECOND,
        MILES_PER_HOUR
    }

    enum class IntensityUnit {
        MM_PER_HOUR,
        CM_PER_HOUR,
        INCHES_PER_HOUR
    }

    enum class PressureUnit {
        MILLIBARS,
        MMHG
    }

    enum class AccumulationUnit {
        INCHES,
        CENTIMETERS
    }
}



