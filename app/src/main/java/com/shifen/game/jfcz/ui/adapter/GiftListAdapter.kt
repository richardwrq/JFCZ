package com.shifen.game.jfcz.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import com.shifen.game.jfcz.R
import com.shifen.game.jfcz.model.Gift
import com.shifen.game.jfcz.ui.viewholder.BaseViewHolder

class GiftListAdapter : BaseAdapter<Gift, BaseViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_gift_list, p0, false)
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(p0: BaseViewHolder, p1: Int) {
        super.onBindViewHolder(p0, p1)
        (p0.itemView as Button).text = list[p1].number.toString()
        //p0.itemView.isEnabled = list[p1].status != 2

        p0.itemView.isEnabled = (list[p1].status == 0) && ( list[p1].goodsList[0].count> 0)
        p0.itemView.isSelected = list[p1].status == 100
    }

}