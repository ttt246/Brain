package com.matthaigh27.chatgptwrapper.ui.chat.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.matthaigh27.chatgptwrapper.RisingApplication.Companion.appContext
import com.matthaigh27.chatgptwrapper.data.local.entity.ImageEntity
import com.matthaigh27.chatgptwrapper.data.models.chat.AutoTaskModel
import com.matthaigh27.chatgptwrapper.data.models.chat.ImageRelatenessModel
import com.matthaigh27.chatgptwrapper.data.models.chat.MailModel
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.data.remote.requests.ComposeMailApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.ComposeMailData
import com.matthaigh27.chatgptwrapper.data.remote.requests.ImageRelatednessApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.NotificationApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.ReadMailApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.ReadMailData
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainContactsApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainImageApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.CommonResult
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.HelpCommandResult
import com.matthaigh27.chatgptwrapper.data.repository.FirebaseRepository
import com.matthaigh27.chatgptwrapper.data.repository.RemoteRepository
import com.matthaigh27.chatgptwrapper.data.repository.RoomRepository
import com.matthaigh27.chatgptwrapper.utils.constants.CommonConstants.FIREBASE_DATABASE_URL
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ContactHelper.getChangedContacts
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ContactHelper.getContacts
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ImageHelper.getBytesFromPath
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ImageHelper.getImagesFromExternalStorage
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ImageHelper.getLocalPathFromUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel : ViewModel() {

    fun getAllHelpCommands(): MutableLiveData<ApiResource<ApiResponse<HelpCommandResult>>> {
        val resource: MutableLiveData<ApiResource<ApiResponse<HelpCommandResult>>> =
            MutableLiveData()
        resource.value = ApiResource.Loading()

        RemoteRepository.getAllHelpCommands(onSuccess = { apiResponse ->
            resource.value = ApiResource.Success(apiResponse)
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }

    fun sendNotification(message: String): MutableLiveData<ApiResource<ApiResponse<CommonResult>>> {
        val request = NotificationApiRequest(
            message = message, confs = RemoteRepository.getKeys()
        )

        val resource: MutableLiveData<ApiResource<ApiResponse<CommonResult>>> = MutableLiveData()
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

    fun trainImages(): MutableLiveData<ApiResource<Int>> {
        val state: MutableLiveData<ApiResource<Int>> = MutableLiveData()
        state.value = ApiResource.Loading()
        CoroutineScope(Dispatchers.IO).launch {
            val images = getImagesFromExternalStorage(appContext.contentResolver)
            val originalImages = RoomRepository.getAllImages().value

            val existImageStatus = BooleanArray(originalImages!!.size) { false }
            val tasks = mutableListOf<Deferred<Unit>>()

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
                                            uuid, "updated", RemoteRepository.getKeys()
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
                                        uuid, "created", RemoteRepository.getKeys()
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
                                originalImages[i].name, "deleted", RemoteRepository.getKeys()
                            )
                        )
                    }
                    tasks.add(task)
                }
            }

            tasks.awaitAll()
            Log.d("Brain", "Finish")
            withContext(Dispatchers.Main) {
                state.value = ApiResource.Success(0)
            }
        }
        return state
    }

    fun trainContacts(): MutableLiveData<ApiResource<ApiResponse<String>>> {
        val state: MutableLiveData<ApiResource<ApiResponse<String>>> = MutableLiveData()
        state.value = ApiResource.Loading()
        val contacts = getContacts(appContext)
        CoroutineScope(Dispatchers.Main).launch {
            val resource: MutableLiveData<Boolean> = MutableLiveData()
            val changedContacts = getChangedContacts(contacts)
            val request = TrainContactsApiRequest(changedContacts, RemoteRepository.getKeys())
            withContext(Dispatchers.Main) {
                RemoteRepository.trainContacts(request, onSuccess = { apiResponse ->
                    state.value = ApiResource.Success(apiResponse)
                }, onFailure = { throwable ->
                    state.value = ApiResource.Error(throwable)
                })
            }
        }
        return state
    }

    fun getImageRelatedness(
        imageName: String,
        message: String
    ): MutableLiveData<ApiResource<ImageRelatenessModel>> {
        val resource: MutableLiveData<ApiResource<ImageRelatenessModel>> =
            MutableLiveData()
        val request = ImageRelatednessApiRequest(
            image_name = imageName, message = message, confs = RemoteRepository.getKeys()
        )
        resource.value = ApiResource.Loading()

        RemoteRepository.getImageRelatedness(request, onSuccess = { apiResponse ->
            val resultImageName = apiResponse.result.content.image_name
            val resultImageDesc = apiResponse.result.content.image_desc

            FirebaseRepository.downloadImageWithName(
                name = resultImageName,
                onSuccess = { response ->
                    resource.value =
                        ApiResource.Success(ImageRelatenessModel(response, resultImageDesc))
                }, onFailure = { throwable ->
                    resource.value = ApiResource.Error(throwable)
                }
            )
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }

    fun readMails(
        from: String,
        password: String,
        imapFolder: String
    ): MutableLiveData<ApiResource<ArrayList<MailModel>>> {
        val resource: MutableLiveData<ApiResource<ArrayList<MailModel>>> =
            MutableLiveData()
        val request = ReadMailApiRequest(
            data = ReadMailData(
                sender = from,
                pwd = password,
                imap_folder = imapFolder
            ), confs = RemoteRepository.getKeys()
        )
        resource.value = ApiResource.Loading()

        RemoteRepository.readEmails(request, onSuccess = { apiResponse ->
            resource.value =
                ApiResource.Success(apiResponse.result)
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }

    fun sendMail(
        sender: String,
        pwd: String,
        to: String,
        subject: String,
        body: String,
        to_send: Boolean,
        filename: String,
        file_content: String
    ): MutableLiveData<ApiResource<String>> {
        val resource: MutableLiveData<ApiResource<String>> =
            MutableLiveData()
        val request = ComposeMailApiRequest(
            data = ComposeMailData(
                sender = sender,
                pwd = pwd,
                to = to,
                subject = subject,
                body = body,
                to_send = to_send,
                filename = filename,
                file_content = file_content
            ), confs = RemoteRepository.getKeys()
        )
        resource.value = ApiResource.Loading()

        RemoteRepository.sendEmail(request, onSuccess = { apiResponse ->
            resource.value =
                ApiResource.Success(apiResponse.result)
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }

    /**
     * This function is used to retrieve real-time data for the auto task.
     * Whenever data in the Firebase real-time database changes,
     * the data is converted to the AutoTaskModel and sent back to the View.
     */
    fun getAutoTaskRealtimeData(referencePath: String): MutableLiveData<ApiResource<AutoTaskModel>> {
        val resource: MutableLiveData<ApiResource<AutoTaskModel>> =
            MutableLiveData()
        val database = Firebase.database(FIREBASE_DATABASE_URL)
        resource.value = ApiResource.Loading()
        database.getReference(referencePath).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()) {
                    try {
                        val data = snapshot.getValue<AutoTaskModel>()
                        data?.let {
                            data.command?.let { command ->
                                if (command.name == "finish") {
                                    resource.value = ApiResource.Success(data)
                                } else {
                                    resource.value = ApiResource.Loading(data)
                                }
                            }
                            data.result?.let {
                                resource.value = ApiResource.Loading(data)
                            }

                        }
                    } catch (exception: Exception) {
                        resource.value = ApiResource.Error(exception.toString())
                    }
                }
            }

            override fun onChildChanged(
                snapshot: DataSnapshot, previousChildName: String?
            ) {
                if (snapshot.exists()) {
                    try {
                        val data = snapshot.getValue<AutoTaskModel>()
                        data?.let {
                            data.command?.let { command ->
                                if (command.name == "finish") {
                                    resource.value = ApiResource.Success(data)
                                } else {
                                    resource.value = ApiResource.Loading(data)
                                }
                            }
                            data.result?.let {
                                resource.value = ApiResource.Loading(data)
                            }
                        }
                    } catch (exception: Exception) {
                        resource.value = ApiResource.Error(exception.toString())
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                resource.value = ApiResource.Error(error.toException().toString())
            }
        })
        return resource
    }
}