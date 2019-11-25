package com.github.adrianhall.weather.models

import android.location.Address
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import com.azure.data.model.Document

/**
 * The model for the favorites.  This provides the longitude / latitude / display name and
 * current location flag.  We have a "dataHasChanged" observable that is triggered when
 * anything is changed.
 */
class FavoriteCity (id: String? = null) : Document(id) {
    private var mDateChanged: MutableLiveData<Date?> = MutableLiveData()

    @JsonIgnore
    val dataHasChanged: LiveData<Date?> = mDateChanged

    var userid: String = ""

    var latitude: Double = 0.0
        private set

    var longitude: Double = 0.0
        private set

    var displayName: String = ""
        private set

    @JsonIgnore
    var isCurrentLocation: Boolean = false
        private set

    constructor(latitude: Double = 0.0, longitude: Double = 0.0, displayName: String = "") : this() {
        this.latitude = latitude
        this.longitude = longitude
        this.displayName = displayName
    }

    /**
     * Sets the location based on a location - use geocoding to set the display name.
     */
    fun setLocation(location: Location, name: String, isActive: Boolean = false) {
        latitude = location.latitude
        longitude = location.longitude
        displayName = name
        isCurrentLocation = isActive
        notifyDataHasChanged()
    }

    /**
     * Sets the location based on an address
     */
    fun setLocation(address: Address, isActive: Boolean = false) {
        latitude = address.latitude
        longitude = address.longitude
        if (address.countryCode == "US") {
            displayName = "${address.locality}, USA"
        } else {
            displayName = "${address.locality}, ${address.countryName}"
        }
        isCurrentLocation = isActive
        notifyDataHasChanged()
    }

    /**
     * Notify the observers that the data has changed.
     */
    private fun notifyDataHasChanged() {
        mDateChanged.postValue(Date())
    }

    /**
     * Returns the ID of the locality
     */
    fun toLocationString(): String {
        return "%.5f,%.5f".format(latitude,longitude)
    }
}