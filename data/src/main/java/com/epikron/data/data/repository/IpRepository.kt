package com.epikron.data.data.repository

import com.epikron.data.data.network.IpService
import com.epikron.data.data.network.retrofitErrorHandler
import javax.inject.Inject

public fun interface IpRepository {
    public suspend fun getIpCountryCode(): String?
}

internal class IpRepositoryImpl @Inject constructor(
    private val ipService: IpService
) : IpRepository {
    override suspend fun getIpCountryCode(): String? {
        return retrofitErrorHandler(ipService.getIpResult())?.countryCode
    }
}
