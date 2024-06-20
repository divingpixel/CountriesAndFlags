@file:OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.epikron.data

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Query
import com.epikron.data.data.local.ContinentsDao
import com.epikron.data.data.local.CountriesDao
import com.epikron.data.data.local.FactBook
import com.epikron.data.data.models.ContinentModel
import com.epikron.data.data.models.CountryModel
import com.epikron.data.data.models.IpLocationModel
import com.epikron.data.data.network.IpService
import com.epikron.data.data.repository.ContinentsRepository
import com.epikron.data.data.repository.ContinentsRepositoryImpl
import com.epikron.data.data.repository.CountriesRepository
import com.epikron.data.data.repository.CountriesRepositoryImpl
import com.epikron.data.data.repository.IpRepository
import com.epikron.data.data.repository.IpRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RepositoriesTest {

    private val mockIpService: IpService = mockk()
    private val mockCountriesDao: CountriesDao = mockk()
    private val mockContinentsDao: ContinentsDao = mockk()
    private val mockApolloClient: ApolloClient = mockk()
    private val mockFactBook: FactBook = mockk()

    private val mockCountriesRepository: CountriesRepository = CountriesRepositoryImpl(mockFactBook, mockApolloClient, mockCountriesDao)
    private val mockContinentsRepository: ContinentsRepository = ContinentsRepositoryImpl(mockApolloClient, mockContinentsDao)
    private val mockIpRepository: IpRepository = IpRepositoryImpl(mockIpService)

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        mockkStatic(Log::class)
        coEvery { Log.i(any(), any()) } returns 0
        coEvery { Log.e(any(), any()) } returns 0
        coEvery { mockApolloClient.query(any(Query::class)).execute() } returns mockk()
        coEvery { mockFactBook.parseData() } returns listOf()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `WHEN country code is requested THEN IpRepository provides it`() {
        coEvery { mockIpService.getIpResult() } returns Response.success(IpLocationModel.empty.copy(countryCode = "XX"))
        runTest {
            val code = mockIpRepository.getIpCountryCode()
            coVerify { mockIpService.getIpResult() }
            assert(code == "XX")
        }
    }

    @Test
    fun `WHEN country code is requested and an error occurs THEN IpRepository returns null`() {
        coEvery { mockIpService.getIpResult() } returns Response.error(404, "{ error : '404'}".toResponseBody())
        runTest {
            val code = mockIpRepository.getIpCountryCode()
            coVerify { mockIpService.getIpResult() }
            assert(code == null)
        }
    }

    @Test
    fun `WHEN countries list is requested THEN CountriesRepository provides it`() {
        val continent = "Europe"
        val countriesCount = 14
        val mockCountryList = CountryModel.generateRandomCountriesList(countriesCount, continent)
        coEvery { mockCountriesDao.getAllCountries() } returns mockCountryList
        coEvery { mockCountriesDao.getContinentForCountry(any()) } returns continent
        coEvery { mockCountriesDao.getCountriesForContinent(any()) } returns mockCountryList
        runTest {
            val countries = mockCountriesRepository.getAllCountries()
            val countryContinent = mockCountriesRepository.getContinentForCountry(mockCountryList[0].code)
            val countriesEurope = mockCountriesRepository.getCountriesForContinent(continent)
            coVerify { mockCountriesDao.getAllCountries() }
            coVerify { mockCountriesDao.getContinentForCountry(any()) }
            coVerify { mockCountriesDao.getCountriesForContinent(any()) }
            assert(countries.size == countriesCount)
            assert(countryContinent == continent)
            assert(countriesEurope == mockCountryList)
        }
    }

    @Test
    fun `WHEN countries list is not in database THEN countries are retrieved from network and written in DB`() {
        coEvery { mockCountriesDao.getAllCountries() } returns emptyList()
        coEvery { mockCountriesDao.insertCountryList(any()) } returns Unit
        runTest {
            mockCountriesRepository.getAllCountries()
            coVerify { mockCountriesDao.getAllCountries() }
            coVerify { mockApolloClient.query(any(Query::class)) }
            coVerify { mockFactBook.parseData() }
            coVerify { mockCountriesDao.insertCountryList(any()) }
        }
    }

    @Test
    fun `WHEN continents list is requested THEN ContinentsRepository provides it`() {
        val mockContinentsList = ContinentModel.list
        coEvery { mockContinentsDao.getAllContinents() } returns mockContinentsList
        runTest {
            val continents = mockContinentsRepository.getContinents()
            coVerify { mockContinentsDao.getAllContinents() }
            assert(continents == mockContinentsList)
        }
    }

    @Test
    fun `WHEN continents list is not in database THEN continents are retrieved from network and written in DB`() {
        coEvery { mockContinentsDao.getAllContinents() } returns emptyList()
        coEvery { mockContinentsDao.insertContinentList(any()) } returns Unit
        runTest {
            mockContinentsRepository.getContinents()
            coVerify { mockContinentsDao.getAllContinents() }
            coVerify { mockApolloClient.query(any(Query::class)) }
            coVerify { mockContinentsDao.insertContinentList(any()) }
        }
    }
}
