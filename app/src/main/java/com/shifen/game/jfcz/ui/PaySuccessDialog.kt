package com.shifen.game.jfcz.ui

import android.app.Activity
import android.os.Bundle
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
}