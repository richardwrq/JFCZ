package com.shifen.game.jfcz.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.Gift
import kotlinx.android.synthetic.main.activity_pay.*

class PayActivity : AppCompatActivity() {

    companion object {
        val GIFT_KEY = "GIFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        init()
    }

    private fun init() {
        val gift = intent.getParcelableExtra<Gift>(GIFT_KEY)

        tvGiftNumber.text = getString(R.string.choose_gift, gift.containerNumber)
        tvGiftName.text = gift.name
        tvGiftPrice.text = getString(R.string.gift_price, gift.price.toString())
    }
}