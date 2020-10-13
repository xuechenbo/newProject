package com.kotyj.com.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.LogUtils;

import java.util.List;


public class PicAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private int checkedPosi = 0;

    public PicAdapter(@Nullable List<String> list) {
        super(R.layout.item_image, list);
    }


    public void setCheckedPosi(int checkedPosi) {
        this.checkedPosi = checkedPosi;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(final BaseViewHolder helper, String item) {
        LogUtils.i("item" + item);
        helper.addOnClickListener(R.id.img);
        GlideUtils.loadImage(mContext, item, (ImageView) helper.getView(R.id.img));
    }
}
