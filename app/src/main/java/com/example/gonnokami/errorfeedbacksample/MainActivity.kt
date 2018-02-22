package com.example.gonnokami.errorfeedbacksample

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.example.gonnokami.errorfeedbacksample.data.api.FeedbackResponse
import com.example.gonnokami.errorfeedbacksample.presentation.common.ErrorFeedback
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val client = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("http://example.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val sampleService = retrofit.create(SampleService::class.java)
        sampleService.sample().enqueue(object : Callback<FeedbackResponse> {
            override fun onResponse(call: Call<FeedbackResponse>, response: Response<FeedbackResponse>) {
                if (response.isSuccessful) {
                    mainTextView.text = response.body()?.message ?: "OK"
                } else {
                    snackbar(ErrorFeedback.map(HttpException(response))).show()
                }
            }

            override fun onFailure(call: Call<FeedbackResponse>, t: Throwable) {
                snackbar(ErrorFeedback.map(t)).show()
            }
        })
    }

    interface SampleService {
        @GET("sample")
        fun sample(): Call<FeedbackResponse>
    }
}

fun Activity.snackbar(errorFeedback: ErrorFeedback, duration: Int = Snackbar.LENGTH_LONG): Snackbar =
        Snackbar.make(findViewById(android.R.id.content), errorFeedback.getMessage(this), duration)
