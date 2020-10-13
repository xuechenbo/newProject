package com.kotyj.com.adapter;

import android.support.annotation.Nullable;

import com.kotyj.com.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.model.CardScoreModel;
import com.kotyj.com.utils.CommonUtils;

import java.util.List;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/4/3
 */
public class ScoreHistoyListAdapter extends BaseQuickAdapter<CardScoreModel, BaseViewHolder> {
    private boolean isScore;

    public ScoreHistoyListAdapter(@Nullable List<CardScoreModel> data) {
        super(R.layout.item_score_history, data);
    }

    public void setScore(boolean score) {
        isScore = score;
    }

    @Override
    protected void convert(BaseViewHolder helper, CardScoreModel item) {
        helper.setText(R.id.tv_phone_name, isScore ? "信用卡号：" : "手机号：");
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_id_card, CommonUtils.translateShortNumber(item.getIdCard(),4,4));
        helper.setText(R.id.tv_phone, isScore ? CommonUtils.translateShortNumber(item.getBankAccount(),4,4) : CommonUtils.translateShortNumber(item.getPhone(), 3, 4));
        helper.setText(R.id.tv_time, item.getCreateTime());
    }
}
