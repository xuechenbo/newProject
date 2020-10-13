package com.kotyj.com.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.kotyj.com.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.MyApplication;
import com.kotyj.com.model.BindCard;
import com.kotyj.com.utils.Utils;

import java.util.List;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/4/1
 */
public class BankCardAdpter extends BaseQuickAdapter<BindCard, BaseViewHolder> {
    public BankCardAdpter(@Nullable List<BindCard> data) {
        super(R.layout.item_bank_credit, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BindCard item) {
        String banNum = item.getBANK_ACCOUNT();
        if (banNum.length() > 4) {
            String bankNum2 = banNum.substring(banNum.length() - 4, banNum.length());
            helper.setText(R.id.tv_bank_account, "尾号 " + bankNum2);
        }
        helper.setText(R.id.tv_name, MyApplication.getBankName(item.getBANK_NAME()));
        Utils.initBankCodeColorIcon(item.getBANK_NAME(), (ImageView) helper.getView(R.id.iv_bank_icon), mContext);
    }
}
