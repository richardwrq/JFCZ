package com.shifen.game.jfcz.ui

import android.content.Context

import android.graphics.*
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.shifen.game.jfcz.R
import java.util.*

/**
 * @author caizeming
 * @email  caizeming@cvte.com
 * @date   2018/10/1
 * @description:
 */
class SmallGapsView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val FAILUR_ANGLE = 7

    private var countDownInterval = 10L
    private var countDownTimer = createCountDownTimer()

    // 速度，单位：度／秒
    var speed = 100
    // 是否开启正反转
    var enableReverse = false
        set (value) {
            if (value != field) {
                updateCurAngle()
                field = value
            }
        }

    private var curAngle = 0f
    private var curTime = -1L
    private var centerX = -1f
    private var centerY = -1f
    private var rotateMatrix = Matrix()

    // 中间的大图标
    private lateinit var centerBitmap: Bitmap
    private var centerBitmapHalfWidth = -1f
    private var centerBitmapHalfHeight = -1f
    // 小刀
    private val knifeBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_knife)
    private val knifeMatrix = Matrix()
    private val knifeArray = ArrayList<Float>()
    //整个游戏图
    private lateinit var gameBitmap: Bitmap
    private var gameCanvas = Canvas()
    private var paint = Paint()
    private var gameBitmapWidth = -1f
    private var gameBitmapHeight = -1f
    private var gameBitmapHalfWidth = -1f
    private var gameBitmapHalfHeight = -1f

    // 游戏是否失败，但增加小刀失败是置位
    private var isFailure = false


    init {
        paint.style = Paint.Style.FILL
    }

    fun prepare(centerBitmapId: Int) {
        centerBitmap = BitmapFactory.decodeResource(resources, centerBitmapId)
        centerBitmapHalfWidth = centerBitmap.width / 2f
        centerBitmapHalfHeight = centerBitmap.height / 2f

        gameBitmapWidth = centerBitmap.width + 2f * knifeBitmap.height
        gameBitmapHeight = centerBitmap.height + 2f * knifeBitmap.height
        gameBitmapHalfWidth = gameBitmapWidth / 2f
        gameBitmapHalfHeight = gameBitmapHeight / 2f

        gameBitmap = Bitmap.createBitmap(gameBitmapWidth.toInt(), gameBitmapHeight.toInt(), Bitmap.Config.ARGB_8888)
        gameCanvas.setBitmap(gameBitmap)
        gameCanvas.drawBitmap(centerBitmap, gameBitmapHalfWidth - centerBitmapHalfWidth,
                gameBitmapHalfHeight - centerBitmapHalfHeight, paint)
    }

    var curRoundIndex =0
    fun start(c:Int) {
        curRoundIndex =c
        curTime = System.currentTimeMillis()
        knifeArray.clear()

        if (curRoundIndex ==1){
            for (i in 1..2){
                var f = i*180.toFloat()
                knifeMatrix.reset()
                knifeMatrix.postTranslate(gameBitmapHalfWidth, centerBitmapHalfHeight + 1.6f * knifeBitmap.height)
                knifeMatrix.postRotate(f, gameBitmapHalfWidth, gameBitmapHalfHeight)
                gameCanvas.drawBitmap(knifeBitmap, knifeMatrix, paint)
                gameCanvas.drawBitmap(centerBitmap, gameBitmapHalfWidth - centerBitmapHalfWidth, gameBitmapHalfHeight - centerBitmapHalfHeight, paint)
            }
        }

        if (curRoundIndex ==2){
            for (i in 1..4){
                var f = i*90.toFloat()
                knifeMatrix.reset()
                knifeMatrix.postTranslate(gameBitmapHalfWidth, centerBitmapHalfHeight + 1.6f * knifeBitmap.height)
                knifeMatrix.postRotate(f, gameBitmapHalfWidth, gameBitmapHalfHeight)
                gameCanvas.drawBitmap(knifeBitmap, knifeMatrix, paint)
                gameCanvas.drawBitmap(centerBitmap, gameBitmapHalfWidth - centerBitmapHalfWidth, gameBitmapHalfHeight - centerBitmapHalfHeight, paint)
            }
        }

        countDownTimer.start()
    }

    fun stop(){
        countDownTimer.cancel()
    }

    /**
     * 增加小刀
     * @return Boolean 成功，失败
     */
    fun addKnife(): Boolean {
        if (isFailure) {
            return false
        }
        var result = true
        if (curRoundIndex == 1) {
            if (curAngle % 180 in (0 - FAILUR_ANGLE)..(0 + FAILUR_ANGLE)) {
                return false
            }
        }

        if (curRoundIndex ==2){
            if (curAngle % 90 in (0 - FAILUR_ANGLE)..(0 + FAILUR_ANGLE)) {
                return false
            }
        }
        knifeArray.forEach {
            if (curAngle in it - FAILUR_ANGLE..it + FAILUR_ANGLE) {
                isFailure = true
                result = false
                countDownTimer.cancel()
            }
        }
        knifeArray.add(curAngle)
        knifeMatrix.reset()
        knifeMatrix.postTranslate(gameBitmapHalfWidth, centerBitmapHalfHeight + 1.6f * knifeBitmap.height)
        knifeMatrix.postRotate(-curAngle, gameBitmapHalfWidth, gameBitmapHalfHeight)
        gameCanvas.drawBitmap(knifeBitmap, knifeMatrix, paint)
        gameCanvas.drawBitmap(centerBitmap, gameBitmapHalfWidth - centerBitmapHalfWidth, gameBitmapHalfHeight - centerBitmapHalfHeight, paint)


        updateCurAngle()
        return result
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        centerX = width / 2f
        centerY = height / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rotateMatrix.reset()
        rotateMatrix.postTranslate(-gameBitmapHalfWidth, -gameBitmapHalfHeight)
        rotateMatrix.postRotate(curAngle)
        rotateMatrix.postTranslate(centerX, centerY)

        canvas.drawBitmap(gameBitmap, rotateMatrix, paint)
    }

    private fun createCountDownTimer(): CountDownTimer {
        return object : CountDownTimer(Long.MAX_VALUE, countDownInterval) {
            override fun onFinish() {
                countDownTimer = createCountDownTimer()
            }

            override fun onTick(p0: Long) {
                updateCurAngle()
                invalidate()
            }
        }
    }

    private fun updateCurAngle() {
        curAngle = if (enableReverse) {
            (curAngle - (System.currentTimeMillis() - curTime) * speed / 1000) % 360
        } else {
            (curAngle + (System.currentTimeMillis() - curTime) * speed / 1000) % 360
        }
        curTime = System.currentTimeMillis()
    }

    fun startLog(){
        Log.i("aaaok","startLog ${curAngle} speed = ${speed} cc=${(System.currentTimeMillis() - curTime) * speed / 1000}")
    }

    fun endLog(){
         Log.i("aaaok","endLog ${curAngle} speed = ${speed} cc=${(System.currentTimeMillis() - curTime) * speed / 1000}")
    }

    open fun lastKnife(duration: Long) {
        var abscur =((duration*speed/1000).toFloat())
        var ret =binarysearchKey(knifeArray, curAngle)
        Log.i("aaaok","ret ${ret} abscur ${abscur}")
        if (enableReverse){
            curAngle= ret +abscur
        }else{
            curAngle= ret -abscur
        }
        Log.i("aaaok","ret2 ${curAngle}")
    }
    /*open fun lastKnife(duration: Long) {
        abscur =Math.abs(startLog-endLog)
        var ret =binarysearchKey(knifeArray, curAngle)
        Log.i("aaaok","ret ${ret} abscur ${abscur}")
        if (enableReverse){
            curAngle= ret +abscur
        }else{
            curAngle= ret -abscur
        }
        Log.i("aaaok","ret2 ${curAngle}")
    }*/
    fun binarysearchKey(a: ArrayList<Float>, targetNum: Float): Float {

        var array = ArrayList<Float>()
        a.forEach {
            array.add(it)
        }
        Collections.sort(array)
        var start = array[array.size-1]-360f
        var end = array[0]+360f
        Log.i("aaaok","数组 ${array.toString()} enableReverse = ${enableReverse} targetNum = ${targetNum}")
        array.add(start)
        array.add(end)
        Collections.sort(array)
        var mid =0f
        mid  = Math.abs(array[0])
        var targetindex = 0
        for (i in array.indices){
            var abs =Math.abs(array[i] - targetNum)
            if (abs < mid){
                mid=abs
                targetindex = i
            }
        }

        if(targetindex ==0){
            return array[array.size-2]
        }

        if(targetindex ==array.size-1){
            return array[1]
        }
        var ret =array[targetindex]
        ret =ret%360f
        a.forEach({
            if (Math.abs(it) == Math.abs(ret)){
                return it
            }
        })
        return a[0]
    }
}

