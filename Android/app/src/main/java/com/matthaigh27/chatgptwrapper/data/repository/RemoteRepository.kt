package com.matthaigh27.chatgptwrapper.data.repository

import com.matthaigh27.chatgptwrapper.RisingApplication.Companion.appContext
import com.matthaigh27.chatgptwrapper.data.models.chat.MailModel
import com.matthaigh27.chatgptwrapper.data.models.setting.SettingModel
import com.matthaigh27.chatgptwrapper.data.remote.ApiClient
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.data.remote.requests.BaseApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.ComposeMailApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.ImageRelatednessApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.NotificationApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.ReadMailApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainContactsApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.TrainImageApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.common.Keys
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.CommonResult
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.HelpCommandResult
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.ImageRelatenessResult
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.TrainImageResult
import com.matthaigh27.chatgptwrapper.utils.helpers.OnFailure
import com.matthaigh27.chatgptwrapper.utils.helpers.OnSuccess
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.SettingHelper.emptySettingModel
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object RemoteRepository {
    /**
     * This function is used to get keys to manage backend.
     */
    fun getKeys(): Keys{
        RoomRepository
        val settingModel = SharedPreferencesRepository.getConfig()
        val keys  = Keys(
            uuid = appContext.getUUID(),
            token = appContext.getFCMToken(),
            openai_key = settingModel.openaiKey,
            pinecone_env = settingModel.pineconeEnv,
            pinecone_key = settingModel.pineconeKey,
            firebase_key = settingModel.firebaseKey,
            settings = settingModel.setting
        )
        return keys
    }

    fun getAllHelpCommands(
        onSuccess: OnSuccess<ApiResponse<HelpCommandResult>>,
        onFailure: OnFailure<String>
    ) {
        val call = ApiClient.instance.apiService.getAllHelpCommands(BaseApiRequest(getKeys()))

        call.enqueue(object : Callback<ApiResponse<HelpCommandResult>> {
            override fun onResponse(call: Call<ApiResponse<HelpCommandResult>>, response: Response<ApiResponse<HelpCommandResult>>) {
                response.body()?.let { data ->
                    onSuccess(data)
                } ?: run {
                    onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<HelpCommandResult>>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    fun sendNotification(
        request: NotificationApiRequest,
        onSuccess: OnSuccess<ApiResponse<CommonResult>>,
        onFailure: OnFailure<String>
    ) {
        val call = ApiClient.instance.apiService.sendNotification(request)

        call.enqueue(object : Callback<ApiResponse<CommonResult>> {
            override fun onResponse(call: Call<ApiResponse<CommonResult>>, response: Response<ApiResponse<CommonResult>>) {
                response.body()?.let { data ->
                    onSuccess(data)
                } ?: run {
                    onFailure(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<ApiResponse<CommonResult>>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    fun trainContacts(
        request: TrainContactsApiRequest,
        onSuccess: OnSuccess<ApiResponse<String>>,
        onFailure: OnFailure<String>
    ) {
        val call = ApiClient.instance.apiService.trainContacts(request)

        call.enqueue(object : Callback<ApiResponse<String>> {
            override fun onResponse(
                call: Call<ApiResponse<String>>, response: Response<ApiResponse<String>>
            ) {
                response.body()?.let { data ->
                    onSuccess(data)
                } ?: run {
                    onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    suspend fun trainImage(request: TrainImageApiRequest) : ApiResource<ApiResponse<TrainImageResult>> = suspendCoroutine { continuation ->

        val call = ApiClient.instance.apiService.trainImage(request)

        call.enqueue(object : Callback<ApiResponse<TrainImageResult>> {
            override fun onResponse(
                call: Call<ApiResponse<TrainImageResult>>, response: Response<ApiResponse<TrainImageResult>>
            ) {
                response.body()?.let { data ->
                    continuation.resume(ApiResource.Success(data))
                } ?: run {
                    continuation.resume(ApiResource.Error("Error"))
                }
            }

            override fun onFailure(call: Call<ApiResponse<TrainImageResult>>, t: Throwable) {
                continuation.resume(ApiResource.Error(t.message.toString()))
            }
        })
    }

    fun getImageRelatedness(
        request: ImageRelatednessApiRequest,
        onSuccess: OnSuccess<ApiResponse<ImageRelatenessResult>>,
        onFailure: OnFailure<String>
    ) {
        val call = ApiClient.instance.apiService.getImageRelatedness(request)

        call.enqueue(object : Callback<ApiResponse<ImageRelatenessResult>> {
            override fun onResponse(
                call: Call<ApiResponse<ImageRelatenessResult>>, response: Response<ApiResponse<ImageRelatenessResult>>
            ) {
                response.body()?.let { data ->
                    onSuccess(data)
                } ?: run {
                    onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<ImageRelatenessResult>>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    fun readEmails(
        request: ReadMailApiRequest,
        onSuccess: OnSuccess<ApiResponse<ArrayList<MailModel>>>,
        onFailure: OnFailure<String>
    ) {
        val call = ApiClient.instance.apiService.readEmails(request)

        call.enqueue(object : Callback<ApiResponse<ArrayList<MailModel>>> {
            override fun onResponse(
                call: Call<ApiResponse<ArrayList<MailModel>>>, response: Response<ApiResponse<ArrayList<MailModel>>>
            ) {
                response.body()?.let { data ->
                    onSuccess(data)
                } ?: run {
                    onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<ArrayList<MailModel>>>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    fun sendEmail(
        request: ComposeMailApiRequest,
        onSuccess: OnSuccess<ApiResponse<String>>,
        onFailure: OnFailure<String>
    ) {
        val call = ApiClient.instance.apiService.sendEmail(request)

        call.enqueue(object : Callback<ApiResponse<String>> {
            override fun onResponse(
                call: Call<ApiResponse<String>>, response: Response<ApiResponse<String>>
            ) {
                response.body()?.let { data ->
                    onSuccess(data)
                } ?: run {
                    onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }
}