package com.shifen.game.jfcz.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.utils.GlideImageLoader
import com.youth.banner.BannerConfig
import kotlinx.android.synthetic.main.activity_main.*

class ADActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        banner.setIndicatorGravity(BannerConfig.CENTER)
                .setImageLoader(GlideImageLoader())
                .setImages(listOf("http://news.vsochina.com/uploadfile/2017/0405/20170405031816266.jpg", "http://h.hiphotos.baidu.com/zhidao/pic/item/b7fd5266d01609242404d97bd50735fae6cd34a8.jpg"))
                .start()

        grabGift.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }
}
