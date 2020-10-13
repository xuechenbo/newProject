package com.kotyj.com.activity.login;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kotyj.com.MyApplication;
import com.kotyj.com.R;
import com.kotyj.com.activity.HomeNewActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.AutoControlModel;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.ImageTypeModel;
import com.kotyj.com.model.UserInfoModel;
import com.kotyj.com.utils.AppUtils;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.LogUtils;
import com.kotyj.com.utils.PermissionUtil;
import com.kotyj.com.utils.StorageAppInfoUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

public class LaunchActivity extends BaseActivity implements Animation.AnimationListener {

    /**
     * 设置缩放动画
     */
    final ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f,
            Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f);
    @BindView(R.id.iv_launch)
    ImageView ivLaunch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_launch;
    }

    @Override
    public void initData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            showImage();
        } else {
            if (PermissionUtil.ALL(context)) {
                showImage();
            }
        }

    }


    private void showImage() {
        ivLaunch.setImageResource(R.drawable.launch);
        animation.setDuration(2000);
        ivLaunch.startAnimation(animation);
        animation.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        String phone = StorageCustomerInfo02Util.getInfo("phoneNum", context);
        String pass = StorageCustomerInfo02Util.getInfo("passwd", context);
        LogUtils.i("phone=" + phone + "pass=" + phone);
        String isRememberPass = StorageAppInfoUtil.getInfo("rememberPass", context);
        if (StringUtil.isEmpty(phone) || StringUtil.isEmpty(isRememberPass)) {
            goLoginActivity();
        } else {
            loadLoginData(phone, pass);
        }
    }

    /**
     * 进入登录页
     */
    private void goLoginActivity() {
        Intent intent = new Intent();
        intent.setClass(this, LoginNewActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void loadLoginData(String phone, String pass) {
        HttpParams map = OkClient.getParamsInstance().getParams();
        if (phone.length() == 11) {
            map.put("1", phone);
            map.put("8", CommonUtils.Md5(pass));

        }
        map.put("3", "190928");

        OkClient.getInstance().post(map, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                goLoginActivity();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                BaseEntity baseEntity = response.body();
                if (baseEntity == null) {
                    goLoginActivity();
                    return;
                }
                String result = baseEntity.getStr39();
                if ("00".equals(result)) {
                    List<UserInfoModel> list = JSONArray.parseArray(baseEntity.getStr42(), UserInfoModel.class);
                    if (list == null || list.size() == 0) {
                        goLoginActivity();
                        return;
                    }
                    UserInfoModel userInfoModel = list.get(0);
                    if ("10B".equals(userInfoModel.getUseStatus())) {
                        goLoginActivity();
                        return;
                    }
                    AutoControlModel autoControlModel = JSONObject.parseObject(baseEntity.getStr23(), AutoControlModel.class);
                    saveAuthControlData(autoControlModel);
                    saveUserData(userInfoModel, result);
                    saveOtherData(baseEntity);
                    if ("W8".equals(result)) {
                        // : 2019/4/16 审核不通过
                        goLoginActivity();
                    } else {
                        if (!TextUtils.isEmpty(baseEntity.getStr44())) {
                            String constant = baseEntity.getStr44();
                            String version = constant.split("-")[2];
                            int newVerCode = Integer.parseInt(version.replace(".", ""));
                            int curVerCode = Integer.parseInt(AppUtils.packageName(context).replace(".", ""));
                            if (newVerCode > curVerCode) {
                                goLoginActivity();
                            } else {
                                goMainActivity();
                            }
                        } else {
                            goMainActivity();
                        }
                    }
                } else {
                    goLoginActivity();
                }

            }
        });
    }

    /**
     * 在线商城，龙虎榜，签到，酷友圈，商学院后台控制
     *
     * @param autoControlModel
     */
    private void saveAuthControlData(AutoControlModel autoControlModel) {
        StorageCustomerInfo02Util.putInfo(context, "bk", autoControlModel.getBk());
        StorageCustomerInfo02Util.putInfo(context, "dk", autoControlModel.getDk());
        StorageCustomerInfo02Util.putInfo(context, "bx", autoControlModel.getBx());
        StorageCustomerInfo02Util.putInfo(context, "kcp", autoControlModel.getKcp());
        StorageCustomerInfo02Util.putInfo(context, "zx", autoControlModel.getZx());
        StorageCustomerInfo02Util.putInfo(context, "sc", autoControlModel.getSc());
        StorageCustomerInfo02Util.putInfo(context, "lz", autoControlModel.getLz());
        StorageCustomerInfo02Util.putInfo(context, "kyq", autoControlModel.getKyq());
        StorageCustomerInfo02Util.putInfo(context, "kf", autoControlModel.getKf());
        StorageCustomerInfo02Util.putInfo(context, "zb", autoControlModel.getZb());
        StorageCustomerInfo02Util.putInfo(context, "sxy", autoControlModel.getSxy());
        StorageCustomerInfo02Util.putInfo(context, "qd", autoControlModel.getQd());
        StorageCustomerInfo02Util.putInfo(context, "lhb", autoControlModel.getLhb());
        StorageCustomerInfo02Util.putInfo(context, "jf", autoControlModel.getJf());
    }

    /**
     * 保存商户数据
     *
     * @param item
     * @param result
     */
    private void saveUserData(UserInfoModel item, String result) {

        StorageCustomerInfo02Util.putInfo(context, "isPay", item.getIsPay());

        StorageCustomerInfo02Util.putInfo(context, "isIntermediary", item.getIsIntermediary());

        StorageCustomerInfo02Util.putInfo(context, "payPsd", item.getPayPassword());

        StorageCustomerInfo02Util.putInfo(context, "merchantId", item.getId());
        StorageCustomerInfo02Util.putInfo(context, "customerNum", item.getMerchantNo());

        //是否是联创会员
        StorageCustomerInfo02Util.putInfo(context, "isMember", item.getIsMember());

        StorageCustomerInfo02Util.putInfo(context, "level", item.getLevel());
        StorageCustomerInfo02Util.putInfo(context, "merchantCnName", item.getMerchantCnName());
        StorageCustomerInfo02Util.putInfo(context, "bankAccount", item.getBankAccount());
        StorageCustomerInfo02Util.putInfo(context, "bankAccountName", item.getBankAccountName());
        StorageCustomerInfo02Util.putInfo(context, "idCardNumber", item.getIdCardNumber());
        StorageCustomerInfo02Util.putInfo(context, "bankDetail", MyApplication.getBankName(item.getBankCode()));
        StorageCustomerInfo02Util.putInfo(context, "bankCode", item.getBankCode());
        StorageCustomerInfo02Util.putInfo(context, "phone", item.getPhone());
        StorageCustomerInfo02Util.putInfo(context, "source", item.getMerchantSource());
        StorageCustomerInfo02Util.putInfo(context, "useStatus", item.getUseStatus());
        StorageCustomerInfo02Util.putInfo(context, "parentPhone", item.getParentPhone());

        JPushInterface.setAlias(context, 1, item.getMerchantNo());

        //10A 未审核，10B 审核通过，10C 审核拒绝，10D 重新审核
        String freezeStatus = item.getFreezeStatus();
        StorageCustomerInfo02Util.putInfo(context, "freezeStatus", freezeStatus);
        String examineResult = item.getRcexamineResult();
        if ("W8".equals(result)) {
            StorageCustomerInfo02Util.putInfo(context, "examineResult", examineResult);
            //审核状态
            StorageCustomerInfo02Util.putInfo(context, "examineState", "W8");
        }
    }

    /**
     * @param
     */
    private void saveOtherData(BaseEntity model) {

        StorageCustomerInfo02Util.putInfo(context, "medMoney", model.getStr28());


        StorageCustomerInfo02Util.putInfo(context, "serviceKF", model.getStr16());


        //TODO 个人
        StorageCustomerInfo02Util.putInfo(context, "planMsg", model.getStr26());
        StorageCustomerInfo02Util.putInfo(context, "PTRMsg", model.getStr27());


        StorageCustomerInfo02Util.putInfo(context, "scoreCost", model.getStr19());
        StorageCustomerInfo02Util.putInfo(context, "honorCost", model.getStr20());


        StorageCustomerInfo02Util.putInfo(context, "level2Cost", model.getStr21());
        StorageCustomerInfo02Util.putInfo(context, "level3Cost", model.getStr22());
        StorageCustomerInfo02Util.putInfo(context, "level4Cost", model.getStr24());
        StorageCustomerInfo02Util.putInfo(context, "level5Cost", model.getStr25());

        List<ImageTypeModel> list = JSONArray.parseArray(model.getStr57(), ImageTypeModel.class);
        for (ImageTypeModel item :
                list) {
            StorageCustomerInfo02Util.putInfo(context, "infoImageUrl_" + item.getType(), item.getImageUrl());
        }

    }

    /**
     * 进入主页面
     */
    private void goMainActivity() {
        Intent intent_start = new Intent();
        intent_start.setClass(context, HomeNewActivity.class);
        intent_start.putExtra("fromLogin", true);
        startActivity(intent_start);
        ViewUtils.overridePendingTransitionComeFinish(context);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtil.ALL:
                int i = 0;
                for (i = 0; i < grantResults.length; i++) {
                    boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        if (isTip) {
                            //表明用户没有彻底禁止弹出权限请求
                            ViewUtils.makeToast(context, "请赋予应用该权限", 1000);
                            PermissionUtil.ALL(context);
                        } else {//表明用户已经彻底禁止弹出权限请求
                            if (context != null && !context.isFinishing()) {
                                new AlertDialog.Builder(context)
                                        .setMessage("请赋予应用权限,否则可能会导致未知错误,赋予权限之后,请重新打开应用！")
                                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                openSetting(context);
                                            }
                                        }).show();
                            }
                        }
                        return;
                    }
                }
                int length = permissions.length;
                if (i == length) {
                    initData();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 打开设置
     */
    public void openSetting(Context mContext) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }


}
