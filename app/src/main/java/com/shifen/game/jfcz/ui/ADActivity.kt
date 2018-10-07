package com.shifen.game.jfcz.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.utils.BANNER_LIST
import com.shifen.game.jfcz.utils.GlideImageLoader
import com.shifen.game.jfcz.utils.getConfig
import com.youth.banner.BannerConfig
import kotlinx.android.synthetic.main.activity_main.*

class ADActivity : BaseActivity() {

    companion object {
        val ACTION_REFRESH_BANNER = "action_refresh_banner"
    }

    private val refreshBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val list = getImagesList()
            if (!list.isEmpty()) {
                banner.update(getImagesList())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        banner.setIndicatorGravity(BannerConfig.CENTER)
                .setImageLoader(GlideImageLoader())
                .setImages(getImagesList())
                .start()

        banner.setOnBannerListener {
            startActivity(Intent(this, GiftListActivity::class.java))
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(refreshBroadcastReceiver, IntentFilter(ACTION_REFRESH_BANNER))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshBroadcastReceiver)
    }

    fun gotoGiftList(view: View) {
        startActivity(Intent(this, GameActivity::class.java))
    }

    private fun getImagesList(): List<String> {
        return getConfig().getString(BANNER_LIST, "")?.split(",")?.toMutableList() ?: mutableListOf("")
    }
}
