package com.shifen.game.jfcz.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.Gift
import com.shifen.game.jfcz.model.operateStatusBody
import com.shifen.game.jfcz.services.GiftService
import com.shifen.game.jfcz.services.ServiceManager
import com.shifen.game.jfcz.services.observeOnMain
import com.shifen.game.jfcz.ui.adapter.OutTestAdapter
import usb.OnDataReceiveListener
import com.shifen.game.jfcz.utils.MDGridRvDividerDecoration


import kotlinx.android.synthetic.main.activity_out_test.*
import usb.DeviceHelp
import java.util.ArrayList

class OutTestActivity : AppCompatActivity(), OnDataReceiveListener {




    val adapter = OutTestAdapter()
    private lateinit var list:List<operateStatusBody>;
    var countDownTimer: CountDownTimer? =null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_out_test)
        DeviceHelp.getInstance().setOnDataReceiveListener(this)

        init()

        // DeviceHelp.getInstance().checkState()
    }


    fun init(){

        DeviceHelp.getInstance().checkState(null)
        rvGiftList.layoutManager = GridLayoutManager(this, 9)
        rvGiftList.addItemDecoration(MDGridRvDividerDecoration(this))
        rvGiftList.adapter = adapter
        adapter.onItemClickListener = { _, position ->

            var number = list[position].number
            DeviceHelp.getInstance().deliverGoods(number)
        }

        all.setOnClickListener {
            var needArr =ArrayList<operateStatusBody>();
            var j =0;
            for (i in list.indices){
                if (list[i].status ==adapter.STATUS_CLOSE){
                    needArr.add(list[i])
                }
            }

            countDownTimer = object : CountDownTimer(needArr.size*2 * 1000L, 2000L) {
                override fun onFinish() {

                }

                override fun onTick(millisUntilFinished: Long) {
                    DeviceHelp.getInstance().deliverGoods(needArr[j].number.toInt())
                    j++;
                }
            }
            countDownTimer!!.start()
        }
        allend.setOnClickListener{
            countDownTimer!!.cancel();
        }

        finish.setOnClickListener {
            if (countDownTimer!=null){
                countDownTimer!!.cancel();
            }
            val intent = Intent(this, ADActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    override fun onDataReceive(msg: ByteArray) {
        // 返回所有所状态 第一个索引代表第一个所 值为状态
        if (msg[3] == DeviceHelp.CHECK_STATE_RETURN) {
            var itemArray = ArrayList<operateStatusBody>();
            for (i in 0..(msg[2].toInt()-1)) {
                var status = msg[3 + i].toInt()
                if (msg[4 + i] == 0x00.toByte()) {
                    status = adapter.STATUS_CLOSE
                }
                if (msg[4 + i] == 0x01.toByte()) {
                    status = adapter.STATUS_OPEN
                }
                if (msg[4 + i] == 0x02.toByte()) {
                    status = adapter.STATUS_CLOSE
                }

                if(i ==20){
                    status = adapter.STATUS_OPEN
                }

                var item = operateStatusBody(i, status);
                itemArray.add(item)
            }
            Log.i("ooo",itemArray.toString())


            runOnUiThread {
                list =itemArray
                adapter.setItems(list)
                adapter.notifyDataSetChanged()
            }
        }

        //返回2个字节 1为柜号 2为状态
        if (msg[3] == DeviceHelp.DELIVER_GOODS_RETURN) {
            runOnUiThread {
                list.get(msg[4].toInt()).status = adapter.STATUS_OPEN
                adapter.notifyDataSetChanged()
            }
        }

    }


    override fun onPause() {
        super.onPause()
        if (countDownTimer!=null){
            countDownTimer!!.cancel();
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
}
