package com.matthaigh27.chatgptwrapper.data.remote

import com.matthaigh27.chatgptwrapper.data.remote.requests.BaseApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.ImageRelatednessApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.NotificationApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainContactsApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainImageApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.data.remote.responses.EmptyResultApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("commands")
    fun getAllHelpCommands(@Body request: BaseApiRequest) : Call<ApiResponse>
    @POST("sendNotification")
    fun sendNotification(@Body request: NotificationApiRequest) : Call<ApiResponse>
    @POST("train/contacts")
    fun trainContacts(@Body request: TrainContactsApiRequest) : Call<EmptyResultApiResponse>
    @POST("image_relatedness")
    fun trainContacts(@Body request: ImageRelatednessApiRequest) : Call<ApiResponse>
    @POST("uploadImage")
    fun trainContacts(@Body request: TrainImageApiRequest) : Call<EmptyResultApiResponse>
}