package com.matthaigh27.chatgptwrapper.data.remote

sealed class ApiResource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : ApiResource<T>(data)
    class Error<T>(message: String, data: T? = null) : ApiResource<T>(data, message)
    class Loading<T>(data: T? = null) : ApiResource<T>(data)
}