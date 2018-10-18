package com.shifen.game.jfcz.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.shifen.game.jfcz.JFCZApplication
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.Gift
import com.shifen.game.jfcz.model.Goods
import com.shifen.game.jfcz.model.OrderStatusRequestBody
import com.shifen.game.jfcz.services.*
import com.shifen.game.jfcz.utils.ApiConfig
import com.shifen.game.jfcz.utils.GAME_SESSION_ID
import com.shifen.game.jfcz.utils.USER_ID
import com.shifen.game.jfcz.utils.putConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pay.*
import java.util.concurrent.TimeUnit
import okhttp3.RequestBody
import java.text.Format
import java.text.SimpleDateFormat


class PayActivity : BaseActivity() {

    private var type: Int = -1
    private lateinit var goods: Goods
    private var currentGiftNumber: Int = 0
    private val orderStatusInterval = 2L
    private val qrCodeRefreshInterval = 70L

    private val disposables = CompositeDisposable()

    companion object {
        val GIFT_KEY="GIFT_KEY"
        val GOODS_KEY = "GOODS_KEY"
        var SESSION_ID_KEY="session_id_key"
        val BUY_TYPE = "buy_type"

        val BUY = 0
        val GAME = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        init()
    }

    private fun init() {
        currentGiftNumber = intent.getIntExtra(PayActivity.GIFT_KEY,-1)
        goods = intent.getParcelableExtra(GOODS_KEY)
        type  = intent.getIntExtra(BUY_TYPE, -1)

        tvGiftNumber.text = getString(R.string.choose_gift, goods.id)
        tvGiftName.text = goods.description
        tvGiftPrice.text = getString(R.string.gift_price, goods.price)
        Glide.with(this).load(goods.pictureUrl).into(ivGift)

        refreshQRCode()

        ivQRCode.setOnClickListener {
            refreshQRCode()
        }

        closebutton.setOnClickListener {
            onNoOperation();
        }
        disposables.add(Observable.interval(orderStatusInterval, orderStatusInterval, TimeUnit.SECONDS)
                .subscribe {
                    var t = ApiConfig.timestamp*1000;
                    var sdf: Format = SimpleDateFormat("yyyyMMddHHmmss")
                    var timestamp =  sdf.format(t);
                    val transferId = ApiConfig.containerId + goods.gridId + goods.goodsId + timestamp
                    val orderStatus = OrderStatusRequestBody(ApiConfig.timestamp, transferId)
                    val gson = Gson()
                    val json = gson.toJson(orderStatus)
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json)

                    ServiceManager.create(PayService::class.java).payOrderStatus(body)
                            .observeOnMain(onNext = { res ->

                                if (type == BUY) {
                                    putConfig { editor ->
                                        editor.putString(USER_ID, res.data.userId)
                                        editor.putString(GAME_SESSION_ID, res.data.gameSessionId)
                                    }
                                    disposables.dispose()
                                    finish();
                                    startActivity(Intent(this, PaySuccessDialog::class.java).apply {
                                        putExtra(PayActivity.GOODS_KEY, goods)
                                        putExtra(PayActivity.SESSION_ID_KEY, res.data.gameSessionId)
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    })

                                    // TODO("打开货柜，上报")
                                    var app : JFCZApplication = application as JFCZApplication
                                    app.deliverGoods(currentGiftNumber);
                                } else if (type == GAME) {
                                    disposables.dispose()
                                    finish();
                                    startActivity(Intent(this, GameActivity::class.java).apply {
                                        putExtra(PayActivity.GIFT_KEY, currentGiftNumber)
                                        putExtra(PayActivity.SESSION_ID_KEY, res.data.gameSessionId)
                                        putExtra(GameActivity.KEY_GIRD_ID, goods.gridId)
                                        putExtra(GameActivity.KEY_GOODS_ID, goods.goodsId)
                                        putExtra(GameActivity.KEY_USER_ID, res.data.userId)
                                        putExtra(GameActivity.KEY_SESSION_ID, res.data.gameSessionId)
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    })
                                }
                            }, onError = { t ->
                                t.printStackTrace()
                            })
                })

        disposables.add(Observable.interval(qrCodeRefreshInterval, qrCodeRefreshInterval, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { refreshQRCode() })
    }

    private fun refreshQRCode() {
        ivQRCode.setImageBitmap(ApiConfig.generateQRCode(
                goods.goodsId,goods.gridId,
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

    override fun onResume() {
        super.onResume()
        startTimer()
    }

    override fun onPause() {
        super.onPause()
        stopTimer();
    }
}