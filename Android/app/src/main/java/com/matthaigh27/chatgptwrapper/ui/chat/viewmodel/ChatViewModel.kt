package com.matthaigh27.chatgptwrapper.ui.chat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matthaigh27.chatgptwrapper.RisingApplication
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.data.remote.requests.ApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.data.repository.RemoteRepository
import com.matthaigh27.chatgptwrapper.utils.helpers.ImageHelper
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val DEFAULT_GPT_MODEL = "gpt-3.5-turbo"

    fun getAllHelpCommands(): MutableLiveData<ApiResource<ApiResponse>> {
        val resource: MutableLiveData<ApiResource<ApiResponse>> = MutableLiveData()
        resource.value = ApiResource.Loading()

        RemoteRepository.getAllHelpCommands(onSuccess = { apiResponse ->
            resource.value = ApiResource.Success(apiResponse)
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable.message.toString())
        })

        return resource
    }

    fun sendNotification(message: String): MutableLiveData<ApiResource<ApiResponse>> {
        val request = ApiRequest(
            token = RisingApplication.appContext.getFCMToken(),
            uuid = RisingApplication.appContext.getUUID(),
            model = DEFAULT_GPT_MODEL,
            message = message
        )
        val resource: MutableLiveData<ApiResource<ApiResponse>> = MutableLiveData()
        resource.value = ApiResource.Loading()

        RemoteRepository.sendNotification(
            request,
            onSuccess = { apiResponse ->
                resource.value = ApiResource.Success(apiResponse)
            },
            onFailure = { throwable ->
                resource.value = ApiResource.Error(throwable.message.toString())
            }
        )

        return resource
    }

    fun trainImages() {
        val originalLocalImages =
            ImageHelper.getImagesFromExternalStorage(RisingApplication.appContext.contentResolver)
    }

    fun trainContacts() {

    }
}