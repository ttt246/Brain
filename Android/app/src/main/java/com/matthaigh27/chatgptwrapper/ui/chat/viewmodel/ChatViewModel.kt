package com.matthaigh27.chatgptwrapper.ui.chat.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.matthaigh27.chatgptwrapper.RisingApplication.Companion.appContext
import com.matthaigh27.chatgptwrapper.data.local.entity.ImageEntity
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.data.remote.requests.ImageRelatednessApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.NotificationApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainContactsApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainImageApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.data.remote.responses.EmptyResultApiResponse
import com.matthaigh27.chatgptwrapper.data.remote.responses.ImageRelatenessApiResponse
import com.matthaigh27.chatgptwrapper.data.repository.FirebaseRepository
import com.matthaigh27.chatgptwrapper.data.repository.RemoteRepository
import com.matthaigh27.chatgptwrapper.data.repository.RoomRepository
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ContactHelper.getChangedContacts
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ContactHelper.getContacts
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ImageHelper.getBytesFromPath
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ImageHelper.getImagesFromExternalStorage
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ImageHelper.getLocalPathFromUri
import com.matthaigh27.chatgptwrapper.utils.helpers.network.RequestFactory
import com.matthaigh27.chatgptwrapper.utils.helpers.network.RequestFactory.buildApiKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            message = message, confs = RequestFactory.buildApiKeys()
        )

        val resource: MutableLiveData<ApiResource<ApiResponse>> = MutableLiveData()
        resource.value = ApiResource.Loading()

        RemoteRepository.sendNotification(request, onSuccess = { apiResponse ->
            resource.value = ApiResource.Success(apiResponse)
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }

    fun downloadImageFromFirebase(name: String): MutableLiveData<ApiResource<ByteArray>> {
        val resource: MutableLiveData<ApiResource<ByteArray>> = MutableLiveData()
        resource.value = ApiResource.Loading()

        FirebaseRepository.downloadImageWithName(name, onSuccess = { apiResponse ->
            resource.value = ApiResource.Success(apiResponse)
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }

    fun uploadImageToFirebase(imageByteArray: ByteArray): MutableLiveData<ApiResource<String>> {
        val resource: MutableLiveData<ApiResource<String>> = MutableLiveData()
        resource.value = ApiResource.Loading()

        FirebaseRepository.uploadImageAsync(imageByteArray, onSuccess = { apiResponse ->
            resource.value = ApiResource.Success(apiResponse)
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }

    fun trainImages(): MutableLiveData<Boolean> {
        val state: MutableLiveData<Boolean> = MutableLiveData()
        state.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val images = getImagesFromExternalStorage(appContext.contentResolver)
            val originalImages = RoomRepository.getAllImages().value

            val existImageStatus = BooleanArray(originalImages!!.size) { false }
            val tasks = mutableListOf<Deferred<Unit>>()

            Log.d("Brain", "Start")
            images.forEach { image ->
                var isExist = false
                val path = getLocalPathFromUri(appContext, image.uri)
                for (i in originalImages.indices) {
                    val entity: ImageEntity = originalImages[i]
                    if (entity.path == path) {
                        if (entity.dataModified != image.modifiedDate) {
                            val byteArray = getBytesFromPath(path)
                            val task = async {
                                val uuid = FirebaseRepository.uploadImage(byteArray)
                                if (uuid != "Error") {
                                    RoomRepository.updateImage(
                                        ImageEntity(
                                            id = 0,
                                            path = path,
                                            name = uuid,
                                            dataModified = image.modifiedDate
                                        )
                                    )
                                    RemoteRepository.trainImage(
                                        TrainImageApiRequest(
                                            uuid, "updated", buildApiKeys()
                                        )
                                    )
                                }
                            }
                            tasks.add(task)
                        }
                        isExist = true
                        existImageStatus[i] = true
                        break
                    }
                }
                if (!isExist) {
                    path?.let {
                        val byteArray = getBytesFromPath(it)
                        val task = async {
                            val uuid = FirebaseRepository.uploadImage(byteArray)
                            if (uuid != "Error") {
                                RoomRepository.insertImage(
                                    ImageEntity(
                                        id = 0,
                                        path = path,
                                        name = uuid,
                                        dataModified = image.modifiedDate
                                    )
                                )
                                RemoteRepository.trainImage(
                                    TrainImageApiRequest(
                                        uuid, "created", buildApiKeys()
                                    )
                                )
                            }
                        }
                        tasks.add(task)
                    }
                }
            }

            for (i in existImageStatus.indices) {
                if (!existImageStatus[i]) {
                    val task = async {
                        RoomRepository.deleteImage(
                            ImageEntity(
                                originalImages[i].id, "", "", 0L
                            )
                        )
                        val result = RemoteRepository.trainImage(
                            TrainImageApiRequest(
                                originalImages[i].name, "deleted", buildApiKeys()
                            )
                        )
                    }
                    tasks.add(task)
                }
            }

            tasks.awaitAll()
            Log.d("Brain", "Finish")
            withContext(Dispatchers.Main) {
                state.value = false
            }
        }
        return state
    }

    fun trainContacts(): MutableLiveData<Boolean> {
        val state: MutableLiveData<Boolean> = MutableLiveData()
        state.value = true
        val contacts = getContacts(appContext)
        CoroutineScope(Dispatchers.Main).launch {
            val resource: MutableLiveData<ApiResource<EmptyResultApiResponse>> = MutableLiveData()
            val changedContacts = getChangedContacts(contacts)
            val request = TrainContactsApiRequest(changedContacts, RequestFactory.buildApiKeys())
            RemoteRepository.trainContacts(request, onSuccess = { apiResponse ->
                resource.value = ApiResource.Success(apiResponse)
            }, onFailure = { throwable ->
                resource.value = ApiResource.Error(throwable)
            })
            withContext(Dispatchers.Main) {
                state.value = false
            }
        }
        return state
    }

    fun getImageRelatedness(imageName: String, message: String): MutableLiveData<ApiResource<ImageRelatenessApiResponse>> {
        val resource: MutableLiveData<ApiResource<ImageRelatenessApiResponse>> = MutableLiveData()
        val request = ImageRelatednessApiRequest(
            image_name = imageName, message = message, confs = buildApiKeys()
        )
        resource.value = ApiResource.Loading()

        RemoteRepository.getImageRelatedness(request, onSuccess = { apiResponse ->
            resource.value = ApiResource.Success(apiResponse)
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }
}