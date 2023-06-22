package com.matthaigh27.chatgptwrapper.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.matthaigh27.chatgptwrapper.data.remote.ApiClient
import com.matthaigh27.chatgptwrapper.data.remote.requests.ApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.utils.helpers.OnFailure
import com.matthaigh27.chatgptwrapper.utils.helpers.OnSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object RemoteRepository {
    private val apiService = ApiClient.apiService

    fun getAllHelpCommands(
        onSuccess: OnSuccess<ApiResponse>,
        onFailure: OnFailure
    ) {
        val call = apiService.getAllHelpCommands()

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                response.body()?.let {
                    onSuccess(ApiResponse(it.status_code, it.message, it.result))
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun sendNotification(
        request: ApiRequest,
        onSuccess: OnSuccess<ApiResponse>,
        onFailure: OnFailure
    ) {
        val call = apiService.sendNotification(request)

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val data = response.body()
                onSuccess(ApiResponse(data!!.status_code, data.message, data.result))
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onFailure(t)
            }
        })
    }
}