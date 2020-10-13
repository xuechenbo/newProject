package com.kotyj.com.activity.wkyk.newwkyk;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.model.BindCard;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BankCardDialog extends DialogFragment {


    @BindView(R.id.tv_yk_plan_tip)
    TextView tvYkPlanTip;
    @BindView(R.id.tv_channels_plan_tip)
    TextView tvChannelsPlanTip;
    @BindView(R.id.ll_make_channels_design)
    RelativeLayout llMakeChannelsDesign;
    @BindView(R.id.tv_qyk_plan_tip)
    TextView tvQykPlanTip;
    @BindView(R.id.ll_make_qyk_design)
    RelativeLayout llMakeQykDesign;
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.view2)
    View view2;
    Unbinder unbinder;
    private BindCard model;

    public static BankCardDialog getIntence(BindCard model) {
        BankCardDialog dialog = new BankCardDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", model);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.custom_Dialog);
        model = (BindCard) getArguments().getSerializable("model");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_bank_card, container);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        if (model == null) {
            return;
        }
        //中介
        if (model.getMakeType()) {
            llMakeChannelsDesign.setVisibility(View.GONE);
            llMakeQykDesign.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
        }

        if (model.getPlanCount() > 0) {
            switch (model.getType()) {
                case "10B":
                    //预留还款
                    tvYkPlanTip.setText("进行中");
                    break;
                case "10C":
                    //全额还
                    tvQykPlanTip.setText("进行中");
                    break;
                case "10D":
                    //多通道
                    tvChannelsPlanTip.setText("进行中");
                    break;
            }
        }
    }

    @OnClick({R.id.ll_make_yk_design, R.id.ll_make_qyk_design, R.id.ll_make_channels_design, R.id.tv_look_plan, R.id.tv_look_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_make_yk_design:
                if (model.getPlanCount() > 0) {
                    final Dialog dialog = new Dialog(getActivity(), R.style.MyProgressDialog);
                    dialog.setContentView(R.layout.dialog_plan_tip);
                    TextView bt_known = (TextView) dialog.findViewById(R.id.btn);
                    dialog.setCanceledOnTouchOutside(true);
                    bt_known.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return;
                }
                goMakeDesign(false, false);
                dismiss();
                break;
            case R.id.ll_make_qyk_design:
                String level = StorageCustomerInfo02Util.getInfo("level", getActivity());
                String isPay = StorageCustomerInfo02Util.getInfo("isPay", getActivity());
                if (isPay.equals("1") && Integer.parseInt(level) > 1) {
                    if (model.getPlanCount() > 0) {
                        final Dialog dialog = new Dialog(getActivity(), R.style.MyProgressDialog);
                        dialog.setContentView(R.layout.dialog_plan_tip);
                        TextView bt_known = (TextView) dialog.findViewById(R.id.btn);
                        dialog.setCanceledOnTouchOutside(true);
                        bt_known.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        return;
                    }
                    goMakeDesign(true, false);
                    dismiss();
                } else {
                    ViewUtils.makeToast(getActivity(), "付费VIP及以上级别可用", 1200);
                }


                break;
            case R.id.ll_make_channels_design:

                ViewUtils.makeToast(getActivity(), "暂未开放", 1200);

//                if (model.getPlanCount() > 0) {
//                    final Dialog dialog = new Dialog(getActivity(), R.style.MyProgressDialog);
//                    dialog.setContentView(R.layout.dialog_plan_tip);
//                    TextView bt_known = (TextView) dialog.findViewById(R.id.btn);
//                    dialog.setCanceledOnTouchOutside(true);
//                    bt_known.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show();
//                    return;
//                }
//                //TODO 多通道
//                goMakeDesign(false, true);
//                dismiss();
                break;
            case R.id.tv_look_plan:
                goLookPlan();
                dismiss();
                break;
            case R.id.tv_look_data:
                goLookData();
                dismiss();
                break;
        }
    }

    private void goMakeDesign(boolean zhia, boolean isChannel) {
        Intent intent = new Intent(getActivity(), YKchannelActivity.class);
        intent.putExtra("isQYK", zhia);
        intent.putExtra("bindCard", model);
        intent.putExtra("isChannels", isChannel);
        startActivity(intent);
    }

    private void goLookData() {
        Intent intent = new Intent(getActivity(), BankCreditDetailActivity.class);
        intent.putExtra("bindCard", model);
        startActivity(intent);
    }


    private void goLookPlan() {
        Intent intent = new Intent(getActivity(), LookPlanActivity.class);
        intent.putExtra("bindCard", model);
        startActivity(intent);
    }

}
