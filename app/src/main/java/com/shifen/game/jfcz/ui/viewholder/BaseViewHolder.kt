package com.shifen.game.jfcz.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View

open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun <T : View> getView(id: Int): T {
        return itemView.findViewById(id)
    }
}