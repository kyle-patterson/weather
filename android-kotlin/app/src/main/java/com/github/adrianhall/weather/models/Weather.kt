package com.github.adrianhall.weather.models

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.adrianhall.weather.utils.UnixTimestampDeserializer
import java.net.URI
import java.util.*

/**
 * Data class for the response from Dark Sky API.
 *
 * This is a direct correlation to the JSON format that Dark Sky returns.  For more information,
 * see https://darksky.net/dev/docs#response-format
 */
data class Weather(
    val latitude: Float,
    val longitude: Float,
    val timezone: String,
    val offset: Int?,
    val currently: CurrentWeather?,
    val hourly: HourlyWeather?,
    val daily: DailyWeather?,
    val alerts: List<WeatherAlert>?) {

    data class CurrentWeather(
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val time: Date,
        val summary: String,
        val icon: String,
        val nearestStormDistance: Float,
        val nearestStormBearing: Int,
        val precipIntensity: Float,
        val precipProbability: Float,
        val precipType: String?,
        val temperature: Float,
        val apparentTemperature: Float,
        val dewPoint: Float,
        val humidity: Float,
        val pressure: Float,
        val windSpeed: Float,
        val windGust: Float,
        val windBearing: Int,
        val cloudCover: Float,
        val uvIndex: Int,
        val visibility: Float,
        val ozone: Float
    )

    data class HourlyWeather(
        val summary: String,
        val icon: String,
        val data: List<HourlyWeatherPoint>
    )

    data class DailyWeather(
        val summary: String,
        val icon: String,
        val data: List<DailyWeatherPoint>
    )

    data class HourlyWeatherPoint(
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val time: Date,
        val summary: String,
        val icon: String,
        val precipIntensity: Float,
        val precipProbability: Float,
        val precipType: String?,
        val temperature: Float,
        val apparentTemperature: Float,
        val dewPoint: Float,
        val humidity: Float,
        val pressure: Float,
        val windSpeed: Float,
        val windGust: Float,
        val windBearing: Int,
        val cloudCover: Float,
        val uvIndex: Int,
        val visibility: Float,
        val ozone: Float
    )

    data class DailyWeatherPoint(
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val time: Date,
        val summary: String,
        val icon: String,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val sunriseTime: Date,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val sunsetTime: Date,
        val moonPhase: Float,
        val precipIntensity: Float,
        val precipIntensityMax: Float,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val precipIntensityMaxTime: Date,
        val precipProbability: Float,
        val precipType: String?,
        val temperatureHigh: Float,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val temperatureHighTime: Date,
        val temperatureLow: Float,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val temperatureLowTime: Date,
        val apparentTemperatureHigh: Float,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val apparentTemperatureHighTime: Date,
        val apparentTemperatureLow: Float,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val apparentTemperatureLowTime: Date,
        val dewPoint: Float,
        val humidity: Float,
        val pressure: Float,
        val windSpeed: Float,
        val windGust: Float,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val windGustTime: Date,
        val windBearing: Int,
        val cloudCover: Float,
        val uvIndex: Int,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val uvIndexTime: Date,
        val visibility: Float,
        val ozone: Float,
        val temperatureMin: Float,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val temperatureMinTime: Date,
        val temperatureMax: Float,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val temperatureMaxTime: Date,
        val apparentTemperatureMin: Float,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val apparentTemperatureMinTime: Date,
        val apparentTemperatureMax: Float,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val apparentTemperatureMaxTime: Date
    )

    data class WeatherAlert(
        val title: String,
        val severity: String,
        val regions: List<String>?,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val time: Date,
        @JsonDeserialize(using = UnixTimestampDeserializer::class)
        val expires: Date,
        val description: String,
        val uri: URI?
    )
}





