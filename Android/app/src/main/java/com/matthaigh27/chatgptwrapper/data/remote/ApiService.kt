package com.matthaigh27.chatgptwrapper.data.remote

import com.matthaigh27.chatgptwrapper.data.remote.requests.BaseApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.NotificationApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("commands")
    fun getAllHelpCommands(@Body request: BaseApiRequest) : Call<ApiResponse>
    @POST("sendNotification")
    fun sendNotification(@Body request: NotificationApiRequest) : Call<ApiResponse>
}