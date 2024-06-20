package com.epikron.data.data.models

import com.google.gson.annotations.SerializedName

internal data class Dimension(
    @SerializedName("value")
    val value: Int = 0,
    @SerializedName("units")
    val units: String = ""
) {
    companion object {
        val empty = Dimension(0, "")
    }
}

internal data class GeoPoint(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("elevation")
    val elevation: Dimension
) {
    companion object {
        val empty = GeoPoint("", Dimension())
    }
}

internal data class CountryFactBookModel(
    @SerializedName("name_short")
    val nameShort: String,
    @SerializedName("name_long")
    val nameLong: String = "",
    @SerializedName("background")
    val background: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("area")
    val area: Dimension,
    @SerializedName("area_rank")
    val areaRank: Int,
    @SerializedName("climate")
    val climate: String,
    @SerializedName("low_point")
    val lowPoint: GeoPoint? = GeoPoint.empty,
    @SerializedName("high_point")
    val highPoint: GeoPoint? = GeoPoint.empty,
    @SerializedName("population")
    val population: Int,
    @SerializedName("population_rank")
    val populationRank: Int,
    @SerializedName("code")
    val code: String
) {
    companion object {
        val empty = CountryFactBookModel("", "", "", "", Dimension(), 0, "", GeoPoint.empty, GeoPoint.empty, 0, 0, "")
    }
}