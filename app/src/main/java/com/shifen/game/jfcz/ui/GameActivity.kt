package com.shifen.game.jfcz.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.shifen.game.jfcz.ConfigManager
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.*
import com.shifen.game.jfcz.services.*
import kotlinx.android.synthetic.main.activity_game.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import usb.DeviceHelp
import usb.OnDataReceiveListener


class GameActivity : AppCompatActivity() {

    private val TAG = "GameActivity"

    private val COUNT_DOWN_SECONDS = 30
    private val COUNT_DOWN_SECONDS_FAILURE = 10
    private var curRoundIndex = -1
    private var waitKnifeNum = 10
    private var waitKnifeViews = ArrayList<ImageView>()

    private val animation = TranslateAnimation(0f, 0f, 0f, -200f)
    private val ivFruits1AnimatorSet = AnimatorSet()
    private val ivFruits2AnimatorSet = AnimatorSet()

    private val ivStar1AnimationSet = AnimationSet(false)
    private val ivStar2AnimationSet = AnimationSet(false)
    private lateinit var roundViews: Array<TextView>
    private var centerBitmapIds = arrayOf(R.mipmap.ic_watermelon, R.mipmap.ic_orange, R.mipmap.ic_peach)
    private var roundIndexIds = arrayOf(R.mipmap.ic_round_1, R.mipmap.ic_round_2, R.mipmap.ic_round_3)
    private var fruits1IndexIds = arrayOf(R.mipmap.ic_watermelon_1, R.mipmap.ic_orange_1, R.mipmap.ic_peach_1)
    private var fruits2IndexIds = arrayOf(R.mipmap.ic_watermelon_2, R.mipmap.ic_orange_2, R.mipmap.ic_peach_2)
    private var isFailure = false

    private var countDownSeconds = COUNT_DOWN_SECONDS
    private var countDownSecondsFailure = COUNT_DOWN_SECONDS_FAILURE

    private var countDownTimer = createCountDownTimer()
    private var countDownTimerFailure: CountDownTimer? = null
    // intent
    companion object {
        val KEY_GIRD_ID = "KEY_GIRD_ID"
        val KEY_USER_ID = "KEY_USER_ID"
        val KEY_GOODS_ID = "KEY_GOODS_ID"
        val KEY_SESSION_ID = "KEY_SESSION_ID"
    }

    private var mGirdId = ""
    private var mUserId = ""
    private var mGoodsId = ""
    private var mSessionId = ""

    /* private var mGirdId = "0000000000000001"
     private var mUserId = "wx3453645756345d535"
     private var mGoodsId = "0000000000001111"
     private var mSessionId = "1000000000000001"*/
    private var mustFailure = true;
    init {
        val animation1 = AlphaAnimation(0f, 1f)
        val animation2 = AlphaAnimation(1f, 0f)
        animation1.duration = 300
        animation2.duration = 300
        animation2.startOffset = 300
        ivStar1AnimationSet.addAnimation(animation1)
        ivStar1AnimationSet.addAnimation(animation2)
        val animation3 = AlphaAnimation(0f, 1f)
        val animation4 = AlphaAnimation(1f, 0f)
        animation3.duration = 300
        animation4.duration = 300
        animation4.startOffset = 300
        ivStar2AnimationSet.addAnimation(animation3)
        ivStar2AnimationSet.addAnimation(animation4)

        animation.duration = 50
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                isFailure = !game.addKnife()
                waitKnifeNum--
                if (waitKnifeNum < 0) {
                    return;
                }
                tvWaitKnifeNum.text = waitKnifeNum.toString()
                waitKnifeViews[waitKnifeNum].visibility = View.INVISIBLE
                if (isFailure) {
                    updateGameResultView(false)
                } else {
                    ivStar1.startAnimation(ivStar1AnimationSet)
                    game.postDelayed({
                        ivStar2.startAnimation(ivStar2AnimationSet)
                    }, 200)

                    if (waitKnifeNum == 0) {
                        // 通过当前小关
                        game.visibility = View.GONE
                        ivFruits1.visibility = View.VISIBLE
                        ivFruits2.visibility = View.VISIBLE
                        updateGameStatus(1)

                        ivFruits1AnimatorSet.start()
                        ivFruits2AnimatorSet.start()
                    }
                }
            }

