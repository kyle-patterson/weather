package com.github.adrianhall.weather.models

import java.util.*

/**
 * Representation of a single location that the user wants to monitor.  These are displayed
 * in a list within the main activity, among other places.  Each location is represented by
 * a UUID, so it's perfectly reasonable to have two locations with different ID and the same
 * lat/long
 */
data class UserLocation(val latitude: Double, val longitude: Double, var displayName: String = "") {
    companion object {
        val defaultLocations = listOf(
            UserLocation(47.6062, -12.3321, "Seattle, USA"),
            UserLocation(51.5074, -0.1278, "London, England"),
            UserLocation(36.3932, 25.4615, "Santorini, Greece")
        )
    }
    val id: String = UUID.randomUUID().toString()

    /**
     * When displaying the location, do it as a lat/long as displayName may be empty
     */
    override fun toString(): String = "%.5f,%.5f".format(latitude, longitude)

    /**
     * This application considers two locations identical only if their ID is equal
     */
    override fun equals(other: Any?) = other is UserLocation && other.id == this.id

    /**
     * Since our identity is tied up in the ID value, we can override the hashCode() as well
     */
    override fun hashCode(): Int = this.id.hashCode()
}