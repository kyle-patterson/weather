package com.github.adrianhall.weather.models

import com.azure.data.model.Document
import java.time.ZoneOffset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FavoriteCityList (id: String? = null) : Document(id) {

    var userid: String = ""
        private set

    var cityListJson: String = ""
        private set

    var lastUpdateTime: String = ""

    constructor(id: String = "", userid: String = "", cityListJson: String = "") : this() {
        val current = LocalDateTime.now(ZoneOffset.UTC)
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val formatted = current.format(formatter)

        this.lastUpdateTime = formatted
        this.id = id
        this.userid = userid
        this.cityListJson = cityListJson
    }
}
