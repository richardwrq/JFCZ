package com.shifen.game.jfcz.ui

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.Goods
import com.shifen.game.jfcz.services.PayService
import com.shifen.game.jfcz.services.ServiceManager
import com.shifen.game.jfcz.services.observeOnMain
import com.shifen.game.jfcz.utils.ApiConfig
import com.shifen.game.jfcz.utils.GAME_SESSION_ID
import com.shifen.game.jfcz.utils.USER_ID
import com.shifen.game.jfcz.utils.putConfig
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pay.*
import java.util.concurrent.TimeUnit

class PayActivity : BaseActivity() {

    private var type: Int = -1
    private lateinit var goods: Goods

    private val orderStatusInterval = 2L
    private val qrCodeRefreshInterval = 70L

    private val disposables = CompositeDisposable()

    companion object {
        val GOODS_KEY = "GOODS_KEY"
        val BUY_TYPE = "buy_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        init()
    }

    private fun init() {
        goods = intent.getParcelableExtra(GOODS_KEY)
        type = intent.getIntExtra(BUY_TYPE, -1)

        tvGiftNumber.text = getString(R.string.choose_gift, goods.id)
        tvGiftName.text = goods.description
        tvGiftPrice.text = getString(R.string.gift_price, goods.price)
        Glide.with(this).load(goods.pictureUrl).into(ivGift)

        refreshQRCode()

        ivQRCode.setOnClickListener {
            refreshQRCode()
        }

        disposables.add(Observable.interval(orderStatusInterval, orderStatusInterval, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe {
                    ServiceManager.create(PayService::class.java).payOrderStatus()
                            .observeOnMain(onNext = { res ->

                                putConfig { editor ->
                                    editor.putString(USER_ID, res.data.userId)
                                    editor.putString(GAME_SESSION_ID, res.data.gameSessionId)
                                }
                                disposables.dispose()
                                TODO("打开货柜，上报")
                            }, onError = {
                               Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                            })
                })
    }

    private fun refreshQRCode() {
        ivQRCode.setImageBitmap(ApiConfig.generateQRCode(
                goods.id,
                type, resources.getDimensionPixelSize(R.dimen.qrcode_width),
                resources.getDimensionPixelSize(R.dimen.qrcode_height)))
    }

    override fun onNoOperation() {
        super.onNoOperation()
        finish()
        val intent = Intent(this@PayActivity, ADActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}