package com.matthaigh27.chatgptwrapper.data.remote

import com.matthaigh27.chatgptwrapper.data.repository.SharedPreferencesRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    /**
     * This variables are used to set time for http communication.
     */
    val TIME_OUT_CALL = 60L
    val TIME_OUT_CONNECT = 60L
    val TIME_OUT_READ = 60L
    val TIME_OUT_WRITE = 60L

    lateinit var apiService: ApiService

    private lateinit var serverUrl: String
    private lateinit var client: OkHttpClient
    private lateinit var retrofit: Retrofit

    init {
        initClient()
    }

    fun initClient() {
        /**
         * The server url is set to url that a user stored before.
         * If the user run this app at first, server url is set to default url.
         */
        val keys = SharedPreferencesRepository.getConfig()
        serverUrl = keys.serverUrl

        client = OkHttpClient
            .Builder()
            .callTimeout(TIME_OUT_CALL, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT_WRITE, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit
            .Builder()
            .baseUrl(serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }


    companion object {
        val instance = ApiClient()
    }
}