            override fun onAnimationStart(p0: Animation?) {
                if (mustFailure) {
                    if (waitKnifeNum == 1 && curRoundIndex == 2) {
                        game.lastKnife(animation.duration)
                    }
                }
            }
        })

        ivFruits1AnimatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                game.visibility = View.VISIBLE
                ivFruits1.visibility = View.GONE
                ivFruits2.visibility = View.GONE
                countDownTimer.cancel()
                nextRound()
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        // .getGameConfig()
        Log.i(TAG, ConfigManager.getGameProbability().toString())
        Log.i(TAG, ConfigManager.getGameConfig().toString())
        if (ConfigManager.getGameProbability()!!.randomType == 1) {
            var gameNumber = ConfigManager.getGameNumber()
            gameNumber--
            ConfigManager.updateGameNumber(gameNumber);

            Log.i(TAG, "gameNumber = ${gameNumber}")
            if (gameNumber < 0) {
                mustFailure = false
            }
        } else {
            val random = java.util.Random()// 定义随机类
            val result = random.nextInt(ConfigManager.getGameNumber()) + 1  //[1,num)
            if (result == 1) {
                mustFailure = false
            }
        }

        Log.i(TAG, "mustFailure = ${mustFailure}")

        val animator1 = ObjectAnimator.ofFloat(ivFruits1, "translationX", 0f, 200f)
        val duration = 800L
        animator1.duration = duration
        val animator2 = ObjectAnimator.ofFloat(ivFruits1, "translationY", 0f, 800f)
        animator2.duration = duration
        val animator3 = ObjectAnimator.ofFloat(ivFruits1, "rotation", 0f, -90f)
        animator3.duration = duration
        val animator7 = ObjectAnimator.ofFloat(ivFruits1, "rotation", -90f, -160f)
        animator7.duration = duration

        ivFruits1AnimatorSet.play(animator1).with(animator3).before(animator2)
        ivFruits1AnimatorSet.play(animator2).with(animator7)

        val animator4 = ObjectAnimator.ofFloat(ivFruits2, "translationX", 0f, -200f)
        animator4.duration = duration
        val animator5 = ObjectAnimator.ofFloat(ivFruits2, "translationY", 0f, 800f)
        animator5.duration = duration
        val animator6 = ObjectAnimator.ofFloat(ivFruits2, "rotation", 0f, -90f)
        animator6.duration = duration
        val animator8 = ObjectAnimator.ofFloat(ivFruits2, "rotation", -90f, -160f)
        animator8.duration = duration

        ivFruits2AnimatorSet.play(animator4).with(animator6).before(animator5)
        ivFruits2AnimatorSet.play(animator5).with(animator8)
        roundViews = arrayOf(tvRound1, tvRound2, tvRound3)
        // 等待的小刀
        if (waitKnifeNum > 10) {
            ltWaitKnife2Group.visibility = View.VISIBLE
        } else {
            ltWaitKnife2Group.visibility = View.GONE
        }
        for (i in 1..10) {
            val ivWaitKnife = ImageView(this)
            ivWaitKnife.setImageResource(R.mipmap.ic_knife_wait)
            ivWaitKnife.visibility = View.GONE
            ltWaitKnife.addView(ivWaitKnife)
            waitKnifeViews.add(ivWaitKnife)
        }

        for (i in 1..10) {
            val ivWaitKnife2 = ImageView(this)
            ivWaitKnife2.setImageResource(R.mipmap.ic_knife_wait)
            ivWaitKnife2.visibility = View.GONE
            ltWaitKnife2.addView(ivWaitKnife2)
            waitKnifeViews.add(ivWaitKnife2)
        }

        game.setOnClickListener {
            addKnife()
        }

        ivKnife.setOnClickListener {
            addKnife()
        }

        btnGameFinish.setOnClickListener {
            startGameNow()
        }

        nextRound()

