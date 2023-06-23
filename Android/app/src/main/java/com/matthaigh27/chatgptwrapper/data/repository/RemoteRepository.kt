package com.matthaigh27.chatgptwrapper.data.repository

import com.matthaigh27.chatgptwrapper.data.remote.ApiClient
import com.matthaigh27.chatgptwrapper.data.remote.requests.NotificationApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.utils.helpers.network.OnFailure
import com.matthaigh27.chatgptwrapper.utils.helpers.network.OnSuccess
import com.matthaigh27.chatgptwrapper.utils.helpers.network.RequestFactory.buildBaseApiRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object RemoteRepository {
    private val apiService = ApiClient.apiService

    fun getAllHelpCommands(
        onSuccess: OnSuccess<ApiResponse>,
        onFailure: OnFailure<String>
    ) {
        val call = apiService.getAllHelpCommands(buildBaseApiRequest())

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                response.body()?.let {
                    onSuccess(ApiResponse(it.status_code, it.message, it.result))
                }?: run {
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
                    onSuccess(ApiResponse(data.status_code, data.message, data.result))
                }?: run {
                    onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }
}