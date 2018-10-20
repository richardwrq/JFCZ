package com.shifen.game.jfcz.ui

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.Goods
import kotlinx.android.synthetic.main.dialog_pay_success.*

class PaySuccessDialog : Activity() {

    companion object {
        // val SESSION_ID_KEY = "session_id_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_pay_success)

        init()
    }

    private fun init() {
        val goods = intent.getParcelableExtra<Goods>(PayActivity.GOODS_KEY)

        Glide.with(this).load(goods.pictureUrl).into(ivGift)
        tvGiftName.text = getString(R.string.pay_success_tips, goods.description)

        tvOrderNumber.text = getString(R.string.order_id, intent.getStringExtra(PayActivity.SESSION_ID_KEY))

        btnFinish.setOnClickListener { finish() }
    }

    override fun onAttachedToWindow() {
        hideBottomUIMenu()
    }
    /**
     * 隐藏虚拟按键，并且全屏
     */
    private fun hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        val decorView = window.decorView
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            decorView.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions;
        }
    }
}