package com.epikron.data.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.epikron.countriesandflags.ContinentsQuery
import com.epikron.data.BuildConfig
import com.epikron.data.data.local.ContinentsDao
import com.epikron.data.data.models.ContinentModel
import com.epikron.data.data.models.ContinentModel.Companion.toContinentModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

public fun interface ContinentsRepository {
    public suspend fun getContinents(): List<ContinentModel>
}

internal class ContinentsRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val continentsDao: ContinentsDao
) : ContinentsRepository {

    private var continentsDaoJobInstance: Deferred<List<ContinentModel>> = CompletableDeferred()

    init {
        continentsDaoJobInstance.cancel()
    }

    private suspend fun getApolloData() =
        try {
            apolloClient
                .query(ContinentsQuery())
                .execute()
                .data?.continents
                ?.filterNotNull()
                ?.sortedBy { it.name } ?: listOf()
        } catch (error: ApolloException) {
            if (BuildConfig.DEBUG) Log.e("APOLLO ERROR", error.message.toString())
            listOf()
        }
            .map { it.toContinentModel() }
            .ifEmpty { ContinentModel.list }

    override suspend fun getContinents(): List<ContinentModel> {
        coroutineScope {
            if (!continentsDaoJobInstance.isActive) {
                continentsDaoJobInstance = async {
                    continentsDao.getAllContinents().ifEmpty {
                        val result = getApolloData()
                        continentsDao.insertContinentList(result)
                        if (BuildConfig.DEBUG) Log.i("CONTINENTS REPOSITORY", "ADDED ${result.size} CONTINENTS TO DB")
                        result
                    }
                }
            } else {
                continentsDaoJobInstance
            }
        }
        return continentsDaoJobInstance.await()
    }
}
