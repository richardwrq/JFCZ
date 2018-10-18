package com.shifen.game.jfcz.model

import android.os.Parcel
import android.os.Parcelable

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

/**
 *
 * @property id Long
 * @property containerId String
 * @property url String
 * @property type Int
 * @property selected Int
 * @property sort Int
 * @property createTime String
 * @property updateTime String
 * @constructor
 */
data class Banner(val id: Long,
                  val containerId: String,
                  val url: String,
                  val type: Int,
                  val selected: Int,
                  val sort: Int,
                  val createTime: String,
                  val updateTime: String)

/**
 *
 * @property id Long
 * @property containerId String
 * @property gridId String
 * @property goodsId String
 * @property position Int
 * @property gamePrice String
 * @property price String
 * @property description String
 * @property pictureUrl String
 * @property count Int
 * @property sort Int
 * @property createTime String
 * @property updateTime String
 * @constructor
 */
data class Goods(val id: Long,
                 val containerId: String,
                 val gridId: String,
                 val goodsId: String,
                 val position: Int,
                 val gamePrice: String,
                 val price: String,
                 val description: String,
                 val pictureUrl: String,
                 val count: Int,
                 val sort: Int,
                 val createTime: String,
                 val updateTime: String) : Parcelable {
    constructor(source: Parcel) : this(
            source.readLong(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readInt(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(containerId)
        writeString(gridId)
        writeString(goodsId)
        writeInt(position)
        writeString(gamePrice)
        writeString(price)
        writeString(description)
        writeString(pictureUrl)
        writeInt(count)
        writeInt(sort)
        writeString(createTime)
        writeString(updateTime)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Goods> = object : Parcelable.Creator<Goods> {
            override fun createFromParcel(source: Parcel): Goods = Goods(source)
            override fun newArray(size: Int): Array<Goods?> = arrayOfNulls(size)
        }
    }
}

/**
 *
 * @property id Long
 * @property containerId String
 * @property gridId String
 * @property number Long
 * @property description String
 * @property status Int
 * @property sort Int
 * @property createTime String
 * @property updateTime String
 * @property goodsList MutableList<Goods>
 * @constructor
 */
data class Gift(val id: Long,
                val containerId: String,
                val gridId: String,
                val number: Long,
                val description: String,
                val status: Int,
                val sort: Int,
                val createTime: String,
                val updateTime: String,
                val goodsList: MutableList<Goods>) : Parcelable {

    constructor(source: Parcel) : this(
            source.readLong(),
            source.readString(),
            source.readString(),
            source.readLong(),
            source.readString(),
            source.readInt(),
            source.readInt(),
            source.readString(),
            source.readString(),
            ArrayList<Goods>().apply { source.readList(this, Goods::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(containerId)
        writeString(gridId)
        writeLong(number)
        writeString(description)
        writeInt(status)
        writeInt(sort)
        writeString(createTime)
        writeString(updateTime)
        writeList(goodsList)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Gift> = object : Parcelable.Creator<Gift> {
            override fun createFromParcel(source: Parcel): Gift = Gift(source)
            override fun newArray(size: Int): Array<Gift?> = arrayOfNulls(size)
        }
    }
}

/**
 *
 * @property userId String
 * @property gameSessionId String
 * @constructor
 */
data class OrderStatus(val userId: String, val gameSessionId: String)

/**
 *
 * @property id Long
 * @property containerId String
 * @property gridLineNumber Int
 * @property musicType Int
 * @property musicUrl String
 * @property url String
 * @property gridVersion String
 * @property goodsVersion String
 * @property createTime String
 * @property updateTime String
 * @constructor
 */
data class Config(val id: Long, val containerId: String, val gridLineNumber: Int, val musicType: Int,
                  val musicUrl: String, val url: String, val gridVersion: String, val goodsVersion: String, val gameVersion: String,
                  val createTime: String, val updateTime: String)


/**
 *
 * @property createTime Long
 * @property checkPointLevel Int
 * @property gameId Int
 * @property kineves Int
 * @property leaveTime Int
 * @property rotate Int
 * @property speed Int
 * @property updateTime Long
 * @constructor
 */
data class GameConfig(val createTime: Long, val checkPointLevel: Int, val gameId: Int, val kineves: Int,
                      val leaveTime: Int, val rotate: Int, val speed: Int, val updateTime: Long)

/**
 *
 * @property t Long
 * @property transferId String
 * @constructor
 */
data class OrderStatusRequestBody(val t: Long, val transferId: String)

/**
 *
 * @property deviceId String
 * @property deviceToken String
 * @constructor
 */
data class PushBindRequestBody(val deviceId: String, val deviceToken: String)

data class updateGoodsBody(var gridId:String,var goodsId:String)