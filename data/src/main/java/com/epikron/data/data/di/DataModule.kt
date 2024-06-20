package com.epikron.data.data.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.room.Room
import com.apollographql.apollo3.ApolloClient
import com.epikron.data.data.CONNECT_TIMEOUT_IN_SECS
import com.epikron.data.data.READ_TIMEOUT_IN_SECS
import com.epikron.data.data.WRITE_TIMEOUT_IN_SECS
import com.epikron.data.data.local.ContinentsDao
import com.epikron.data.data.local.ContinentsDatabase
import com.epikron.data.data.local.CountriesDao
import com.epikron.data.data.local.CountriesDatabase
import com.epikron.data.data.local.FactBook
import com.epikron.data.data.network.IpService
import com.epikron.data.data.network.UserAgentInterceptor
import com.epikron.data.data.repository.ContinentsRepository
import com.epikron.data.data.repository.ContinentsRepositoryImpl
import com.epikron.data.data.repository.CountriesRepository
import com.epikron.data.data.repository.CountriesRepositoryImpl
import com.epikron.data.data.repository.IpRepository
import com.epikron.data.data.repository.IpRepositoryImpl
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
public object RepositoryModule {
    @Singleton
    @Provides
    internal fun provideCountriesRepository(factBook: FactBook, apolloClient: ApolloClient, countriesDao: CountriesDao): CountriesRepository =
        CountriesRepositoryImpl(factBook, apolloClient, countriesDao)

    @Singleton
    @Provides
    internal fun provideContinentsRepository(apolloClient: ApolloClient, continentsDao: ContinentsDao): ContinentsRepository =
        ContinentsRepositoryImpl(apolloClient, continentsDao)

    @Singleton
    @Provides
    internal fun provideIpRepository(ipService: IpService): IpRepository =
        IpRepositoryImpl(ipService)

    @Singleton
    @Provides
    internal fun provideSharedPrefs(@ApplicationContext appContext: Context): SharedPreferences =
        appContext.getSharedPreferences("app_saved_keys", Context.MODE_PRIVATE)
}

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://countries.trevorblades.com/graphql")
            .build()
    }

    @Provides
    fun provideIpBaseUrl(): String = "https://api.ip2location.io/"

    @Provides
    fun httpClient() = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_IN_SECS.toLong(), TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT_IN_SECS.toLong(), TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_IN_SECS.toLong(), TimeUnit.SECONDS)
        .addInterceptor(UserAgentInterceptor())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideIpService(retrofit: Retrofit): IpService = retrofit.create(IpService::class.java)
}

@InstallIn(SingletonComponent::class)
@Module
internal object DatabaseModule {
    @Provides
    fun provideResources(@ApplicationContext appContext: Context): Resources = appContext.resources

    @Singleton
    @Provides
    fun provideFactBook(resources: Resources): FactBook = FactBook(resources)


    @Provides
    fun provideCountriesDao(database: CountriesDatabase): CountriesDao {
        return database.countriesDao()
    }

    @Provides
    @Singleton
    fun provideCountriesDatabase(@ApplicationContext appContext: Context): CountriesDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            CountriesDatabase::class.java,
            "CountriesDatabase"
        ).build()
    }

    @Provides
    fun provideContinentsDao(database: ContinentsDatabase): ContinentsDao {
        return database.continentsDao()
    }

    @Provides
    @Singleton
    fun provideContinentsDatabase(@ApplicationContext appContext: Context): ContinentsDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            ContinentsDatabase::class.java,
            "ContinentsDatabase"
        ).build()
    }
}
