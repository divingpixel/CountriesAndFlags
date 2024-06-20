package com.epikron.data.data.network

import com.epikron.data.data.models.IpLocationModel
import retrofit2.Response
import retrofit2.http.GET

public interface IpService {
    @GET(".")
    public suspend fun getIpResult() : Response<IpLocationModel>
}