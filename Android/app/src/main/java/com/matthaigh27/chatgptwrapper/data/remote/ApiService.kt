package com.matthaigh27.chatgptwrapper.data.remote

import com.matthaigh27.chatgptwrapper.data.remote.requests.BaseApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.ImageRelatednessApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.NotificationApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainContactsApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainImageApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.CommonResult
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.HelpCommandResult
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.ImageRelatenessResult
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.TrainImageResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("commands")
    fun getAllHelpCommands(@Body request: BaseApiRequest) : Call<ApiResponse<HelpCommandResult>>
    @POST("sendNotification")
    fun sendNotification(@Body request: NotificationApiRequest) : Call<ApiResponse<CommonResult>>
    @POST("train/contacts")
    fun trainContacts(@Body request: TrainContactsApiRequest) : Call<ApiResponse<String>>
    @POST("image_relatedness")
    fun getImageRelatedness(@Body request: ImageRelatednessApiRequest) : Call<ApiResponse<ImageRelatenessResult>>
    @POST("uploadImage")
    fun trainImage(@Body request: TrainImageApiRequest) : Call<ApiResponse<TrainImageResult>>
}