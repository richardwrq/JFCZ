package com.shifen.game.jfcz.ui

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import com.shifen.game.jfcz.R
import kotlinx.android.synthetic.main.dialog_error.*

class ErrorDialog : Activity() {

    companion object {
        val TIPS_KEY = "tips_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_error)

        val string = intent.getStringExtra(TIPS_KEY)
        tvTips.text = string
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