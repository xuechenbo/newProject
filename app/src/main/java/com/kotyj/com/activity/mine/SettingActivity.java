package com.kotyj.com.activity.mine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.activity.login.LoginNewActivity;
import com.kotyj.com.activity.login.PasswordChangeActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.DataCleanManager;
import com.kotyj.com.utils.LogUtils;
import com.kotyj.com.utils.StorageAppInfoUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.ViewUtils;
import com.kotlin.com.wkyk.web.AgentWebActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.phoneNum)
    TextView phoneNum;
    @BindView(R.id.tv_cache)
    TextView tvCache;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_pay_status)
    TextView tvPayStatus;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_setting;
    }

    @Override
    public void initData() {
        tvTitle.setText("我的设置");
        tvVersion.setText("V " + CommonUtils.getAppVersionName(this));
        tvCache.setText(DataCleanManager.getTotalCacheSize(context));
        phone = StorageCustomerInfo02Util.getInfo("phone", context);
        phoneNum.setText("手机号  " + CommonUtils.translateShortNumber(phone, 3, 4));
        LogUtils.i("normal" + StorageAppInfoUtil.getBooleanInfo("aliAuth", context));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String payPsd = StorageCustomerInfo02Util.getInfo("payPsd", context);
        if (payPsd.isEmpty() || payPsd.equals("null")) {
            tvPayStatus.setText("去设置");
        } else {
            tvPayStatus.setText("去修改");
        }
    }

    @OnClick({R.id.iv_back, R.id.rl_phone, R.id.rl_pass_login, R.id.rl_pay_psw, R.id.rl_clear_cache, R.id.tv_xy, R.id.tv_ys, R.id.btn_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_phone:
                startActivity(new Intent(context, ReplacePhoneActivity.class));
                break;
            case R.id.rl_pass_login:
                startActivity(new Intent(context, PasswordChangeActivity.class)
                        .putExtra("isLogin", true)
                        .putExtra("title", "登录密码"));
                break;
            case R.id.rl_pay_psw:
                startActivity(new Intent(context, PasswordChangeActivity.class)
                        .putExtra("isLogin", false)
                        .putExtra("title", "支付密码"));
                break;
            case R.id.rl_clear_cache:
                ViewUtils.showDialogStandard(context, "清除缓存？", "清除缓存后，你的手机可用储存空间将会增加", "", "确认 ", new ViewUtils.OnshowDialogStandard() {
                    @Override
                    public void clickOk() {
                        DataCleanManager.clearAllCache(context);
                        ViewUtils.makeToast(context, "缓存清除成功", 500);
                        tvCache.setText("0KB");
                    }

                    @Override
                    public void clickCancel() {

                    }
                }).show();
                break;
            case R.id.tv_xy:
                startActivity(new Intent(context, AgentWebActivity.class)
                        .putExtra("title", "用户协议")
                        .putExtra("url", "http://xinjuejia.llyzf.cn/xinjuejia_register_protocol.html"));
                break;
            case R.id.tv_ys:
                startActivity(new Intent(context, AgentWebActivity.class)
                        .putExtra("title", "隐私政策")
                        .putExtra("url", "http://xinjuejia.llyzf.cn/xinjuejia_privacy_policy.html"));
                break;
            case R.id.btn_exit:
                showExitDialog(context);
                break;
        }
    }

    private void showExitDialog(final Activity activity) {
        Button confirmBt, cancleBt;
        final Dialog mydialog = new Dialog(activity, R.style.MyProgressDialog);
        mydialog.setContentView(R.layout.dialog_exit);
        mydialog.setCanceledOnTouchOutside(false);
        confirmBt = (Button) mydialog.findViewById(R.id.bt_cancelPlan);
        cancleBt = (Button) mydialog.findViewById(R.id.bt_suspendCancel);
        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isFastDoubleClick()) {
                    return;
                }
                //清除存储终端信息的缓存数据
                if (StorageCustomerInfo02Util.clearKey(context)) {
                    goLogin();
                } else {
                    ViewUtils.makeToast(context, "数据清除失败", 500);
                }
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

    /**
     * 跳转至登录页
     */
    private void goLogin() {
        Intent intent = new Intent(context, LoginNewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
