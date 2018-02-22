package com.example.gonnokami.errorfeedbacksample.presentation.common

import android.content.Context
import android.support.annotation.StringRes
import com.example.gonnokami.errorfeedbacksample.R
import com.example.gonnokami.errorfeedbacksample.data.api.FeedbackResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * フィードバックを考慮したエラーを表現するクラス
 *
 * @author gonnokami
 */
sealed class ErrorFeedback {

    abstract fun getMessage(context: Context): String

    data class ApiErrorFeedback(val message: String, val code: Int) : ErrorFeedback() {
        override fun getMessage(context: Context): String = message
    }

    data class ApplicationErrorFeedback(@StringRes val resId: Int) : ErrorFeedback() {
        override fun getMessage(context: Context): String = context.getString(resId)
    }

    companion object {
        fun map(throwable: Throwable): ErrorFeedback {
            return when (throwable) {
                is UnknownHostException, is ConnectException -> ApplicationErrorFeedback(R.string.error_network)
                is SocketTimeoutException -> ApplicationErrorFeedback(R.string.error_timeout)
                is HttpException -> {
                    try {
                        throwable.response().errorBody()?.let {
                            ApiErrorFeedback(Gson().fromJson(it.string(), FeedbackResponse::class.java).message, throwable.code())
                        } ?: throw JsonSyntaxException(Throwable())
                    } catch (e: JsonSyntaxException) {
                        when (throwable.code()) {
                            404 -> ApplicationErrorFeedback(R.string.error_not_found)
                            406 -> ApplicationErrorFeedback(R.string.error_not_acceptable)
                            409 -> ApplicationErrorFeedback(R.string.error_conflict)
                            else -> ApplicationErrorFeedback(R.string.error_connection)
                        }
                    }
                }
                else -> ApplicationErrorFeedback(R.string.error_connection)
            }
        }
    }
}
