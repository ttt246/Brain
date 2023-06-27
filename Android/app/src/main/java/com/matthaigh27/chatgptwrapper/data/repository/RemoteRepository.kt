package com.matthaigh27.chatgptwrapper.data.repository

import com.matthaigh27.chatgptwrapper.data.remote.ApiClient
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.data.remote.requests.ImageRelatednessApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.NotificationApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainContactsApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainImageApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.data.remote.responses.EmptyResultApiResponse
import com.matthaigh27.chatgptwrapper.data.remote.responses.ImageRelatenessApiResponse
import com.matthaigh27.chatgptwrapper.data.remote.responses.TrainImageApiResponse
import com.matthaigh27.chatgptwrapper.utils.helpers.OnFailure
import com.matthaigh27.chatgptwrapper.utils.helpers.OnSuccess
import com.matthaigh27.chatgptwrapper.utils.helpers.network.RequestFactory.buildBaseApiRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object RemoteRepository {
    private val apiService = ApiClient.apiService

    fun getAllHelpCommands(
        onSuccess: OnSuccess<ApiResponse>, onFailure: OnFailure<String>
    ) {
        val call = apiService.getAllHelpCommands(buildBaseApiRequest())

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                response.body()?.let { data ->
                    onSuccess(data)
                } ?: run {
                    onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    fun sendNotification(
        request: NotificationApiRequest,
        onSuccess: OnSuccess<ApiResponse>,
        onFailure: OnFailure<String>
    ) {
        val call = apiService.sendNotification(request)

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                response.body()?.let { data ->
                    onSuccess(data)
                } ?: run {
                    onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    fun trainContacts(
        request: TrainContactsApiRequest,
        onSuccess: OnSuccess<EmptyResultApiResponse>,
        onFailure: OnFailure<String>
    ) {
        val call = apiService.trainContacts(request)

        call.enqueue(object : Callback<EmptyResultApiResponse> {
            override fun onResponse(
                call: Call<EmptyResultApiResponse>, response: Response<EmptyResultApiResponse>
            ) {
                response.body()?.let { data ->
                    onSuccess(data)
                } ?: run {
                    onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<EmptyResultApiResponse>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    suspend fun trainImage(request: TrainImageApiRequest) : ApiResource<TrainImageApiResponse> = suspendCoroutine { continuation ->

        val call = apiService.trainImage(request)

        call.enqueue(object : Callback<TrainImageApiResponse> {
            override fun onResponse(
                call: Call<TrainImageApiResponse>, response: Response<TrainImageApiResponse>
            ) {
                response.body()?.let { data ->
                    continuation.resume(ApiResource.Success(data))
                } ?: run {
                    continuation.resume(ApiResource.Error("Error"))
                }
            }

            override fun onFailure(call: Call<TrainImageApiResponse>, t: Throwable) {
                continuation.resume(ApiResource.Error(t.message.toString()))
            }
        })
    }

    fun getImageRelatedness(
        request: ImageRelatednessApiRequest,
        onSuccess: OnSuccess<ImageRelatenessApiResponse>,
        onFailure: OnFailure<String>
    ) {
        val call = apiService.getImageRelatedness(request)

        call.enqueue(object : Callback<ImageRelatenessApiResponse> {
            override fun onResponse(
                call: Call<ImageRelatenessApiResponse>, response: Response<ImageRelatenessApiResponse>
            ) {
                response.body()?.let { data ->
                    onSuccess(data)
                } ?: run {
                    onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<ImageRelatenessApiResponse>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }
}