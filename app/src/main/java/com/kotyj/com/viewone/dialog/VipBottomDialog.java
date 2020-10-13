package com.kotyj.com.viewone.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.callback.StringCallback;
import com.kotyj.com.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class VipBottomDialog extends DialogFragment {


    public Dialog loadingDialog;
    Unbinder unbinder;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.ll1)
    LinearLayout ll1;
    @BindView(R.id.tv_wxPay)
    TextView tvWxPay;
    @BindView(R.id.tv_zfbPay)
    TextView tvZfbPay;
    private Activity mContext;
    private StringCallback mStringCallback;
    private String money, type, level;


    public static VipBottomDialog getIntence(String money, String type, String level) {
        VipBottomDialog dialog = new VipBottomDialog();
        Bundle bundle = new Bundle();
        bundle.putString("money", money);
        bundle.putString("type", type);
        bundle.putString("level", level);
        dialog.setArguments(bundle);
        return dialog;
    }


    public void setStringCallback(StringCallback stringCallback) {
        mStringCallback = stringCallback;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialog_vip);
        money = getArguments().getString("money");
        type = getArguments().getString("type");
        level = getArguments().getString("level");
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = window.getAttributes();
        ((WindowManager.LayoutParams) layoutParams).gravity = Gravity.BOTTOM;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes((WindowManager.LayoutParams) layoutParams);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        unbinder.unbind();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_vip_pay_bottom, container);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        getDialog().setCanceledOnTouchOutside(true);
        tvMoney.setText(money);

        loadingDialog = ViewUtils.createLoadingDialog(getActivity(), getString(R.string.loading_wait), false);
    }


    @OnClick({R.id.tv_wxPay, R.id.tv_zfbPay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_wxPay:
                if (mStringCallback != null) {
                    mStringCallback.callback("w");
                    dismiss();
                }
                break;
            case R.id.tv_zfbPay:
                if (mStringCallback != null) {
                    mStringCallback.callback("z");
                    dismiss();
                }
                break;
        }
    }

}
