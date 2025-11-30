package com.example.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

public class ImageLoader {
    private static final RequestOptions AVATAR_OPTIONS = new RequestOptions()
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
            .override(120, 120)
            .dontAnimate();

    public static void loadAvatar(Context context, int resourceId, ImageView imageView) {
        Glide.with(context)
                .load(resourceId)
                .apply(AVATAR_OPTIONS)
                .into(imageView);
    }

    public static void loadAvatar(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(AVATAR_OPTIONS)
                .into(imageView);
    }
    public static void preloadAvatar(Context context, int resourceId) {
        Glide.with(context)
                .load(resourceId)
                .apply(AVATAR_OPTIONS)
                .preload();
    }
    // 清理内存缓存
    public static void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }
    // 在内存紧张时调用
    public static void trimMemory(Context context, int level) {
        Glide.get(context).trimMemory(level);
    }
}