package com.shifen.game.jfcz.services

import android.content.Context
import com.shifen.game.jfcz.BuildConfig
import com.shifen.game.jfcz.JFCZApplication
import com.shifen.game.jfcz.model.LoginResult
import com.shifen.game.jfcz.model.Response
import com.shifen.game.jfcz.utils.*
import io.reactivex.Observable
import java.util.*

class LoginServiceImpl {

    fun login(context: Context): Observable<Response<LoginResult>> {
        val random = Random()
        val num = random.nextInt(999999)
        val imei = JFCZApplication.INSTANCE.DEVICE_ID
        val md5 = "deviceNum=$imei&random=$num&apiKey=${BuildConfig.API_KEY}".md5()
        return ServiceManager.create(LoginService::class.java).login(imei, num, md5)
    }

    fun saveLoginResult(context: Context, result: LoginResult) {
        context.putConfig { editor ->
            editor.putString(APP_TOKEN, result.token)
            editor.putLong(TOKEN_TIMEOUT, result.timeout)
            editor.putString(CONTAINER_ID, result.containerId)
        }
        ApiConfig.token = result.token
        ApiConfig.containerId = result.containerId
    }
}