package com.kotyj.com.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.model.BindCard;
import com.kotyj.com.utils.Utils;

import java.util.List;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/5/9
 */
public class ManagerCreditAdapter extends BaseQuickAdapter<BindCard, BaseViewHolder> {
    boolean is2Pay;
    private boolean isVip;
    private boolean isCards;
    private String makeType;

    public ManagerCreditAdapter(@Nullable List<BindCard> data) {
        super(R.layout.item_manager_bank, data);
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public void setIs2Pay(boolean is2Pay) {
        this.is2Pay = is2Pay;
    }

    public void setMakeType(String makeType) {
        this.makeType = makeType;
    }


    @Override
    protected void convert(BaseViewHolder helper, BindCard item) {


        helper.addOnClickListener(R.id.tv_unbind);
        helper.addOnClickListener(R.id.btn_make);
        helper.setGone(R.id.btn_make, !isVip);
        helper.setGone(R.id.ll_make, !isCards);
        helper.setText(R.id.btn_make, makeType);
        if (item.getPlanCount() > 0 && !is2Pay) {
            helper.setText(R.id.btn_make, "计划进行中");
        }

        String banNum = item.getBANK_ACCOUNT();
        if (banNum.length() > 4) {
            String bankNum1 = banNum.substring(0, 4);
            String bankNum2 = banNum.substring(banNum.length() - 4, banNum.length());
            helper.setText(R.id.tv_bank_account, bankNum1 + " **** **** " + bankNum2);
        }
        helper.setText(R.id.tv_limit, item.getLIMIT_MONEY())
                .setText(R.id.tv_bill_day, item.getBILL_DAY())
                .setText(R.id.tv_DayNum, item.getDay())
                .setText(R.id.tv_pay_day, "-" + item.getREPAYMENT_DAY());
        String bankName = item.getBANK_NAME();
        Utils.initBankCodeColorIcon(bankName, (ImageView) helper.getView(R.id.iv_bank_icon), mContext);
        helper.setText(R.id.tv_bank_name, item.getShort_cn_name());
        if (helper.getAdapterPosition() % 3 == 0) {
            helper.setBackgroundRes(R.id.cl_card, R.drawable.shape_solid_red_top_left_right);
            helper.setBackgroundRes(R.id.btn_make, R.drawable.button_bank_item_make1);
        } else if (helper.getAdapterPosition() % 3 == 1) {
            helper.setBackgroundRes(R.id.cl_card, R.drawable.shape_solid_blue_top_left_right);
            helper.setBackgroundRes(R.id.btn_make, R.drawable.button_bank_item_make2);
        } else {
            helper.setBackgroundRes(R.id.cl_card, R.drawable.shape_solid_green_top_left_right);
            helper.setBackgroundRes(R.id.btn_make, R.drawable.button_bank_item_make3);
        }

    }
}
