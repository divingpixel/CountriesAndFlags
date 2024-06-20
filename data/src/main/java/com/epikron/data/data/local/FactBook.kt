package com.epikron.data.data.local

import android.content.res.Resources
import android.util.Log
import com.epikron.data.data.models.CountryFactBookModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

public class FactBook @Inject constructor(private val resources: Resources) {

    internal suspend fun parseData(): List<CountryFactBookModel> {
        return coroutineScope {
            var result: List<CountryFactBookModel>
            resources.assets.open("data/factbook.json").apply {
                result = Gson().fromJson(readBytes().decodeToString(), object : TypeToken<List<CountryFactBookModel>>() {}.type)
                close()
            }
            Log.i("FACT BOOK", "DATA PARSED FOR ${result.size} COUNTRIES")
            result
        }
    }
}
