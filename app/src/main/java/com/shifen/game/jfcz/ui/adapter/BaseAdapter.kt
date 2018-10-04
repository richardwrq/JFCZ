package com.shifen.game.jfcz.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.shifen.game.jfcz.ui.viewholder.BaseViewHolder

abstract class BaseAdapter<T, VH : BaseViewHolder> : RecyclerView.Adapter<VH>() {

    protected val list = ArrayList<T>()
    var onItemClickListener: ((view: View, position: Int) -> Unit)? = null

    fun getItem(index: Int): T {
        return list[index]
    }

    fun addItems(items: List<T>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: T) {
        list.add(item)
        notifyDataSetChanged()
    }

    fun removeItem(item: T): Boolean {
        val removed = list.remove(item)
        notifyDataSetChanged()
        return removed
    }

    fun removeItemWithIndex(index: Int): T {
        val removed = list.removeAt(index)
        notifyDataSetChanged()
        return removed
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: VH, p1: Int) {
        p0.itemView.setOnClickListener {
            onItemClickListener?.invoke(p0.itemView, p0.adapterPosition)
        }
    }

}