//        val metric = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(metric)
//        val width = metric.widthPixels  // 屏幕宽度（像素）
//        val height = metric.heightPixels  // 屏幕高度（像素）
//        val density = metric.density  // 屏幕密度（0.75 / 1.0 / 1.5）
//        val densityDpi = metric.densityDpi  // 屏幕密度DPI（120 / 160 / 240）
//        Log.e("czm", "width = $width ---  height = $height ---- density = $density --- densityDpi = $densityDpi")
    }

    override fun onResume() {
        super.onResume()
        intent.getStringExtra(KEY_GOODS_ID)?.let {
            mGoodsId = it
        }
        intent.getStringExtra(KEY_GIRD_ID)?.let {
            mGirdId = it
        }
        intent.getStringExtra(KEY_USER_ID)?.let {
            mUserId = it
        }
        intent.getStringExtra(KEY_SESSION_ID)?.let {
            mSessionId = it
        }
    }

    private fun enableReverse() {
        game.postDelayed({
            game.enableReverse = !game.enableReverse
            enableReverse()
        }, 5000)
    }

    private fun nextRound() {
        curRoundIndex++
        // 通关
        if (curRoundIndex == 3) {
            updateGameResultView(true)
            return
        }

        var gameConfig: GameConfig;
        if (mustFailure) {
            gameConfig = ConfigManager.getGameConfig()[curRoundIndex]
            waitKnifeNum = Math.min(gameConfig.kineves, 20)
            countDownSeconds = gameConfig.leaveTime
            game.speed = gameConfig.speed * 80
        } else {
            // TODO 容易模式
            gameConfig = ConfigManager.getGameConfig()[curRoundIndex]
            gameConfig.rotate = 0
            waitKnifeNum = Math.min(gameConfig.kineves, 20)
            countDownSeconds = gameConfig.leaveTime
            game.speed = Math.min(gameConfig.speed * 40, 100)
        }

        if (waitKnifeNum > 10) {
            ltWaitKnife2Group.visibility = View.VISIBLE
        } else {
            ltWaitKnife2Group.visibility = View.GONE
        }
        for (i in 0 until waitKnifeNum) {
            waitKnifeViews[i].visibility = View.VISIBLE
        }
        ltRound.visibility = View.VISIBLE
        ivStar1.visibility = View.INVISIBLE
        ivStar2.visibility = View.INVISIBLE
        ltGame.visibility = View.GONE
        ltResult.visibility = View.GONE
        ivRoundIndex.setImageResource(roundIndexIds[curRoundIndex])
        ivFruits1.setImageResource(fruits1IndexIds[curRoundIndex])
        ivFruits2.setImageResource(fruits2IndexIds[curRoundIndex])

        tvWaitKnifeNum.text = waitKnifeNum.toString()

        newGame(gameConfig)

        ltRound.postDelayed({
            ltRound.visibility = View.GONE
            ltGame.visibility = View.VISIBLE
            ltResult.visibility = View.GONE
            game.prepare(centerBitmapIds[curRoundIndex])
            roundViews[curRoundIndex].isSelected = true
            tvCountDownTime.text = countDownSeconds.toString()
            countDownTimer = createCountDownTimer()
            countDownTimer.start()
            if (gameConfig.rotate == 1) {
                enableReverse()
            }
            game.start(curRoundIndex)
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
            btnGameFinish.setImageResource(R.drawable.bg_btn_win)
            updateGoods()
        } else {
            updateGameStatus(2)

            ltResult.setBackgroundResource(R.mipmap.bg_game)
            ivResult.setImageResource(R.mipmap.ic_game_failure)
            btnGameFinish.visibility = View.VISIBLE
            tvCountDownTimeFailure.visibility = View.VISIBLE
            btnGameFinish.setImageResource(R.drawable.bg_btn_failure)
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

    override fun onStop() {
        super.onStop()
        game.stop()
        countDownTimerFailure?.cancel()
        countDownTimer.cancel()
    }

    private fun startGameNow() {
        startActivity(Intent(this, GiftListActivity::class.java))
        finish()
    }

    private fun createCountDownTimer(): CountDownTimer {
        return object : CountDownTimer(countDownSeconds * 1000L, 1000) {
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

    private fun updateGameStatus(status: Int) {
        val gameConfig = ConfigManager.getGameConfig()[curRoundIndex]
        val jsonObject = JSONObject()
        jsonObject.put("gridId", mGirdId)
        jsonObject.put("userId", mUserId)
        jsonObject.put("goodsId", mGoodsId)
        jsonObject.put("sessionId", mSessionId)
        jsonObject.put("gameId", gameConfig.gameId)
        jsonObject.put("checkPointLevel", gameConfig.checkPointLevel)
        jsonObject.put("passStatus", status)
        jsonObject.put("passTime", gameConfig.leaveTime - countDownSeconds)

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString())
        ServiceManager.create(GameService::class.java).updateGameStatus(body).observeOnMain { }
    }

    private fun newGame(gameConfig: GameConfig) {
        val jsonObject = JSONObject()
        jsonObject.put("gridId", mGirdId)
        jsonObject.put("userId", mUserId)
        jsonObject.put("goodsId", mGoodsId)
        jsonObject.put("sessionId", mSessionId)
        jsonObject.put("gameId", gameConfig.gameId)
        jsonObject.put("checkPointLevel", gameConfig.checkPointLevel)

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString())
        ServiceManager.create(GameService::class.java).newGameStatus(body).observeOnMain {}
    }

    fun updateGoods() {

        val updateGoodsBodyRequestBody = updateGoodsBody(mGirdId, mGoodsId)
        var gson = Gson()
        var json = gson.toJson(updateGoodsBodyRequestBody)
        var body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json)
        ServiceManager.create(GoodsService::class.java).updateGoods(body)
                .observeOnMain {}

        val currentGiftNumber = intent.getIntExtra(PayActivity.GIFT_KEY, -1)
        // TODO("打开货柜，上报")
        DeviceHelp.getInstance().deliverGoods(currentGiftNumber)
        DeviceHelp.getInstance().setOnDataReceiveListener(object : OnDataReceiveListener {
            override fun onDataReceive(bytes: ByteArray) {
                if (bytes[3] == DeviceHelp.DELIVER_GOODS_RETURN) {
                    var number = bytes[4].toInt();
                    var status = bytes[5].toInt();
                    var operateStatusBody = operateStatusBody(number, status)
                    gson = Gson()
                    json = gson.toJson(operateStatusBody)
                    body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json)
                    ServiceManager.create(OperateService::class.java).operateStatus(body).observeOnMain {}
                }
            }
        })


        //恢复默认次数  固定模式
        if (ConfigManager.getGameProbability()!!.randomType == 1) {
            ConfigManager.updateGameNumber(ConfigManager.getGameProbability()!!.rate)
        }
        mustFailure = true;
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



