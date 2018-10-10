package com.shifen.game.jfcz.services

import android.util.Log
import com.shifen.game.jfcz.utils.ApiConfig
import com.shifen.game.jfcz.utils.ApiConfig.BASE_URL
import com.shifen.game.jfcz.utils.ApiConfig.CONNECT_TIMEOUT
import com.shifen.game.jfcz.utils.ApiConfig.READ_TIMEOUT
import com.shifen.game.jfcz.utils.ApiConfig.WRITE_TIMEOUT
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceManager {

    private val retrofit: Retrofit

    init {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor {
                    val originalRequest = it.request()

                    val requestBuilder = originalRequest.newBuilder()
                            .addHeader("Content-Type", "application/json; charset=utf-8")

                    val paths = originalRequest.url().pathSegments()
                    if (paths[paths.size - 1] != "login") {
                        val url = originalRequest.url().newBuilder()
                                .addQueryParameter("token", ApiConfig.token)
                                .addQueryParameter("containerId", ApiConfig.containerId)
                        requestBuilder.url(url.build())
                    }

                    val request = requestBuilder.build()
                    return@addInterceptor it.proceed(request)
                }

        retrofit = Retrofit.Builder().client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
    }

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}