package com.matthaigh27.chatgptwrapper.ui.chat.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.matthaigh27.chatgptwrapper.RisingApplication
import com.matthaigh27.chatgptwrapper.RisingApplication.Companion.appContext
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.data.remote.requests.NotificationApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainContactsApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.data.remote.responses.EmptyResultApiResponse
import com.matthaigh27.chatgptwrapper.data.repository.FirebaseRepository
import com.matthaigh27.chatgptwrapper.data.repository.RemoteRepository
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ContactHelper.getChangedContacts
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ContactHelper.getContacts
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ImageHelper
import com.matthaigh27.chatgptwrapper.utils.helpers.network.RequestFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    fun uploadImageToFirebase(imageByteArray: ByteArray): MutableLiveData<ApiResource<String>> {
        val resource: MutableLiveData<ApiResource<String>> = MutableLiveData()
        resource.value = ApiResource.Loading()
        FirebaseRepository.uploadImage(
            imageByteArray,
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
        val contacts = getContacts(appContext)
        CoroutineScope(Dispatchers.Main).launch {
            val resource: MutableLiveData<ApiResource<EmptyResultApiResponse>> = MutableLiveData()
            val changedContacts = getChangedContacts(contacts)
            val request = TrainContactsApiRequest(changedContacts, RequestFactory.buildApiKeys())
            RemoteRepository.trainContacts(
                request,
                onSuccess = { apiResponse ->
                    resource.value = ApiResource.Success(apiResponse)
                },
                onFailure = { throwable ->
                    resource.value = ApiResource.Error(throwable)
                }
            )
        }

    }
}