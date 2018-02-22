package com.example.gonnokami.errorfeedbacksample.data.api

/**
 * 更新リクエストやエラー時のフィードバックに利用するメッセージ等を含むレスポンス
 *
 * @author gonnokami
 */
data class FeedbackResponse(val code: Int, val message: String)
