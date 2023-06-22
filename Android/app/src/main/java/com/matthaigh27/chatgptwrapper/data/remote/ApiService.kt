package com.matthaigh27.chatgptwrapper.data.remote

import com.matthaigh27.chatgptwrapper.data.remote.requests.ApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("commands")
    fun getAllHelpCommands() : Call<ApiResponse>
    @POST("sendNotification")
    fun sendNotification(@Body request: ApiRequest) : Call<ApiResponse>
}