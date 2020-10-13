package com.kotyj.com.activity.wkyk.newwkyk;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.model.PreviewPlanModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShowPlanDetailsDialog extends DialogFragment {
    Unbinder unbinder;
    @BindView(R.id.tv_planTime)
    TextView tvPlanTime;
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.tv_channelName)
    TextView tvChannelName;
    @BindView(R.id.tv_inputMoney)
    TextView tvInputMoney;
    @BindView(R.id.tv_YuMoney)
    TextView tvYuMoney;
    @BindView(R.id.tv_feeMoney)
    TextView tvFeeMoney;
    @BindView(R.id.tv_xfNum)
    TextView tvXfNum;
    @BindView(R.id.tv_hkNum)
    TextView tvHkNum;
    @BindView(R.id.tv_rate)
    TextView tvRate;
    @BindView(R.id.tv_timeMoney)
    TextView tvTimeMoney;
    @BindView(R.id.tv_xfTotalMoney)
    TextView tvXfTotalMoney;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.tv_title_1)
    TextView tvTitle1;
    @BindView(R.id.tv_no)
    TextView tvNo;
    @BindView(R.id.tv_submitPlan)
    TextView tvSubmitPlan;
    private PreviewPlanModel model;

    public static ShowPlanDetailsDialog getIntence(PreviewPlanModel model) {
        ShowPlanDetailsDialog dialog = new ShowPlanDetailsDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", model);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.custom_Dialog);
        model = (PreviewPlanModel) getArguments().getSerializable("model");
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_show_plan_details, container);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {

        if (model.isZhia()) {
            tvFeeMoney.setVisibility(View.GONE);
            tvTitle1.setText("手续费");
            tvYuMoney.setText(model.getTotalFee() + "元");
        } else {
            tvYuMoney.setText(model.getWorkingFund() + "元");
        }

        tvFeeMoney.setText("手续费:" + model.getTotalFee() + "元");
        tvPlanTime.setText(model.getStartDate() + "-" + model.getEndDate());
        tvChannelName.setText(model.getChannelName());
        tvInputMoney.setText(model.getRepayAmount() + "元");


        tvXfNum.setText("消费笔数:" + model.getTotalXfNum() + "笔");

        tvHkNum.setText("还款笔数:" + model.getHkBs() + "笔");


        tvRate.setText("消费费率:" + (Double.parseDouble(model.getRate()) * 100) + "%");
        tvTimeMoney.setText("还款手续费:" + model.getSingMoney() + "/笔");

        tvXfTotalMoney.setText(model.getTotalXfMoney() + "元");

        tvArea.setText(model.getArea().get("province").getDivisionName() + "-" + model.getArea().get("city").getDivisionName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_no, R.id.tv_submitPlan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_no:
                dismiss();
                break;
            case R.id.tv_submitPlan:
                if (onShowSuccess != null) {
                    onShowSuccess.onShowMsg();
                    dismiss();
                }
                break;
        }
    }

    OnShowSuccess onShowSuccess;

    public interface OnShowSuccess {
        void onShowMsg();
    }


    public void setOnShowSuccessListener(OnShowSuccess onShowSuccess) {
        this.onShowSuccess = onShowSuccess;
    }
}
