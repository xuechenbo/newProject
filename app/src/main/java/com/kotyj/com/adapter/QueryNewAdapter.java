package com.kotyj.com.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.model.QueryModel;
import com.kotyj.com.utils.DateUtil;
import com.kotyj.com.utils.LogUtils;

import java.util.List;


public class QueryNewAdapter extends BaseQuickAdapter<QueryModel, BaseViewHolder> {
    private List<QueryModel> mList;

    public QueryNewAdapter(@Nullable List<QueryModel> data) {
        super(R.layout.item_trade_list, data);
        mList = data;
    }


    @Override
    protected void convert(BaseViewHolder helper, QueryModel item) {
        int position = helper.getAdapterPosition();
        LogUtils.i("position=" + position);
        helper.setText(R.id.tv_date, DateUtil.formatDate_Sdftwo(item.getCREATE_TIME().getTime()));
        helper.setText(R.id.tv_name, item.getCde_name());
        helper.setText(R.id.tv_money, item.getTRX_AMT() + "");
    }

}
