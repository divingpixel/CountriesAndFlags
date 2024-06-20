package com.epikron.countriesandflags.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ConnectivityObserver {

    private var isRegistered: Boolean = false
    private val _isNetworkAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isNetworkAvailableFlow: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()
    val isNetworkAvailable: Boolean = _isNetworkAvailable.value

    fun register(context: Context) {
        if (!isRegistered) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            _isNetworkAvailable.value = capabilities != null
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    _isNetworkAvailable.value = true
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    _isNetworkAvailable.value = false
                }
            })
        }
    }
}
