package com.kotyj.com.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.model.ClientModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.DateUtil;
import com.kotyj.com.utils.Utils;

import java.util.List;

public class ClientListAdapter extends BaseQuickAdapter<ClientModel, BaseViewHolder> {


    public ClientListAdapter(@Nullable List<ClientModel> data) {
        super(R.layout.item_client_list, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, ClientModel item) {
        baseViewHolder
                .addOnClickListener(R.id.takePhone)
                .setText(R.id.tv_level, Utils.GetLeveName(item.getLEVEL()))
                .setVisible(R.id.takePhone, item.getIsDirect().equals("1") ? true : false)
                .setText(R.id.tv_name, item.getMerchant_cn_name().length() == 11 ? "未实名" : (item.getIsDirect().equals("1") ? item.getMerchant_cn_name() : CommonUtils.translateShortNumber(item.getMerchant_cn_name(), 1, 0)))
                .setText(R.id.tv_phone, item.getIsDirect().equals("1") ? item.getPhone() : CommonUtils.translateShortNumber(item.getPhone(), 3, 4))
                .setText(R.id.tv_create_time, DateUtil.formatDateYMD(item.getCREATE_TIME().getTime()));

    }
}
