package com.shifen.game.jfcz.services

import android.content.Intent
import android.util.Log
import com.shifen.game.jfcz.BuildConfig
import com.shifen.game.jfcz.JFCZApplication
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.*
import com.shifen.game.jfcz.ui.ErrorDialog
import com.shifen.game.jfcz.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import retrofit2.http.*

fun <T> Observable<Response<T>>.observeOnMain(observerAdapter: ObserverAdapter<Response<T>>) {

    wrapLogin().observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .subscribe(observerAdapter)
}

fun <T> Observable<Response<T>>.wrapLogin(): Observable<Response<T>> {
    if (JFCZApplication.INSTANCE.getConfig().getLong(TOKEN_TIMEOUT, 0L) - System.currentTimeMillis() / 1000 <= 24 * 60 * 60) {
        Log.i("wrapLogin", "token过期，需要重新登录!")
        val loginServiceImpl = LoginServiceImpl()
        fun showErrorDialog() {
            JFCZApplication.INSTANCE.putConfig {
                it.remove(TOKEN_TIMEOUT)
                it.remove(APP_TOKEN)
                ApiConfig.containerId = ""
                ApiConfig.token = ""
            }
            val tips = JFCZApplication.INSTANCE.getString(R.string.login_error)
            JFCZApplication.INSTANCE.startActivity(Intent(JFCZApplication.INSTANCE, ErrorDialog::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(ErrorDialog.TIPS_KEY, tips)
            })
        }
        return loginServiceImpl.login(JFCZApplication.INSTANCE)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnError {
                    showErrorDialog()
                }
                .flatMap {
                    if (BuildConfig.DEBUG) {
                        Log.d("Observable", "login response: $it")
                    }
                    if (it.code == 0) {
                        loginServiceImpl.saveLoginResult(JFCZApplication.INSTANCE, it.data)
                        return@flatMap this
                    } else {
                        throw Throwable("login error")
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    if (it.message == "login error") {
                        showErrorDialog()
                    }
                }
    }
    return this
}

fun <T> Observable<Response<T>>.observeOnMain(onCompile: (() -> Unit)? = null, onError: ((Throwable) -> Unit)? = null, onNext: ((Response<T>) -> Unit)? = null) {
    observeOnMain(object : ObserverAdapter<Response<T>>() {
        override fun onComplete() {
            super.onComplete()
            onCompile?.invoke()
        }

        override fun onError(e: Throwable) {
            super.onError(e)
            onError?.invoke(e)
        }

        override fun onNext(t: Response<T>) {
            Log.d("JFCZApplication", "res: $t")
            try {
                if (t.code == 0) {
                    onNext?.invoke(t)
                } else {
                    //onError(Throwable(t.message))
                }
            } catch (e: Exception) {
                //onError(e)
            }
        }
    })
}

abstract class ObserverAdapter<T> : DisposableObserver<T>() {

    override fun onStart() {
        if (!isNetworkAvailable(JFCZApplication.INSTANCE)) {
            val tips = JFCZApplication.INSTANCE.getString(R.string.network_invalid)
            JFCZApplication.INSTANCE.startActivity(Intent(JFCZApplication.INSTANCE, ErrorDialog::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(ErrorDialog.TIPS_KEY, tips)
            })
            dispose()
            return
        }
    }

    override fun onComplete() {
        dispose()
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        dispose()
    }
}

interface LoginService {

    /**
     *
     * @param deviceNum ") deviceNum: String
     * @param random ") random: Int
     * @param sign ") sign: String
     * @return Observable<Response<LoginResult>>
     */
    @GET("/dev/auto/login")
    fun login(@Query("deviceNum") deviceNum: String, @Query("random") random: Int, @Query("sign") sign: String): Observable<Response<LoginResult>>
}

interface BannerService {

    @GET("/sale/banner/list")
    fun getBannerList(): Observable<Response<List<Banner>>>
}

interface GiftService {

    @GET("/sale/grid/list")
    fun getGiftList(): Observable<Response<List<Gift>>>
}

interface PayService {

    @POST("/sale/pay-order/status")
    fun payOrderStatus(@Body requestBody: RequestBody): Observable<Response<OrderStatus>>
}

interface PushService {

    @POST("/push/device/bind?apiKey=${BuildConfig.API_KEY}")
    fun bind(@Body requestBody: RequestBody): Observable<Response<String>>
}

interface GoodsService {

    @POST("/sale/game-pass/update/goods-lib")
    fun updateGoods(@Body requestBody: RequestBody): Observable<Response<String>>
}

interface ConfigService {

    @GET("/sale/config")
    fun getConfig(): Observable<Response<Config>>
}

interface GameService {

    @GET("/sale/game-config")
    fun getGameConfig(): Observable<Response<List<GameConfig>>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/sale/new/game")
    fun newGameStatus(@Body json: RequestBody): Observable<Response<String>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/sale/update/game-status")
    fun updateGameStatus(@Body json: RequestBody): Observable<Response<String>>

}

interface OperateService {

    // 柜子锁操作
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/sale/operate/grid")
    fun operateStatus(@Body json: RequestBody): Observable<Response<String>>

    // 柜子锁操作
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/sale/update/grids/status")
    fun operateStatusAll(@Body json: RequestBody): Observable<Response<String>>


}

