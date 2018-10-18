package com.shifen.game.jfcz.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.Html
import android.widget.Toast
import com.bumptech.glide.Glide
import com.shifen.game.jfcz.ConfigManager
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.Goods
import com.shifen.game.jfcz.services.GiftService
import com.shifen.game.jfcz.services.ServiceManager
import com.shifen.game.jfcz.services.observeOnMain
import com.shifen.game.jfcz.ui.adapter.GiftListAdapter
import com.shifen.game.jfcz.utils.MDGridRvDividerDecoration
import kotlinx.android.synthetic.main.activity_gift_list.*

class GiftListActivity : BaseActivity() {

    private var currentGoods: Goods? = null
    private val adapter = GiftListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_list)

        init()

        questGiftList()
    }

    private fun init() {
        rvGiftList.layoutManager = GridLayoutManager(this, 5)
        rvGiftList.addItemDecoration(MDGridRvDividerDecoration(this))

        rvGiftList.adapter = adapter

        adapter.onItemClickListener = { _, position ->

            currentGoods = adapter.getItem(position).goodsList[0]
            tvGiftName.text = currentGoods?.description
            tvGiftNumber.text = getString(R.string.choose_gift, currentGoods?.id)
            tvGiftPrice.text = Html.fromHtml(getString(R.string.gift_price_red, currentGoods?.price.toString()))
            tvChallengePrice.text = Html.fromHtml(getString(R.string.gift_challenge_price_red, currentGoods?.gamePrice.toString()))
            Glide.with(this).load(currentGoods?.pictureUrl).into(ivGift)
        }

        btnBuy.setOnClickListener {
            if (currentGoods == null) {
                Toast.makeText(this, getString(R.string.pls_choose_gift), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, PayActivity::class.java).apply {
                putExtra(PayActivity.GOODS_KEY, currentGoods)
                putExtra(PayActivity.BUY_TYPE, PayActivity.BUY)
            })
        }

        btnChallenge.setOnClickListener {
            if (currentGoods == null) {
                Toast.makeText(this, getString(R.string.pls_choose_gift), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActivity(Intent(this, PayActivity::class.java).apply {
                putExtra(PayActivity.GOODS_KEY, currentGoods)
                putExtra(PayActivity.BUY_TYPE, PayActivity.GAME)
            })
        }

        closebutton.setOnClickListener {
            onNoOperation();
        }
    }

    private fun questGiftList() {
        val list = ConfigManager.getGiftList()
        if (list.isNotEmpty()) {
            adapter.setItems(list)
            return
        }
        ServiceManager.create(GiftService::class.java)
                .getGiftList()
                .observeOnMain {
                    adapter.setItems(it.data)
                    ConfigManager.updateGiftList(it.data)
                }
    }

    override fun onNoOperation() {
        super.onNoOperation()
        finish()
        val intent = Intent(this@GiftListActivity, ADActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
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