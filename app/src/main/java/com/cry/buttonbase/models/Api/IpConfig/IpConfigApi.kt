package com.cry.buttonbase.models.Api.IpConfig

import com.cry.buttonbase.models.IpData
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface IpConfigApi
{
    @GET ("json")
    fun loadData(@Query("q") status : String) : Call<IpData>
}