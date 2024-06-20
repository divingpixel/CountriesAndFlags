package com.epikron.data.data.models

import com.google.gson.annotations.SerializedName

public data class IpLocationModel(
    @SerializedName("ip")
    val ip: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("country_name")
    val countryName: String,
    @SerializedName("region_name")
    val regionName: String,
    @SerializedName("city_name")
    val cityName: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("zip_code")
    val zipCode: String,
    @SerializedName("time_zone")
    val timeZone: String,
    @SerializedName("asn")
    val asNumber: String,
    @SerializedName("as")
    val asName: String,
    @SerializedName("is_proxy")
    val isProxy: Boolean,
    @SerializedName("message")
    val message: String
) {
    public companion object {
        public val empty: IpLocationModel = IpLocationModel("", "", "", "", "", 0.0, 0.0, "", "", "", "", false, "")
    }
}
