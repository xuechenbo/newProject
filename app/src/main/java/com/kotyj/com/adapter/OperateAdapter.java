package com.kotyj.com.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.model.OperateModel;
import com.kotyj.com.utils.DateUtil;

public class OperateAdapter extends BaseQuickAdapter<OperateModel, BaseViewHolder> {

    public OperateAdapter() {
        super(R.layout.item_operate);
    }


    @Override
    protected void convert(BaseViewHolder helper, OperateModel item) {
        helper.setText(R.id.tv_title, item.getTitle())
                .setText(R.id.tv_time, DateUtil.formatDateToHM_(item.getCreateTime().getTime()));
    }


}
