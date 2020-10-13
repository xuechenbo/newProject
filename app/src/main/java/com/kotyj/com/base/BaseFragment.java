package com.kotyj.com.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kotyj.com.R;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.viewone.dialog.TipDialog;
import com.lzy.okgo.OkGo;

import butterknife.ButterKnife;


public abstract class BaseFragment extends Fragment {
    public Activity context;
    public Dialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        loadingDialog = ViewUtils.createLoadingDialog(context, getString(R.string.loading_wait), true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(initLayout(), null);
        ButterKnife.bind(this, view);
        initData(view);
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    /**
     * 设置布局
     *
     * @return
     */
    public abstract int initLayout();

    /**
     * 设置数据
     */
    public abstract void initData(View v);

    /**
     * 判断是否已经实名认证
     *
     * @return
     */
    public boolean checkCustomerInfoCompleteShowToast() {
        //用于判断是否进行过实名认证
        String freezeStatus = StorageCustomerInfo02Util.getInfo("freezeStatus", context);
        String bankAccount = StorageCustomerInfo02Util.getInfo("bankAccount", context);
        String idCardNumber = StorageCustomerInfo02Util.getInfo("idCardNumber", context);
        if (freezeStatus.equals("10A")) {
            TipDialog tipDialog = TipDialog.getInstance("是否去实名认证", "auth");
            tipDialog.show(getChildFragmentManager(), "auth");
            return false;
        } else if ("10F".equals(freezeStatus)) {
            ViewUtils.makeToast(context, "实名认证审核中", 1200);
            return false;
        } else if ("10B".equals(freezeStatus) && bankAccount.isEmpty()) {
            checkBindCard();
            return false;
        } else if ("10E".equals(freezeStatus)) {
            ViewUtils.makeToast(context, "实名认证审核中", 1200);
            return false;
        } else if ("10A".equals(freezeStatus) && idCardNumber.length() > 0 && bankAccount.length() > 0) {
            ViewUtils.makeToast(context, "实名认证审核中", 1200);
            return false;
        } else if ("10D".equals(freezeStatus)) {
            ViewUtils.makeToast(context, "重新实名审核中", 1200);
        } else if ("10C".equals(freezeStatus)) {
            ViewUtils.makeToast(context, "认证拒绝请重新认证", 1200);
            TipDialog tipDialog = TipDialog.getInstance("是否去实名认证", "auth");
            tipDialog.show(getChildFragmentManager(), "auth");
            return false;
        } else if ("10B".equals(freezeStatus)) {
            return true;
        }
        return true;
    }


    public String getMerNo() {
        return StorageCustomerInfo02Util.getInfo("customerNum", context);
    }

    public String getMerId() {
        return StorageCustomerInfo02Util.getInfo("merchantId", context);
    }

    /**
     * 判断是否绑定储蓄卡
     *
     * @return
     */
    public boolean checkBindCard() {
        String bankAccount = StorageCustomerInfo02Util.getInfo("bankAccount", context);
        if (StringUtil.isEmpty(bankAccount)) {
            TipDialog tipDialog = TipDialog.getInstance("是否去绑定储蓄卡", "bind");
            tipDialog.show(getChildFragmentManager(), "auth");
            return false;
        }
        return true;
    }

}
