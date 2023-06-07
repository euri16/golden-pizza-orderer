package dev.eury.goldenpizza.network.calladapter

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException

class NetworkResultCall<S : Any>(
    private val delegate: Call<S>
) : Call<NetworkResponse<S>> {

    override fun enqueue(callback: Callback<NetworkResponse<S>>) {
        return delegate.enqueue(object : Callback<S> {
            override fun onResponse(call: Call<S>, response: Response<S>) {
                val code = response.code()

                with(this@NetworkResultCall) {
                    when {
                        response.isSuccessful -> {
                            val body = response.body()

                            if (body == null) {
                                callback.onResponse(
                                    this,
                                    Response.success(NetworkResponse.ApiError(body = null, code))
                                )

                                return@with
                            }

                            callback.onResponse(
                                this,
                                Response.success(NetworkResponse.Success(body))
                            )
                        }

                        else -> {
                            callback.onResponse(
                                this,
                                Response.success(
                                    NetworkResponse.ApiError(response.errorBody(), code)
                                )
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<S>, throwable: Throwable) {
                val networkResponse = when (throwable) {
                    is UnknownHostException -> NetworkResponse.NetworkError(throwable)
                    else -> NetworkResponse.ApiError(body = null, throwable = throwable)
                }
                callback.onResponse(this@NetworkResultCall, Response.success(networkResponse))
            }
        })
    }

    override fun clone() = NetworkResultCall(delegate.clone())

    override fun execute(): Response<NetworkResponse<S>> {
        throw UnsupportedOperationException("NetworkResponseCall doesn't support execute")
    }

    override fun isExecuted() = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled() = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}
