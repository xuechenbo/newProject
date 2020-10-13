package com.kotyj.com.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.kotyj.com.R;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class GlideUtils {
    //TODO 改到了Glide4   new RequestOptions()
    //https://www.jianshu.com/p/625faa5e9560

    private GlideUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static void loadImage(Context mContext, String url, ImageView imageView) {
        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions().error(R.drawable.no_img))
                .into(imageView);
    }


    //加载头像
    public static void loadAvatar(Context mContext, String url, ImageView avatar) {
        if (mContext != null) {
            //TODO placeholder 占位图
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.head)
                    .error(R.drawable.head)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .circleCrop();
            Glide.with(mContext)
                    .load(StringUtil.isEmpty(url) ? R.drawable.head : url)
                    .apply(options)
                    .into(avatar);
        }
    }

    public static void loadNoChacheImage(Context context, String url, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .error(R.drawable.no_img)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(imageView);
    }


    //圆角商品
    public static void LoadShopItem(Context context, String url, ImageView imageView) {
        MultiTransformation mation5 = new MultiTransformation(
                new RoundedCornersTransformation(80, 0, RoundedCornersTransformation.CornerType.ALL));
        RequestOptions options = new RequestOptions()
                .error(R.drawable.no_img)
                .placeholder(R.drawable.no_img);
        Glide.with(context)
                .load(url)
                .apply(RequestOptions.bitmapTransform(mation5))
                .apply(options)
                .into(imageView);
    }


    //自定义传入格式
    public static void load(Context context, String url, ImageView imageView, RequestOptions options) {
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(imageView);
    }
}
