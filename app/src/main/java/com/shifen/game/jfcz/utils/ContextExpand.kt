package com.shifen.game.jfcz.utils

import android.content.Context
import android.content.SharedPreferences

private const val CONFIG_KEY = "config_key"

const val TOKEN = "token"

fun Context.putConfig(block: (SharedPreferences.Editor) -> Unit) {
    val editor = getSharedPreferences(CONFIG_KEY, Context.MODE_PRIVATE).edit()
    block.invoke(editor)
    editor.apply()
}

fun Context.getConfig() : SharedPreferences {
    return getSharedPreferences(CONFIG_KEY, Context.MODE_PRIVATE)
}