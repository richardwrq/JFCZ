package com.shifen.game.jfcz.ui

import android.os.Build
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View

/**
 * @author caizeming
 * @email  caizeming@cvte.com
 * @date   2018/10/7
 * @description:
 */
open class BaseActivity : AppCompatActivity() {

    private var countDownTimer = object : CountDownTimer(60 * 1000L, 1000L) {
        override fun onFinish() {
            onNoOperation()
        }

        override fun onTick(millisUntilFinished: Long) {
            //do nothing
        }
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_HOME, KeyEvent.KEYCODE_MENU -> return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                countDownTimer.start()
            }

        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 一定时间内无操作
     */
    open fun onNoOperation() {

    }
}