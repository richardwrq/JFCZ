package com.shifen.game.jfcz.model

/**
 * 响应结果
 * @param T
 * @property code Int
 * @property data T
 * @property message String
 * @property version String
 * @constructor
 */
data class Response<T>(val code: Int, val data: T, val message: String, val version: String)