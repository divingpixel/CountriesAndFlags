package com.epikron.data.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epikron.data.data.models.ContinentModel

@Dao
internal interface ContinentsDao {

    @Query("SELECT * FROM continentModel ORDER BY id DESC")
    suspend fun getAllContinents(): List<ContinentModel>

    @Query("SELECT * FROM continentModel WHERE continent_name LIKE :continentName LIMIT 1")
    suspend fun getByName(continentName : String): ContinentModel?

    @Query("SELECT * FROM continentModel WHERE continent_code LIKE :continentCode LIMIT 1")
    suspend fun getByCode(continentCode : String): ContinentModel?

    @Insert
    suspend fun insertContinent(continent: ContinentModel)

    @Insert
    suspend fun insertContinentList(continentList: List<ContinentModel>)

    @Delete
    suspend fun deleteContinent(continent: ContinentModel)

    @Update
    suspend fun updateContinent(continent: ContinentModel)
}