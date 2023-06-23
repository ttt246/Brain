package com.matthaigh27.chatgptwrapper.ui.chat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.matthaigh27.chatgptwrapper.RisingApplication
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.data.remote.requests.NotificationApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.data.repository.FirebaseRepository
import com.matthaigh27.chatgptwrapper.data.repository.RemoteRepository
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ImageHelper
import com.matthaigh27.chatgptwrapper.utils.helpers.network.RequestFactory

class ChatViewModel : ViewModel() {

    fun getAllHelpCommands(): MutableLiveData<ApiResource<ApiResponse>> {
        val resource: MutableLiveData<ApiResource<ApiResponse>> = MutableLiveData()
        resource.value = ApiResource.Loading()

        RemoteRepository.getAllHelpCommands(onSuccess = { apiResponse ->
            resource.value = ApiResource.Success(apiResponse)
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }

    fun sendNotification(message: String): MutableLiveData<ApiResource<ApiResponse>> {
        val request = NotificationApiRequest(
            message = message,
            confs = RequestFactory.buildApiKeys()
        )

        val resource: MutableLiveData<ApiResource<ApiResponse>> = MutableLiveData()
        resource.value = ApiResource.Loading()

        RemoteRepository.sendNotification(
            request,
            onSuccess = { apiResponse ->
                resource.value = ApiResource.Success(apiResponse)
            },
            onFailure = { throwable ->
                resource.value = ApiResource.Error(throwable)
            }
        )

        return resource
    }

    fun downloadImageFromFirebase(name: String): MutableLiveData<ApiResource<ByteArray>> {
        val resource: MutableLiveData<ApiResource<ByteArray>> = MutableLiveData()
        resource.value = ApiResource.Loading()

        FirebaseRepository.downloadImageWithName(
            name,
            onSuccess = { apiResponse ->
                resource.value = ApiResource.Success(apiResponse)
            },
            onFailure = { throwable ->
                resource.value = ApiResource.Error(throwable)
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