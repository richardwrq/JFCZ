package com.shifen.game.jfcz.services

import com.shifen.game.jfcz.utils.BASE_URL
import com.shifen.game.jfcz.utils.CONNECT_TIMEOUT
import com.shifen.game.jfcz.utils.READ_TIMEOUT
import com.shifen.game.jfcz.utils.WRITE_TIMEOUT
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