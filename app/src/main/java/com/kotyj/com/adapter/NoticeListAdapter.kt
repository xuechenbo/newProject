package com.kotyj.com.adapter

import android.graphics.Color
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotyj.com.R
import com.kotyj.com.model.NoticeModel
import com.kotyj.com.utils.DateUtil


class NoticeListAdapter(data: List<NoticeModel>) : BaseQuickAdapter<NoticeModel, BaseViewHolder>(R.layout.item_notice_list, data) {


    override fun convert(helper: BaseViewHolder, item: NoticeModel) {
        helper.setText(R.id.tv_title, item.title)
                .setText(R.id.tv_content, item.content)
                .setText(R.id.tv_date, DateUtil.formatDateToHM_point(item.updateDate.time))
                .setText(R.id.no_read, if (item.hasRead == 0) "未读" else "已读")
                .setTextColor(R.id.no_read, if (item.hasRead == 0) mContext.resources.getColor(R.color.theme_fontColor) else Color.WHITE)
    }
}
