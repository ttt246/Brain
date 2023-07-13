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
import com.matthaigh27.chatgptwrapper.RisingApplication
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

class ChatViewModel(private val remoteRepository: RemoteRepository) : ViewModel() {

    /**
     * This function is used to fetch all help commands from Brain.
     */
    fun getAllHelpCommands(): MutableLiveData<ApiResource<ApiResponse<HelpCommandResult>>> {
        val resource: MutableLiveData<ApiResource<ApiResponse<HelpCommandResult>>> =
            MutableLiveData()
        resource.value = ApiResource.Loading()

        remoteRepository.getAllHelpCommands(onSuccess = { apiResponse ->
            resource.value = ApiResource.Success(apiResponse)
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }

    /**
     * This function is used to send user's message to Brain to analyze the user's query.
     */
    fun sendNotification(message: String): MutableLiveData<ApiResource<ApiResponse<CommonResult>>> {
        val request = NotificationApiRequest(
            message = message, confs = remoteRepository.getKeys()
        )

        val resource: MutableLiveData<ApiResource<ApiResponse<CommonResult>>> = MutableLiveData()
        resource.value = ApiResource.Loading()

        remoteRepository.sendNotification(request, onSuccess = { apiResponse ->
            resource.value = ApiResource.Success(apiResponse)
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }

    /**
     * This function is used to download image from Firebase Storage.
     *
     * @param name An image name that is stored in Firebase storage
     */
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

    /**
     * This function is used to upload image to Firebase storage and return uuid of uploaded image name.
     *
     * @param imageByteArray A bytearray of image to upload
     * @return A uuid that is generated to keep unique when the image is uploaded.
     */
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

    /**
     * This function is used to train changed images in user's mobile local storage.
     */
    fun trainImages(): MutableLiveData<ApiResource<Int>> {
        val state: MutableLiveData<ApiResource<Int>> = MutableLiveData()
        state.value = ApiResource.Loading()
        CoroutineScope(Dispatchers.IO).launch {
            /**
             * Get images from external storage
             */
            val images = getImagesFromExternalStorage(appContext.contentResolver)

            /**
             * Get images from room database, in which previous images are stores so we can find changed
             * images by comparing the two image array data.
             */
            val originalImages = RoomRepository.getAllImages().value

            val existImageStatus = BooleanArray(originalImages!!.size) { false }
            val tasks = mutableListOf<Deferred<Unit>>()

            images.forEach { image ->
                var isExist = false
                val path = getLocalPathFromUri(appContext, image.uri)
                for (i in originalImages.indices) {
                    val entity: ImageEntity = originalImages[i]
                    if (entity.path == path) {
                        /**
                         * If path of images is same and modified date of images is different,
                         * update the image in Room database and send update request to Brain.
                         */
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
                                    remoteRepository.trainImage(
                                        TrainImageApiRequest(
                                            uuid, "updated", remoteRepository.getKeys()
                                        )
                                    )
                                }
                            }
                            tasks.add(task)
                        }
                        isExist = true
                        /**
                         * Indexes of existed images are stored in below BooleanArray variable so that
                         * after this loop, it is possible to search for new images that created in local storage.
                         */
                        existImageStatus[i] = true
                        break
                    }
                }

                /**
                 * New images are inserted into Room database and send create request to Brain.
                 */
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
                                remoteRepository.trainImage(
                                    TrainImageApiRequest(
                                        uuid, "created", remoteRepository.getKeys()
                                    )
                                )
                            }
                        }
                        tasks.add(task)
                    }
                }
            }

            /**
             * Images that doesn't exist in existImageStatus BooleanArray are deleted from database.
             */
            for (i in existImageStatus.indices) {
                if (!existImageStatus[i]) {
                    val task = async {
                        RoomRepository.deleteImage(
                            ImageEntity(
                                originalImages[i].id, "", "", 0L
                            )
                        )
                        val result = remoteRepository.trainImage(
                            TrainImageApiRequest(
                                originalImages[i].name, "deleted", remoteRepository.getKeys()
                            )
                        )
                    }
                    tasks.add(task)
                }
            }

            tasks.awaitAll()
            withContext(Dispatchers.Main) {
                state.value = ApiResource.Success(0)
            }
        }
        return state
    }

    /**
     * This function is used to train changed contacts.
     */
    fun trainContacts(): MutableLiveData<ApiResource<ApiResponse<String>>> {
        val state: MutableLiveData<ApiResource<ApiResponse<String>>> = MutableLiveData()
        state.value = ApiResource.Loading()
        /**
         * Get current contacts from user's phone
         */
        val contacts = getContacts(appContext)
        CoroutineScope(Dispatchers.Main).launch {
            val resource: MutableLiveData<Boolean> = MutableLiveData()

            /**
             * Get changed contacts
             */
            val changedContacts = getChangedContacts(contacts)

            /**
             * Send request to Server
             *
             */
            val request = TrainContactsApiRequest(changedContacts, remoteRepository.getKeys())
            withContext(Dispatchers.Main) {
                remoteRepository.trainContacts(request, onSuccess = { apiResponse ->
                    state.value = ApiResource.Success(apiResponse)
                }, onFailure = { throwable ->
                    state.value = ApiResource.Error(throwable)
                })
            }
        }
        return state
    }

    /**
     * This function is used to get similar image to one that a user uploaded.
     */
    fun getImageRelatedness(
        imageName: String,
        message: String
    ): MutableLiveData<ApiResource<ImageRelatenessModel>> {
        val resource: MutableLiveData<ApiResource<ImageRelatenessModel>> =
            MutableLiveData()
        val request = ImageRelatednessApiRequest(
            image_name = imageName, message = message, confs = remoteRepository.getKeys()
        )
        resource.value = ApiResource.Loading()

        /**
         * Get the uuid of the similar image
         */
        remoteRepository.getImageRelatedness(request, onSuccess = { apiResponse ->
            val resultImageName = apiResponse.result.content.image_name
            val resultImageDesc = apiResponse.result.content.image_desc

            /**
             * With the uuid of the image, download the image data from Firebase Storage
             */
            FirebaseRepository.downloadImageWithName(
                name = resultImageName,
                onSuccess = { response ->
                    resource.value =
                        ApiResource.Success(ImageRelatenessModel(response, resultImageDesc))
                },
                onFailure = { throwable ->
                    resource.value = ApiResource.Error(throwable)
                }
            )
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }

    /**
     * This function is used to read mails
     */
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
            ), confs = remoteRepository.getKeys()
        )
        resource.value = ApiResource.Loading()

        remoteRepository.readEmails(request, onSuccess = { apiResponse ->
            resource.value =
                ApiResource.Success(apiResponse.result)
        }, onFailure = { throwable ->
            resource.value = ApiResource.Error(throwable)
        })

        return resource
    }

    /**
     * this function is used to send mails
     */
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
            ), confs = remoteRepository.getKeys()
        )
        resource.value = ApiResource.Loading()

        remoteRepository.sendEmail(request, onSuccess = { apiResponse ->
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