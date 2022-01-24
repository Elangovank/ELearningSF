package com.gm.WebServices

import com.gm.models.Model
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface APIInterface {

    @GET("/v2.0/forecast/hourly?")
    fun doGetUserList(
            @Query("postal_code")postal_code:String?,
            @Query("key") key: String?): Call<Model.WeatherData?>?

}


