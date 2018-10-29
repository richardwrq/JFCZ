package com.shifen.game.jfcz.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.shifen.game.jfcz.ConfigManager
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.Banner
import com.shifen.game.jfcz.services.BannerService
import com.shifen.game.jfcz.services.ServiceManager
import com.shifen.game.jfcz.services.observeOnMain
import com.shifen.game.jfcz.utils.GlideImageLoader
import com.youth.banner.BannerConfig
import kotlinx.android.synthetic.main.activity_main.*

class ADActivity : BaseActivity()  {

    companion object {
        val ACTION_REFRESH_BANNER = "action_refresh_banner"
    }

    private lateinit var bannerList: List<Banner>

    private val refreshBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            questBannerList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

        questBannerList()
    }

    private fun init() {
        startActivity(Intent(this, GameActivity::class.java))

        go.setOnClickListener{
            stopTimer()
            startActivity(Intent(this, GiftListActivity::class.java))
            finish()
        }

        banner.setIndicatorGravity(BannerConfig.CENTER)
                .setImageLoader(GlideImageLoader())
                .setImages(ArrayList<String>())
                .start()

        banner.setOnBannerListener {
            stopTimer()
            startActivity(Intent(this, GiftListActivity::class.java))
            finish()
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(refreshBroadcastReceiver, IntentFilter(ACTION_REFRESH_BANNER))
    }

    private fun questBannerList() {
        ServiceManager.create(BannerService::class.java)
                .getBannerList()
                .observeOnMain(onNext = { res ->
                    bannerList = res.data
                    ConfigManager.updateBannerList(bannerList)
                    if (res.data.isNotEmpty()) {
                        banner.update(res.data.map { it.url })
                    }
                }, onError = {
                    it.printStackTrace()
                })
    }


    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshBroadcastReceiver)
    }

}
