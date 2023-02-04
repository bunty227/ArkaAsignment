package com.app.arkaasignment.networking

import com.app.arkaasignment.model.ImageDataClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiInterface {

    companion object {
        @JvmStatic
        var BASE_URL: String = "https://api.imgur.com"

        @JvmStatic
        var CLIENT_ID: String = "37b9ffb12c71679"

    }

    @GET("/3/gallery/top/week")
    fun getData(@Header("Client-ID") clientID: String): Call<ImageDataClass>
}
