package com.epikron.data.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.epikron.data.data.models.CountryModel

@Database(entities = [CountryModel::class], version = 1)
internal abstract class CountriesDatabase : RoomDatabase() {
    internal abstract fun countriesDao(): CountriesDao
}
