package com.shifen.game.jfcz.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.Html
import android.widget.Toast
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.Gift
import com.shifen.game.jfcz.ui.adapter.GiftListAdapter
import com.shifen.game.jfcz.utils.MDGridRvDividerDecoration
import kotlinx.android.synthetic.main.activity_gift_list.*

class GiftListActivity : BaseActivity() {

    private var currentGift: Gift? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_list)

        init()
    }

    private fun init() {
        rvGiftList.layoutManager = GridLayoutManager(this, 5)
        rvGiftList.addItemDecoration(MDGridRvDividerDecoration(this))

        val list = ArrayList<Gift>()
        for (i in 0..34) {
            list.add(Gift("这是${i}号礼品", i, "...", 50.0F, 10.0F))
        }
        val adapter = GiftListAdapter()
        adapter.addItems(list)
        rvGiftList.adapter = adapter

        adapter.onItemClickListener = { _, position ->

            currentGift = adapter.getItem(position)
            tvGiftName.text = currentGift?.name
            tvGiftNumber.text = getString(R.string.choose_gift, currentGift?.containerNumber)
            tvGiftPrice.text = Html.fromHtml(getString(R.string.gift_price_red, currentGift?.price.toString()))
            tvChallengePrice.text = Html.fromHtml(getString(R.string.gift_challenge_price_red, currentGift?.challengePrice.toString()))
        }

        btnBuy.setOnClickListener {
            if (currentGift == null) {
                Toast.makeText(this, getString(R.string.pls_choose_gift), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, PayActivity::class.java).apply { putExtra(PayActivity.GIFT_KEY, currentGift) })
        }

        btnChallenge.setOnClickListener {
            if (currentGift == null) {
                Toast.makeText(this, getString(R.string.pls_choose_gift), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, GameActivity::class.java))
        }
    }

    override fun onNoOperation() {
        super.onNoOperation()
        finish()
        val intent = Intent(this@GiftListActivity, ADActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}