package com.shifen.game.jfcz.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.shifen.game.jfcz.R
import com.youth.banner.loader.ImageLoader

class GlideImageLoader : ImageLoader() {

    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        val roundedCorners = RoundedCorners(15)
        val options = RequestOptions.bitmapTransform(roundedCorners)
                .override(context.resources.getDimensionPixelSize(R.dimen.ad_banner_width), context.resources.getDimensionPixelSize(R.dimen.ad_banner_height))
        Glide.with(context).load(path).apply(options).into(imageView)
    }
}