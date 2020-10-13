package com.kotyj.com.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.model.ShoppingMalModel;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.Utils;

import java.util.List;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/8/20
 */
public class ShoppingMalAdapter extends BaseQuickAdapter<ShoppingMalModel, BaseViewHolder> {

    public ShoppingMalAdapter(@Nullable List<ShoppingMalModel> data) {
        super(R.layout.item_shopping_mall, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final ShoppingMalModel item) {
        helper.setText(R.id.name, item.getName())
                .setText(R.id.price, Utils.getShopPayType(item.getPrice(), item.getPoint()));

        GlideUtils.LoadShopItem(mContext, item.getImage(), (ImageView) helper.getView(R.id.image));

    }

}
