package com.shifen.game.jfcz.services

import android.content.Intent
import com.shifen.game.jfcz.JFCZApplication
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.LoginResult
import com.shifen.game.jfcz.model.Response
import com.shifen.game.jfcz.ui.ErrorDialog
import com.shifen.game.jfcz.utils.isNetworkAvailable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.http.GET
import retrofit2.http.Query

fun <T> Observable<T>.observeOnMain(observerAdapter: ObserverAdapter<T>) {
    observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(observerAdapter)
}

fun <T> Observable<T>.observeOnMain(onNext: ((T) -> Unit)? = null, onCompile: (() -> Unit)? = null, onError: ((Throwable) -> Unit)? = null) {
    observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : ObserverAdapter<T>() {
                override fun onComplete() {
                    super.onComplete()
                    onCompile?.invoke()
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    onError?.invoke(e)
                }

                override fun onNext(t: T) {
                    onNext?.invoke(t)
                }
            })
}

abstract class ObserverAdapter<T> : DisposableObserver<T>() {

    override fun onStart() {
        if (!isNetworkAvailable(JFCZApplication.INSTANCE)) {
            val tips = JFCZApplication.INSTANCE.getString(R.string.network_invalid)
            val intent = Intent(JFCZApplication.INSTANCE, ErrorDialog::class.java)
            intent.putExtra(ErrorDialog.TIPS_KEY, tips)
            JFCZApplication.INSTANCE.startActivity(intent)
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

    @GET("/dev/auto/login")
    fun login(@Query("deviceNum") deviceNum: String, @Query("random") random: Int, @Query("sign") sign: String): Observable<Response<LoginResult>>
}