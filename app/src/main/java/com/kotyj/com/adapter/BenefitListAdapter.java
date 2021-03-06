package com.kotyj.com.adapter;

import android.support.annotation.Nullable;

import com.kotyj.com.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.model.BenefitModel;

import java.util.List;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/4/4
 */
public class BenefitListAdapter extends BaseQuickAdapter<BenefitModel, BaseViewHolder> {
    private String type;

    public BenefitListAdapter(@Nullable List<BenefitModel> data, String type) {
        super(R.layout.item_benefit_list, data);
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, BenefitModel item) {

        helper.setText(R.id.tv_name, item.getMoneyType());
        helper.setText(R.id.tv_date, item.getCreateTime());
        helper.setText(R.id.tv_money, item.getTrxAmt() + "");
    }
}
