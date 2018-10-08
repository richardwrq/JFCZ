package com.shifen.game.jfcz.model

import android.os.Parcel
import android.os.Parcelable
import java.lang.StringBuilder

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

/**
 * 登录成功返回结果
 * @property timeout Long 有效期
 * @property containerId String 货柜id
 * @property token String
 * @constructor
 */
data class LoginResult(val timeout: Long, val containerId: String, val token: String)

data class Gift(val name: String, val containerNumber: Int, val imageUrl: String, val price: Float, val challengePrice: Float) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readInt(),
            source.readString(),
            source.readFloat(),
            source.readFloat()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeInt(containerNumber)
        writeString(imageUrl)
        writeFloat(price)
        writeFloat(challengePrice)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Gift> = object : Parcelable.Creator<Gift> {
            override fun createFromParcel(source: Parcel): Gift = Gift(source)
            override fun newArray(size: Int): Array<Gift?> = arrayOfNulls(size)
        }
    }
}