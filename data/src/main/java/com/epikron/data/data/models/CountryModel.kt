package com.epikron.data.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.epikron.countriesandflags.CountriesQuery
import com.epikron.data.data.UPPERCASE_ALPHABET
import com.epikron.utils.overflowInRange
import kotlin.random.Random

@Entity
public data class CountryModel(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "country_code") val code: String,
    @ColumnInfo(name = "country_name") val name: String,
    @ColumnInfo(name = "country_native_name") val nativeName: String,
    @ColumnInfo(name = "country_continent") val continent: String,
    @ColumnInfo(name = "country_location") val location: String,
    @ColumnInfo(name = "country_area") val area: Int,
    @ColumnInfo(name = "country_capital") val capital: String,
    @ColumnInfo(name = "country_flag") val flag: String,
    @ColumnInfo(name = "country_currency") val currency: String,
    @ColumnInfo(name = "country_languages") val languages: String,
    @ColumnInfo(name = "country_background") val background: String
) {
    public companion object {
        public val empty: CountryModel = CountryModel(0, "", "", "", "", "", 0, "", "", "", "", "")
        public val test: CountryModel = generateCountryModel(0, "ES", "Spain", "Europe")
        public val testLong: CountryModel = test.copy(
            id = 1,
            code = "XX",
            name = "Very long Country Name under The Ocean",
            nativeName = "Long Name",
            capital = "The Middle of Nowhere",
            languages = "Italian, French, German, English, Spanish, Portuguese, Chinese"
        )
        public val previewTestList: List<CountryModel> = listOf(test, testLong, generateCountryModel(2, "PT", "Portugal", "Europe"))

        private fun generateCountryModel(id: Int, code: String, name: String, continent: String) : CountryModel = CountryModel(
            id = id,
            code = code,
            name = name,
            nativeName = "Native $name",
            capital = "$name Capital",
            continent = continent,
            location = "$continent Continent",
            area = Random.nextInt(1000, 999999),
            flag = "\uD83C\uDF0D",
            currency = "$name money",
            languages = "$name languages, $continent languages",
            background = "Lorem ipsum dolor sit amet"
        )

        public fun generateRandomCountriesList(count: Int, continent: String): List<CountryModel> {
            val imaginaryCountryNames: List<String> =
                listOf(
                    "Ambrosia", "Avalon", "Atlantis", "Buranda", "Costaguana", "Dorne", "Ishmaelia", "Isengard", "Laputa",
                    "Mordor", "Narnia", "Neverland", "Rivendell", "Rohan", "Utopia", "Wakanda", "Wonderland", "Zembla"
                ).shuffled()
            val list: MutableList<CountryModel> = mutableListOf()
            repeat(count) { index ->
                val country = imaginaryCountryNames[index.overflowInRange(imaginaryCountryNames.size - 1)]
                val code = country[0] + UPPERCASE_ALPHABET.random().toString()
                list.add(generateCountryModel(index, code, country, continent))
            }
            return list
        }

        internal fun CountriesQuery.Country.toCountryModel(countryDetails: CountryFactBookModel? = null): CountryModel {
            return CountryModel(
                code = this.code ?: "",
                name = this.name ?: "",
                nativeName = this.native ?: "",
                continent = this.continent?.name ?: "",
                capital = this.capital ?: "",
                flag = this.emoji ?: "",
                currency = this.currency ?: "",
                languages = this.languages?.joinToString { it?.name ?: "" } ?: "",
                area = countryDetails?.area?.value ?: 0,
                location = countryDetails?.location ?: "",
                background = countryDetails?.background ?: ""
            )
        }
    }
}
