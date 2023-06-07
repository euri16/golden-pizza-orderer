package dev.eury.goldenpizza.core.data.utils

import dev.eury.goldenpizza.network.calladapter.NetworkResponse
import okhttp3.ResponseBody

sealed class ResultOperation<out T : Any?> {
    data class Success<T : Any?>(val value: T) : ResultOperation<T>()

    data class Error(
        val body: ResponseBody?,
        val throwable: Throwable?,
        val code: Int? = null,
        val isNetworkError: Boolean = false
    ) : ResultOperation<Nothing>()

    val optValue: T?
        get() = (this as? Success)?.value

    fun <R : Any> map(mapper: (T) -> R) = when (this) {
        is Error -> this
        is Success -> Success(mapper(value))
    }

    companion object {
        fun <T : Any> T.wrapSuccess() = Success(this)

        fun <T : Any> T?.wrapNullable(): ResultOperation<T> {
            return this?.let { Success(this) } ?: Error(
                body = null,
                throwable = IllegalStateException("value is null when wrapping"),
            )
        }

        val Error.isServerDown
            get() = (code ?: 0) >= 500
    }
}

internal fun <T : Any> NetworkResponse<T>.asOperation(): ResultOperation<T> = when (this) {
    is NetworkResponse.ApiError -> ResultOperation.Error(
        body = body,
        throwable = throwable,
        code = code
    )

    is NetworkResponse.NetworkError -> ResultOperation.Error(
        body = null,
        throwable = error,
        isNetworkError = true
    )

    is NetworkResponse.Success -> ResultOperation.Success(value)
}
