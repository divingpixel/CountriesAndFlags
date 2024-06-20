package com.epikron.data.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.epikron.countriesandflags.CountriesQuery
import com.epikron.data.BuildConfig
import com.epikron.data.data.START_CONTINENT
import com.epikron.data.data.local.CountriesDao
import com.epikron.data.data.local.FactBook
import com.epikron.data.data.models.CountryFactBookModel
import com.epikron.data.data.models.CountryModel
import com.epikron.data.data.models.CountryModel.Companion.toCountryModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

public interface CountriesRepository {
    public suspend fun getAllCountries(): List<CountryModel>
    public suspend fun getContinentForCountry(countryCode: String?): String
    public suspend fun getCountriesForContinent(continentName: String): List<CountryModel>
}

internal class CountriesRepositoryImpl @Inject constructor(
    private val factBook: FactBook,
    private val apolloClient: ApolloClient,
    private val countriesDao: CountriesDao
) : CountriesRepository {
    private var factBookData: List<CountryFactBookModel> = listOf()
    private var countriesDaoJobInstance: Deferred<List<CountryModel>> = CompletableDeferred()

    init {
        countriesDaoJobInstance.cancel()
    }

    private fun List<CountriesQuery.Country>?.mapToCountryModel(): List<CountryModel> {
        return this?.let { list ->
            list.map { country ->
                country.toCountryModel(
                    country.code?.let { countryCode ->
                        getCountryFactBook(countryCode)
                    } ?: CountryFactBookModel.empty
                )
            }
        } ?: listOf()
    }

    private fun getCountryFactBook(code: String): CountryFactBookModel {
        return factBookData.firstOrNull { it.code.lowercase() == code.lowercase() } ?: CountryFactBookModel.empty
    }

    private suspend fun getApolloData() =
        try {
            apolloClient
                .query(CountriesQuery())
                .execute()
                .data?.countries?.filterNotNull()
        } catch (error: ApolloException) {
            if (BuildConfig.DEBUG) Log.e("APOLLO ERROR", error.message.toString())
            listOf()
        }
            .mapToCountryModel()

    override suspend fun getAllCountries(): List<CountryModel> {
        coroutineScope {
            if (!countriesDaoJobInstance.isActive) {
                countriesDaoJobInstance = async {
                    countriesDao.getAllCountries().ifEmpty {
                        factBookData = factBook.parseData()
                        val queryCountries = getApolloData()
                        countriesDao.insertCountryList(queryCountries)
                        if (BuildConfig.DEBUG) Log.i("COUNTRIES REPOSITORY", "ADDED ${queryCountries.size} COUNTRIES TO DB")
                        queryCountries
                    }
                }
            } else {
                countriesDaoJobInstance
            }
        }
        return countriesDaoJobInstance.await()
    }

    override suspend fun getContinentForCountry(countryCode: String?): String {
        return countriesDao.getContinentForCountry(countryCode) ?: START_CONTINENT
    }

    override suspend fun getCountriesForContinent(continentName: String): List<CountryModel> {
        return countriesDao.getCountriesForContinent(continentName)
    }
}
