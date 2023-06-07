package dev.eury.goldenpizza.network.calladapter

import okhttp3.ResponseBody
import java.io.IOException

sealed class NetworkResponse<out T : Any> {
    data class Success<out T : Any>(val value: T) : NetworkResponse<T>()

    data class NetworkError(val error: IOException) : NetworkResponse<Nothing>(), BaseNetworkError {
        override val throwableOrDefault: Throwable = error
    }

    data class ApiError(
        val body: ResponseBody?,
        val code: Int? = null,
        val throwable: Throwable? = null
    ) : NetworkResponse<Nothing>(), BaseNetworkError {
        override val throwableOrDefault = throwable ?: Throwable("Api error: $code")
    }
}

sealed interface BaseNetworkError {
    val throwableOrDefault: Throwable
}