package com.epikron.data.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.epikron.data.data.models.ContinentModel

@Database(entities = [ContinentModel::class], version = 1)
internal abstract class ContinentsDatabase : RoomDatabase() {
    internal abstract fun continentsDao() : ContinentsDao
}
