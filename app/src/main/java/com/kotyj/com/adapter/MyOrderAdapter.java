package com.kotyj.com.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.model.OrderModel;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.Utils;

import java.util.List;

public class MyOrderAdapter extends BaseQuickAdapter<OrderModel, BaseViewHolder> {
    Context context;

    public MyOrderAdapter(@Nullable List<OrderModel> data, Context context) {
        super(R.layout.item_order, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, OrderModel item) {
        helper.setText(R.id.tv_status, Utils.getPayStatus(item.getStatus()));
        if (item.getStatus().equals("10A")) {
            helper.setText(R.id.my_service, "立即付款");
            helper.setBackgroundRes(R.id.my_service, R.drawable.shape_theme_pink_45)
                    .setTextColor(R.id.my_service, context.getResources().getColor(R.color.white));
        } else {
            helper.setText(R.id.my_service, "联系客服");
            helper.setBackgroundRes(R.id.my_service, R.drawable.shape_strike_theme_pink_45)
                    .setTextColor(R.id.my_service, context.getResources().getColor(R.color.theme_fontColor));
        }


        helper.setText(R.id.tv_time, "订单编号:" + item.getId())
                .setText(R.id.tv_product_name, item.getGoodsName())
                .setText(R.id.tv_price, item.getGoodsPrice() + "元" + "+" + item.getGoodPoint() + "积分")
                .setText(R.id.tv_price_total, "应付:￥" + item.getPay() + "元" + "+" + item.getPoint() + "积分")
                .setText(R.id.tv_amount_top, "x" + item.getGoodsCount())
                .setText(R.id.tv_unit_price, item.getGoodsPrice() + "+" + item.getGoodPoint() + "积分")
                .setText(R.id.tv_size, "规格:" + item.getGoodsSpecification())
        ;


        GlideUtils.loadImage(mContext, item.getGoodsImage(), (ImageView) helper.getView(R.id.iv_product));
        helper.addOnClickListener(R.id.my_service);

    }


}
