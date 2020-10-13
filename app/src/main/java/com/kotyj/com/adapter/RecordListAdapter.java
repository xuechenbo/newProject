package com.kotyj.com.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.model.RecordListModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.StringUtil;

import java.util.List;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/8/20
 */
public class RecordListAdapter extends BaseQuickAdapter<RecordListModel, BaseViewHolder> {

    public RecordListAdapter(@Nullable List<RecordListModel> data) {
        super(R.layout.item_record_list, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, RecordListModel item) {
        int position = helper.getAdapterPosition();
        if (position == 0) {
            helper.setGone(R.id.cl_no_1, true);
            helper.setGone(R.id.cl_no, false);
            GlideUtils.loadAvatar(mContext, item.getHEAD_URL(), (ImageView) helper.getView(R.id.iv_pic_1));
            helper.setText(R.id.tv_phone_1, CommonUtils.translateShortNumber(item.getPHONE(), 3, 4));
            helper.setText(R.id.tv_number_1, CommonUtils.formatNewWithScale(item.getCount().toString(), 0) + "");
        } else {
            helper.setGone(R.id.cl_no_1, false);
            helper.setGone(R.id.cl_no, true);
            if (position == 1 || position == 2) {
                helper.setGone(R.id.tv_no, false);
                helper.setGone(R.id.iv_no, true);
                helper.setGone(R.id.iv_no_4, false);
                helper.setGone(R.id.cl_no_2, true);
                helper.setImageResource(R.id.iv_huangguan_2, position == 1 ? R.drawable.record_2_icon : R.drawable.record_3_icon);
                helper.setImageResource(R.id.iv_number_2, position == 1 ? R.drawable.record_no_2 : R.drawable.record_no_3);
                helper.setImageResource(R.id.iv_no, position == 1 ? R.drawable.record_2 : R.drawable.record_3);
                helper.setBackgroundRes(R.id.iv_no_small, position == 1 ? R.drawable.shape_strike_ring_2_circle : R.drawable.shape_strike_tong_2_circle);
            } else {
                helper.setGone(R.id.iv_no_4, true);
                helper.setGone(R.id.cl_no_2, false);
                helper.setGone(R.id.tv_no, true);
                helper.setGone(R.id.iv_no, false);
                helper.setText(R.id.tv_no, "NO." + (position + 1));
            }

            GlideUtils.loadAvatar(mContext, item.getHEAD_URL(), (ImageView) helper.getView(R.id.iv_no_small));
            GlideUtils.loadAvatar(mContext, item.getHEAD_URL(), (ImageView) helper.getView(R.id.iv_no_4));
            if (!StringUtil.isEmpty(item.getPHONE())) {
                helper.setText(R.id.tv_phone, CommonUtils.translateShortNumber(item.getPHONE(), 3, 4));
            }
            helper.setText(R.id.tv_number, CommonUtils.formatNewWithScale(item.getCount().toString(), 0) + "");

        }

        helper.setText(R.id.tv_level_1_type_title, "直推");
        helper.setText(R.id.tv_level_1_type_title_2, "人");
        helper.setText(R.id.tv_type_title, "直推");
        helper.setText(R.id.tv_type_title_2, "人");


    }
}