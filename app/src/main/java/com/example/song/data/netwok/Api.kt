package com.example.song.data.netwok

import com.example.song.data.model.Song
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("search")
     fun getSongs(@Query("term") name :String): Call<Song>


    companion object {
        operator fun invoke(): Api {
            return Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl("https://itunes.apple.com/")
                .build()
                .create(Api::class.java)
        }
    }

}