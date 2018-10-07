package com.shifen.game.jfcz.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.Gift
import kotlinx.android.synthetic.main.activity_pay.*

class PayActivity : BaseActivity() {

    companion object {
        val GIFT_KEY = "GIFT_KEY"
    }

    private val countDownTimer = object : CountDownTimer(60 * 1000L, 1000L) {
        override fun onFinish() {
            finish()
            val intent = Intent(this@PayActivity, ADActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        override fun onTick(millisUntilFinished: Long) {
            //do nothing
        }
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

    override fun onResume() {
        super.onResume()
        countDownTimer.start()
    }

    override fun onPause() {
        super.onPause()
        countDownTimer.cancel()
    }


}