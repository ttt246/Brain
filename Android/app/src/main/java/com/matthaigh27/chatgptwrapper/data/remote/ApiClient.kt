package com.matthaigh27.chatgptwrapper.data.remote

import com.matthaigh27.chatgptwrapper.utils.constants.CommonConstants.API_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    val TIME_OUT_CALL = 60L
    val TIME_OUT_CONNECT = 60L
    val TIME_OUT_READ = 60L
    val TIME_OUT_WRITE = 60L

    private val client = OkHttpClient
        .Builder()
        .callTimeout(TIME_OUT_CALL, TimeUnit.SECONDS)
        .connectTimeout(TIME_OUT_CONNECT, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT_READ, TimeUnit.SECONDS)
        .writeTimeout(TIME_OUT_WRITE, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}