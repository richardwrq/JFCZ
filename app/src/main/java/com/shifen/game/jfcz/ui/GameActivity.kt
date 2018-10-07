package com.shifen.game.jfcz.ui

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.shifen.game.jfcz.R
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {
    private val COUNT_DOWN_SECONDS = 30
    private val COUNT_DOWN_SECONDS_FAILURE = 10
    private var curRoundIndex = -1
    private var waitKnifeNum = 7
    private var waitKnifeViews = ArrayList<ImageView>()

    private val animation = TranslateAnimation(0f, 0f, 0f, -200f)
//    private val animation1 = TranslateAnimation(0f, 600f, 0f, 200f)
//    private val animation2 = TranslateAnimation(0f, -600f, 0f, 200f)

    private val starAnimationSet = AnimationSet(true)
    private lateinit var roundViews: Array<TextView>
    private var centerBitmapIds = arrayOf(R.mipmap.ic_watermelon, R.mipmap.ic_orange, R.mipmap.ic_peach)
    private var roundIndexIds = arrayOf(R.mipmap.ic_round_1, R.mipmap.ic_round_2, R.mipmap.ic_round_3)
    private var isFailure = false

    private var countDownSeconds = COUNT_DOWN_SECONDS
    private var countDownSecondsFailure = COUNT_DOWN_SECONDS_FAILURE

    private var countDownTimer = createCountDownTimer()
    private var countDownTimerFailure: CountDownTimer? = null

    init {
        val animation1 = AlphaAnimation(0f, 1f)
        val animation2 = AlphaAnimation(1f, 0f)
        animation1.duration = 300
        animation2.duration = 300
        animation2.startOffset = 300
        animation.duration = 50
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                isFailure = !game.addKnife()
                waitKnifeNum--
                tvWaitKnifeNum.text = waitKnifeNum.toString()
                waitKnifeViews[waitKnifeNum].visibility = View.INVISIBLE

                if (isFailure) {
                    updateGameResultView(false)
                } else {
                    ivStar1.visibility = View.VISIBLE
                    ivStar1.startAnimation(starAnimationSet)
                    game.postDelayed({
                        ivStar2.visibility = View.VISIBLE
                        ivStar2.startAnimation(starAnimationSet)
                    }, 100)
                    game.postDelayed({
                        ivStar1.visibility = View.INVISIBLE
                    }, 600)
                    game.postDelayed({
                        ivStar2.visibility = View.INVISIBLE
                    }, 700)


                    if (waitKnifeNum == 0) {
                        // 通过当前小关
//                        animation1.duration = 1500
//                        animation2.duration = 1500
//                        ivFruits1.startAnimation(animation1)
//                        ivFruits2.startAnimation(animation2)
                        nextRound()
                    }
                }
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        roundViews = arrayOf(tvRound1, tvRound2, tvRound3)
        // 等待的小刀
        for (i in 1..waitKnifeNum) {
            val ivWaitKnife = ImageView(this)
            ivWaitKnife.setImageResource(R.mipmap.ic_knife_wait)
            ltWaitKnife.addView(ivWaitKnife)
            waitKnifeViews.add(ivWaitKnife)
        }

        game.setOnClickListener {
            addKnife()
        }

        ivKnife.setOnClickListener {
            addKnife()
        }

        btnGameFinish.setOnClickListener {

        }

        nextRound()

//        val metric = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(metric)
//        val width = metric.widthPixels  // 屏幕宽度（像素）
//        val height = metric.heightPixels  // 屏幕高度（像素）
//        val density = metric.density  // 屏幕密度（0.75 / 1.0 / 1.5）
//        val densityDpi = metric.densityDpi  // 屏幕密度DPI（120 / 160 / 240）
//        Log.e("czm", "width = $width ---  height = $height ---- density = $density --- densityDpi = $densityDpi")
        hideBottomUIMenu()
    }

    private fun nextRound() {
        // 通关
        curRoundIndex++
        if (curRoundIndex == 3) {
            updateGameResultView(true)
            return
        }
        ltRound.visibility = View.VISIBLE
        ivStar1.visibility = View.INVISIBLE
        ivStar2.visibility = View.INVISIBLE
        ltGame.visibility = View.GONE
        ltResult.visibility = View.GONE
        ivRoundIndex.setImageResource(roundIndexIds[curRoundIndex])

        waitKnifeNum = waitKnifeViews.size
        waitKnifeViews.forEach {
            it.visibility = View.VISIBLE
        }

        tvWaitKnifeNum.text = waitKnifeNum.toString()

        ltRound.postDelayed({
            ltRound.visibility = View.GONE
            ltGame.visibility = View.VISIBLE
            ltResult.visibility = View.GONE
            game.prepare(centerBitmapIds[curRoundIndex])
            roundViews[curRoundIndex].isSelected = true
            tvCountDownTime.text = countDownSeconds.toString()
            countDownTimer.cancel()
            countDownTimer = createCountDownTimer()
            countDownTimer.start()
            game.start()
        }, 3000L)
    }

    private fun addKnife() {
        if (!isGameValid()) {
            return
        }
        ivKnife.startAnimation(animation)
    }

    private fun isGameValid(): Boolean {
        if (isFailure || curRoundIndex == 3) {
            return false
        }
        return true
    }

    private fun updateGameResultView(win: Boolean) {
        ltRound.visibility = View.GONE
        ltGame.visibility = View.GONE
        ltResult.visibility = View.VISIBLE
        if (win) {
            ltResult.setBackgroundResource(R.mipmap.bg_round)
            ivResult.setImageResource(R.mipmap.ic_game_win)
            btnGameFinish.visibility = View.VISIBLE
            tvCountDownTimeFailure.visibility = View.GONE
            btnGameFinish.setImageResource(R.mipmap.ic_finish)
        } else {
            ltResult.setBackgroundResource(R.mipmap.bg_game)
            ivResult.setImageResource(R.mipmap.ic_game_failure)
            btnGameFinish.visibility = View.VISIBLE
            tvCountDownTimeFailure.visibility = View.VISIBLE
            btnGameFinish.setImageResource(R.mipmap.ic_start_game_now)
            countDownTimerFailure?.cancel()
            countDownSecondsFailure = COUNT_DOWN_SECONDS_FAILURE
            tvCountDownTimeFailure.text = "(${countDownSecondsFailure}s)"
            countDownTimerFailure = object : CountDownTimer(COUNT_DOWN_SECONDS_FAILURE * 1000L, 1000) {
                override fun onFinish() {
                    startGameNow()
                }

                override fun onTick(p0: Long) {
                    countDownSecondsFailure--
                    tvCountDownTimeFailure.text = "(${countDownSecondsFailure}s)"
                }

            }
            countDownTimerFailure?.start()
        }
    }

    private fun startGameNow() {

    }

    private fun createCountDownTimer(): CountDownTimer {
        countDownSeconds = COUNT_DOWN_SECONDS
        return object : CountDownTimer(COUNT_DOWN_SECONDS * 1000L, 1000) {
            override fun onFinish() {
                isFailure = true
                updateGameResultView(false)
            }

            override fun onTick(p0: Long) {
                countDownSeconds--
                tvCountDownTime.text = countDownSeconds.toString()
            }
        }
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



