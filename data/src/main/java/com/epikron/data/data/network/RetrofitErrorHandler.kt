package com.epikron.data.data.network

import android.util.Log
import com.epikron.data.BuildConfig
import org.json.JSONObject
import retrofit2.Response

internal fun <T> retrofitErrorHandler(response: Response<T>): T? {
    return if (response.isSuccessful && response.body() != null) {
        response.body()!!
    } else {
        val errMsg = response.errorBody()?.string()?.let {
            JSONObject(it).getString("error")
        } ?: run {
            response.code().toString()
        }
        if (BuildConfig.DEBUG) Log.e("RETROFIT ERROR", errMsg)
        null
    }
}
