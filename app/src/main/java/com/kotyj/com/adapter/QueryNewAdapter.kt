package com.kotyj.com.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotyj.com.R
import com.kotyj.com.model.QueryModel
import com.kotyj.com.utils.DateUtil


class QueryNewAdapter(val mList: List<QueryModel>) : BaseQuickAdapter<QueryModel, BaseViewHolder>(R.layout.item_trade_list, mList) {

    override fun convert(helper: BaseViewHolder, item: QueryModel) {
        helper.setText(R.id.tv_date, DateUtil.formatDate_Sdftwo(item.creatE_TIME.time))
        helper.setText(R.id.tv_name, item.cde_name)
        helper.setText(R.id.tv_money, "${item.trX_AMT}")
    }

}
