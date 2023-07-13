package com.matthaigh27.chatgptwrapper.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.matthaigh27.chatgptwrapper.data.remote.ApiService
import com.matthaigh27.chatgptwrapper.data.remote.requests.BaseApiRequest
import com.matthaigh27.chatgptwrapper.data.remote.requests.common.Keys
import com.matthaigh27.chatgptwrapper.data.remote.requests.common.OpenAISetting
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.HelpCommandResult
import com.matthaigh27.chatgptwrapper.data.repository.RemoteRepository
import com.matthaigh27.chatgptwrapper.data.repository.RoomRepository
import com.matthaigh27.chatgptwrapper.data.repository.SharedPreferencesRepository
import com.matthaigh27.chatgptwrapper.utils.helpers.OnFailure
import com.matthaigh27.chatgptwrapper.utils.helpers.OnSuccess
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import retrofit2.mock.Calls

@RunWith(MockitoJUnitRunner::class)
class RemoteRepositoryTest {
    private val testUUID = "956a11be45cba4a4"
    private val testToken = "cI3EvimJQv-G5imdWrBprf:APA91bEZ5u6uq9Yq4a6NglN0L9pVM7p-rlxKB_FikbfKlzHnZT5GeAjxF0deuPT2GurS8bK6JTE2XPZLQqbsrtjxeRGhGOH5INoQ7MrRlr4TR3xFswKxJSkfi1aBUWDaLGALeirZ7GuZ"

    @Mock
    private lateinit var testKey: Keys

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var onFailure: OnFailure<String>

    @Mock
    private lateinit var    onSuccess: OnSuccess<ApiResponse<HelpCommandResult>>

    @Mock
    private lateinit var apiRequest: BaseApiRequest

    @Before
    fun setup() {
        testKey = Keys(
            uuid = testUUID,
            token = testToken,
            pinecone_key = "",
            pinecone_env = "",
            firebase_key = "",
            openai_key = "",
            settings = OpenAISetting(
                temperature = 0.6f
            )
        )

        apiRequest = BaseApiRequest(testKey)
    }

    @Test
    fun `test getAllHelpCommands success`() {
        val helpCommandResult = HelpCommandResult("test_help_command", arrayListOf())
        val apiResponse = ApiResponse(200, emptyList(), helpCommandResult)
        val response = Response.success(apiResponse)
        Mockito.`when`(apiService.getAllHelpCommands(apiRequest)).thenReturn(Calls.response(response))
        Mockito.`when`(RemoteRepository.getKeys()).thenReturn(testKey)

        RemoteRepository.getAllHelpCommands(onSuccess, onFailure)

        verify {
            onSuccess.invoke(any())
        }

    }

//    @Test
//    fun `test getAllHelpCommands failure`() {
//        val exception = RuntimeException("Test exception")
//        val call: Call<ApiResponse<HelpCommandResult>> = Calls.failure(exception)
//
//        Mockito.`when`(apiService.getAllHelpCommands(any())).thenReturn(call)
//
//        RoomRepository.getAllHelpCommands(onSuccess, onFailure)
//
//        verify(onSuccess, never()).invoke(any())
//        verify(onFailure).invoke(exception.message ?: "")
//    }
}