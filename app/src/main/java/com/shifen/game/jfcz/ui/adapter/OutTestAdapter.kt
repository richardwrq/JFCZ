package com.shifen.game.jfcz.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.operateStatusBody
import com.shifen.game.jfcz.ui.OutTestActivity
import com.shifen.game.jfcz.ui.viewholder.BaseViewHolder

class OutTestAdapter : BaseAdapter<operateStatusBody, BaseViewHolder>() {

    open var STATUS_OPEN = 100
    open var STATUS_CLOSE = 101

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_out_test, p0, false)
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(p0: BaseViewHolder, p1: Int) {
        super.onBindViewHolder(p0, p1)
        (p0.itemView as Button).text = (list[p1].number+1).toString()
        if (list[p1].status == STATUS_OPEN){
            p0.itemView.setBackgroundColor(Color.rgb(234, 226, 70))
        }
    }

}