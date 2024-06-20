package com.epikron.data.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epikron.data.data.models.CountryModel

@Dao
internal interface CountriesDao {

    @Query("SELECT * FROM countryModel ORDER BY country_name")
    suspend fun getAllCountries(): List<CountryModel>

    @Query("SELECT * FROM countryModel WHERE country_name LIKE :countryName LIMIT 1")
    suspend fun getByName(countryName : String): CountryModel?

    @Query("SELECT * FROM countryModel WHERE country_code LIKE :countryCode LIMIT 1")
    suspend fun getByCode(countryCode : String): CountryModel?

    @Query("SELECT country_continent FROM countryModel WHERE country_code LIKE :countryCode LIMIT 1")
    suspend fun getContinentForCountry(countryCode: String?): String?

    @Query("SELECT * FROM countryModel WHERE country_continent LIKE :continentName ORDER BY country_name")
    suspend fun getCountriesForContinent(continentName: String) : List<CountryModel>

    @Insert
    suspend fun insertCountry(country: CountryModel)

    @Insert
    suspend fun insertCountryList(countryList: List<CountryModel>)

    @Delete
    suspend fun deleteCountry(country: CountryModel)

    @Update
    suspend fun updateCountry(country: CountryModel)
}