package com.kotyj.com.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.model.WithdrawModel;
import com.kotyj.com.utils.DateUtil;

import java.util.List;


public class WithdrawListAdapter extends BaseQuickAdapter<WithdrawModel, BaseViewHolder> {
    public WithdrawListAdapter(@Nullable List<WithdrawModel> data) {
        super(R.layout.item_withdraw_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WithdrawModel item) {
        if (item.getCreateTime() != null) {
            helper.setText(R.id.tv_date_start, DateUtil.formatDateToHMS(item.getCreateTime().getTime()));
        }
        helper.setText(R.id.tv_money, "-" + item.getTrxAmt() + "");
        String status = item.getStatus();
        String statusName = "";
        switch (status) {
            case "10A":
                statusName = "失败";
                break;
            case "10B":
                statusName = "审核中";
                break;
            case "10C":
                statusName = "成功";
                break;
            case "10D":
                statusName = "失败";
                break;
        }
        helper.setText(R.id.tv_status, statusName);

    }
}
