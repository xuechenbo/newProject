package com.kotyj.com.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.model.PlanItem;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.DateUtil;

import java.util.List;


public class LookPlanAdapter extends BaseQuickAdapter<PlanItem, BaseViewHolder> {
    boolean isMed;

    public LookPlanAdapter(@Nullable List<PlanItem> data, boolean isMed) {
        super(R.layout.item_look_plan, data);
        this.isMed = isMed;
    }

    @Override
    protected void convert(BaseViewHolder helper, PlanItem item) {
        String status = item.getPlanStatus();
        if ("10A".equals(status)) {
            status = "未开始";
        } else if ("10B".equals(status)) {
            status = "执行中";
        } else if ("10D".equals(status)) {
            status = "已暂停";
        } else if ("10C".equals(status)) {
            status = "失败";
        } else if ("10E".equals(status)) {
            status = "已完成";
        }

        if (isMed) {
            helper.setGone(R.id.rll_med, true)
                    .setText(R.id.tv_name, item.getMERCHANT_CN_NAME())
                    .setGone(R.id.vie1, true);
        }


        String progressNum = item.getPlanProgress().replace("%", "");
        helper.setProgress(R.id.progress, Integer.parseInt(progressNum));

        helper.setText(R.id.tv_dealStatus, status);
        helper.setText(R.id.tv_shouldPayNow, "本期应还  " + CommonUtils.format_point00(item.getShouldPayNow()));
        helper.setText(R.id.tv_paidAmountNow, "本期已还  " + CommonUtils.format_point00(item.getPaidAmountNow()));
        helper.setText(R.id.tv_progressNum, item.getPlanProgress());
        helper.setText(R.id.tv_planCycle, "计划周期  " + item.getPlanCycle());
        helper.setText(R.id.tv_planCreateTime, "创建时间  " + DateUtil.formatHMS(item.getCreateTime()));
        helper.addOnClickListener(R.id.btn_detail);
        helper.setVisible(R.id.tv_planType, item.getType().equals("10C") ? true : false);
    }
}
