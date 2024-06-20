package com.epikron.countriesandflags

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.epikron.countriesandflags.ui.composables.NavigationDrawer
import com.epikron.countriesandflags.ui.theme.SwipingCardsTheme
import com.epikron.countriesandflags.util.ConnectivityObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        ConnectivityObserver.register(applicationContext)

        setContent {
            SwipingCardsTheme {
                NavigationDrawer()
            }
        }

        if (BuildConfig.DEBUG) CoroutineScope(Dispatchers.IO).launch {
            ConnectivityObserver.isNetworkAvailableFlow.collect { isNetworkAvailable ->
                Log.i("NETWORK STATUS", "NETWORK AVAILABLE : $isNetworkAvailable")
            }
        }
    }
}
