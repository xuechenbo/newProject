package com.kotyj.com.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.model.CardPlanList;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class PlanDetailAdapter extends BaseQuickAdapter<CardPlanList, BaseViewHolder> {
    boolean isQYK;
    boolean isShow = false;
    int mCount = 5;


    public void setShowCount(boolean isShow) {
        this.isShow = isShow;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return isShow ? mCount : super.getItemCount();

    }

    public PlanDetailAdapter(int layoutResId, @Nullable List<CardPlanList> data, boolean isQYK) {
        super(layoutResId, data);
        this.isQYK = isQYK;
    }

    @Override
    protected void convert(BaseViewHolder helper, CardPlanList item) {
        String consumeOrRepay = item.getFromMoney().setScale(2).toString();

        if (isQYK) {
            if (item.getType().equals("sale") || item.getType().equals("pay")) {
                helper.setGone(R.id.ll_area, true);
                helper.setGone(R.id.ll_industry, true);
            } else {
                helper.setGone(R.id.ll_area, false);
                helper.setGone(R.id.ll_industry, false);
            }
        } else {
            if (item.getType().equals("sale")) {
                helper.setGone(R.id.ll_area, true);
                helper.setGone(R.id.ll_industry, true);
            } else {
                helper.setGone(R.id.ll_area, false);
                helper.setGone(R.id.ll_industry, false);
            }
        }
        helper.setText(R.id.tv_area, item.getArea())
                .setText(R.id.industry, item.getIndustry());


        if (isQYK) {
            if ("pay".equals(item.getType())) {
                helper.setText(R.id.type_tv, "消费");
                helper.setBackgroundRes(R.id.type_tv, R.drawable.shape_solid_orange_corner_5);
            } else if ("sale".equals(item.getType())) {
                helper.setText(R.id.type_tv, "手续费");
                helper.setBackgroundRes(R.id.type_tv, R.drawable.shape_solid_dark_green_corner_5);
            } else {
                helper.setText(R.id.type_tv, "还款");
                helper.setBackgroundRes(R.id.type_tv, R.drawable.shape_solid_blue_corner_5);
            }
        } else {
            if ("sale".equals(item.getType())) {
                helper.setText(R.id.type_tv, "消费");
                helper.setBackgroundRes(R.id.type_tv, R.drawable.shape_solid_orange_corner_5);
            } else {
                helper.setText(R.id.type_tv, "还款");
                helper.setBackgroundRes(R.id.type_tv, R.drawable.shape_solid_blue_corner_5);
            }
        }
        String status = item.getStatus();
        int dealStatusDrawableId = R.drawable.icon_wait_deal;
        switch (status) {
            case "10A":
            case "10D":
                dealStatusDrawableId = R.drawable.icon_wait_deal;
                break;
            case "10B":
                dealStatusDrawableId = R.drawable.icon_success_deal;
                break;
            case "10C":
                dealStatusDrawableId = R.drawable.icon_plan_detail_fail;
                break;
        }

        helper.setImageResource(R.id.iv_payStatus, dealStatusDrawableId);
        helper.setText(R.id.tv_money, consumeOrRepay);
        SimpleDateFormat dateFormat = null;
        if (isQYK) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        helper.setText(R.id.tv_date, dateFormat.format(new Date(item.getPlanTime())) + "");
    }
}
