package com.kotyj.com.activity.mine;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.ServiceModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.PermissionUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ServiceCenterActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_service_phone)
    TextView tvServicePhone;
    @BindView(R.id.ll_phone)
    LinearLayout llPhone;
    @BindView(R.id.tv_wechat)
    TextView tvWechat;
    @BindView(R.id.ll_service_wechat)
    LinearLayout llServiceWechat;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_service_phone1)
    TextView tvServicePhone1;
    @BindView(R.id.btn_wechat_copy)
    TextView btnWechatCopy;
    @BindView(R.id.tv_wechat1)
    TextView tvWechat1;
    @BindView(R.id.btn_wechat_copy1)
    TextView btnWechatCopy1;
    @BindView(R.id.tv_qq)
    TextView tvQq;
    @BindView(R.id.btn_qq_copy)
    TextView btnQqCopy;
    @BindView(R.id.tv_qq1)
    TextView tvQq1;
    @BindView(R.id.btn_qq_copy1)
    TextView btnQqCopy1;
    private ServiceModel serviceModel;

    @Override
    public int initLayout() {
        return R.layout.act_service_center;
    }

    @Override
    public void initData() {
        tvTitle.setText("我的客服");
        String serviceKF = StorageCustomerInfo02Util.getInfo("serviceKF", context);
        serviceModel = new Gson().fromJson(serviceKF, ServiceModel.class);
        Log.e("sss", serviceKF);
        tvServicePhone.setText("联系电话：" + serviceModel.getPhone1());
        tvServicePhone1.setText("联系电话：" + serviceModel.getPhone2());
        tvWechat.setText("官方微信号：" + serviceModel.getWx1());
        tvWechat1.setText("官方微信号：" + serviceModel.getWx2());
        tvQq.setText("官方QQ号：" + serviceModel.getQq1());
        tvQq1.setText("官方QQ号：" + serviceModel.getQq2());
    }

    @OnClick({R.id.iv_back, R.id.tv_service_phone, R.id.tv_service_phone1, R.id.btn_wechat_copy,
            R.id.btn_wechat_copy1, R.id.btn_qq_copy, R.id.btn_qq_copy1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ViewUtils.overridePendingTransitionBack(context);
                break;
            case R.id.tv_service_phone:
                if (!PermissionUtil.CALL_PHONE(context)) {
                    return;
                }
                showCallServerDialog(serviceModel.getPhone1());
                break;
            case R.id.tv_service_phone1:
                if (!PermissionUtil.CALL_PHONE(context)) {
                    return;
                }
                showCallServerDialog(serviceModel.getPhone2());
                break;
            case R.id.btn_wechat_copy:
                coptString(serviceModel.getWx1());
                break;
            case R.id.btn_wechat_copy1:
                coptString(serviceModel.getWx2());
                break;
            case R.id.btn_qq_copy:
                coptString(serviceModel.getQq1());
                break;
            case R.id.btn_qq_copy1:
                coptString(serviceModel.getQq2());
                break;
        }
    }


    void coptString(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", content);
        cm.setPrimaryClip(mClipData);
        ViewUtils.makeToast(context, "复制成功", 1000);
    }

    private void showCallServerDialog(final String phone) {
        Button confirmBt, cancleBt;
        final Dialog mydialog = new Dialog(context, R.style.MyProgressDialog);
        mydialog.setContentView(R.layout.callserver_dialog);
        mydialog.setCanceledOnTouchOutside(false);
        TextView phonenum = (TextView) mydialog.findViewById(R.id.phoneNum);

        phonenum.setText(phone);
        confirmBt = (Button) mydialog.findViewById(R.id.bt_cancelPlan);
        cancleBt = (Button) mydialog.findViewById(R.id.bt_suspendCancel);
        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isFastDoubleClick2()) {
                    return;
                }
                mydialog.dismiss();
                String serviceNumber = phone.replace("-", "");
                Intent phoneIntent = new Intent(
                        "android.intent.action.CALL", Uri.parse("tel:"
                        + serviceNumber));
                startActivity(phoneIntent);
            }
        });
        cancleBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mydialog.dismiss();
            }

        });

        mydialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
