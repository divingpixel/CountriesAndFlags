package com.epikron.data.data.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class UserAgentInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request()
                .newBuilder()
                .addHeader(USER_AGENT_KEY, USER_AGENT_STRING)
                .build()
        )
    }

    companion object {
        private const val USER_AGENT_KEY = "User-Agent"
        private const val USER_AGENT_STRING = "SwipingCards"
    }
}
