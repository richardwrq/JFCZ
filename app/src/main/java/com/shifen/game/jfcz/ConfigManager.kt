package com.shifen.game.jfcz

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shifen.game.jfcz.model.Banner
import com.shifen.game.jfcz.model.GameConfig
import com.shifen.game.jfcz.model.Gift


/**
 * @author caizeming
 * @email  caizeming@cvte.com
 * @date   2018/10/13
 * @description:
 */
@SuppressLint("StaticFieldLeak")
object ConfigManager {

    private const val TAG = "ConfigManager"

    private const val key_game_cofing = "game_config"

    private const val key_gift_list = "gift_list"

    private const val key_banner_list = "banner_list"

    private lateinit var mContext: Context
    private lateinit var mConfigSP: SharedPreferences
    private var mGson = Gson()

    private const val mDefaultGameConfigJsonStr = "[" +
            "{\"createTime\":0,\"gameId\":1,\"checkPointLevel\":1,\"id\":1,\"kineves\":5,\"leaveTime\":15,\"rotate\":0,\"speed\":2,\"updateTime\":0}," +
            "{\"createTime\":0,\"gameId\":2,\"checkPointLevel\":2,\"id\":2,\"kineves\":10,\"leaveTime\":13,\"rotate\":1,\"speed\":2,\"updateTime\":0}," +
            "{\"createTime\":0,\"gameId\":3,\"checkPointLevel\":3,\"id\":3,\"kineves\":15,\"leaveTime\":10,\"rotate\":1,\"speed\":3,\"updateTime\":0}]"
    private val mDefaultGameConfig: ArrayList<GameConfig>
    private var mGameConfig: List<GameConfig>? = null

    private var mGiftList: List<Gift>? = null
    private var mBannerList: List<Banner>? = null

    init {
        mDefaultGameConfig = mGson.fromJson(mDefaultGameConfigJsonStr, object : TypeToken<List<GameConfig>>() {}.type)
    }

    fun init(context: Context) {
        mContext = context
        mConfigSP = context.getSharedPreferences("config", Context.MODE_PRIVATE)
    }

    fun getGameConfig(): List<GameConfig> {
        if (mGameConfig == null) {
            val jsonStr = mConfigSP.getString(key_game_cofing, "")
            if (!jsonStr.isNullOrEmpty()) {
                mGameConfig = mGson.fromJson(jsonStr, object : TypeToken<List<GameConfig>>() {}.type)
            }
        }
        if (mGameConfig == null) {
            mGameConfig = mDefaultGameConfig
        }
        return mGameConfig!!
    }

    fun updateGameConfig(gameConfig: List<GameConfig>) {
        Log.d(TAG, "updateGameConfig")
        mGameConfig = gameConfig
        val edit = mConfigSP.edit()
        edit.putString(key_game_cofing, mGson.toJson(mGameConfig))
        edit.apply()
    }

    fun getGiftList(): List<Gift> {
        if (mGiftList == null) {
            val jsonStr = mConfigSP.getString(key_gift_list, "")
            if (!jsonStr.isNullOrEmpty()) {
                mGiftList = mGson.fromJson(jsonStr, object : TypeToken<List<Gift>>() {}.type)
            }
        }
        if (mGiftList == null) {
            mGiftList = ArrayList()
        }
        return mGiftList!!
    }

    fun updateGiftList(giftList: List<Gift>) {
        mGiftList = giftList
        mConfigSP.edit().apply {
            putString(key_gift_list, mGson.toJson(mGiftList))
        }.apply()
    }

    fun getBannerList(): List<Banner> {
        if (mBannerList == null) {
            val jsonStr = mConfigSP.getString(key_banner_list, "")
            if (!jsonStr.isNullOrEmpty()) {
                mBannerList = mGson.fromJson(jsonStr, object : TypeToken<List<Banner>>() {}.type)
            }
        }
        if (mBannerList == null) {
            mBannerList = ArrayList()
        }
        return mBannerList!!
    }

    fun updateBannerList(bannerList: List<Banner>) {
        mBannerList = bannerList
        mConfigSP.edit().apply {
            putString(key_banner_list, mGson.toJson(mBannerList))
        }.apply()
    }
}