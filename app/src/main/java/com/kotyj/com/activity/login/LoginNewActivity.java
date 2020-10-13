package com.kotyj.com.activity.login;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.AutoControlModel;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.ImageTypeModel;
import com.kotyj.com.model.UserInfoModel;
import com.kotyj.com.utils.AppUtils;
import com.kotyj.com.utils.CheckOutMessage;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.DataCleanManager;
import com.kotyj.com.utils.KeyBoardUtils;
import com.kotyj.com.utils.StorageAppInfoUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.viewone.dialog.TipDialog;
import com.kotyj.com.viewone.dialog.UpdateDialog;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;


public class LoginNewActivity extends BaseActivity {


    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_pass)
    EditText etPass;
    @BindView(R.id.checkbox)
    CheckBox checkbox;
    @BindView(R.id.tv_forget_pass)
    TextView tvForgetPass;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.btn_login)
    ImageView btnLogin;
    @BindView(R.id.ll_code)
    LinearLayout llCode;
    @BindView(R.id.tv_loginType)
    TextView tvLoginType;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_getcode)
    TextView tvGetcode;
    private Dialog Disabledialog;
    private boolean LOG_TYPE = false;
    private String phone, password;

    private final long COUNT_DOWN_TOTAL = 60000;
    private final long COUNT_DOWN_INTERVAL = 1000;
    private final CountDownTimer countDownTimer = new CountDownTimer(COUNT_DOWN_TOTAL, COUNT_DOWN_INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvGetcode.setText(String.format("%ds", millisUntilFinished / COUNT_DOWN_INTERVAL));
            tvGetcode.setEnabled(false);
        }

        @Override
        public void onFinish() {
            tvGetcode.setText("获取验证码");
            tvGetcode.setEnabled(true);
        }
    };
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_new_login;
    }

    @Override
    public void initData() {
        String phoneNum = StorageCustomerInfo02Util.getInfo("phoneNum", this);
        String password = StorageCustomerInfo02Util.getInfo("passwd", this);
        if (!StringUtil.isEmpty(phoneNum)) {
            etPhone.setText(phoneNum);
            etPhone.setSelection(phoneNum.length());
        }
        if (!StringUtil.isEmpty(password)) {
            etPass.setText(password);
        }
        setBtnLogin(etPass.getText().toString());
        deleteCache();
        initTextChangedListener();
    }


    @OnClick({R.id.tv_forget_pass, R.id.btn_login, R.id.tv_register, R.id.tv_loginType, R.id.tv_getcode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_loginType:
                String loginType = tvLoginType.getText().toString().trim();
                if (loginType.equals("验证码登录")) {//密码登录
                    LOG_TYPE = true;
                    tvLoginType.setText("密码登录");
                    llCode.setVisibility(View.VISIBLE);
                    etPass.setVisibility(View.GONE);
                    setBtnLogin(etCode.getText().toString());
                } else {
                    //验证码登录
                    LOG_TYPE = false;
                    llCode.setVisibility(View.GONE);
                    etPass.setVisibility(View.VISIBLE);
                    tvLoginType.setText("验证码登录");
                    setBtnLogin(etPass.getText().toString());
                }
                break;
            case R.id.tv_forget_pass:
                startActivityForResult(new Intent(context, ForgetPassNewActivity.class), 1);
                break;
            case R.id.btn_login:
                KeyBoardUtils.hideKeyboard(view);
                phone = etPhone.getText().toString().trim();
                password = etPass.getText().toString().trim();
                if (StringUtil.isEmpty(phone)) {
                    ViewUtils.makeToast(context, "请输入手机号", 1000);
                    return;
                }

                if (LOG_TYPE) {
                    code = etCode.getText().toString().trim();
                    if (StringUtil.isEmpty(code)) {
                        ViewUtils.makeToast(context, "请输入验证码", 1000);
                        return;
                    }
                } else {
                    if (StringUtil.isEmpty(password)) {
                        ViewUtils.makeToast(context, "请输入密码", 1000);
                        return;
                    }
                }

                if (phone.length() != 11) {
                    ViewUtils.makeToast(context, "请输入正确的手机号", 1000);
                    return;
                }
                loginPhone(phone, LOG_TYPE ? code : password);
                break;
            case R.id.tv_register:
                startActivity(new Intent(context, RegisterNewActivity.class));
                break;
            case R.id.tv_getcode:
                if ("获取验证码".equals(tvGetcode.getText().toString())) {
                    String phoneValue = etPhone.getText().toString();
                    if (CheckOutMessage.isEmpty(context, "手机号", phoneValue)) return;
                    if (phoneValue.length() != 11) {
                        ViewUtils.makeToast(context, "请输入正确的手机号", 1000);
                        return;
                    }
                    etCode.setText("");
                    sendCheckCode(phoneValue);
                }
                break;
            default:
                break;
        }
    }

    private void loginPhone(String phone, String password) {
        HttpParams map = OkClient.getParamsInstance().getParams();
        map.put("1", phone);
        map.put("3", "190928");
        if (LOG_TYPE) {
            map.put("12", password);
        } else {
            map.put("8", CommonUtils.Md5(password));
        }
        login(map);
    }

    /**
     * 登录
     *
     * @param map
     */
    private void login(HttpParams map) {
        loadingDialog.show();

        OkClient.getInstance().post(map, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                loadingDialog.dismiss();
                BaseEntity baseEntity = response.body();
                if (baseEntity == null) {
                    return;
                }
                String result = baseEntity.getStr39();
                if ("00".equals(result) || "W8".equals(result)) {

                    StorageCustomerInfo02Util.putInfo(context, "login", result);

                    List<UserInfoModel> list = JSONArray.parseArray(baseEntity.getStr42(), UserInfoModel.class);
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    UserInfoModel userInfoModel = list.get(0);
                    //TODO
                    saveOtherData(baseEntity);
                    AutoControlModel autoControlModel = JSONObject.parseObject(baseEntity.getStr23(), AutoControlModel.class);
                    saveAuthControlData(autoControlModel);

                    if (StringUtil.isEmpty(userInfoModel.getPhone())) {
                        startActivity(new Intent(context, LoginBindPhoneActivity.class).putExtra("customerNum", userInfoModel.getMerchantNo()));
                        return;
                    }
                    saveUserData(userInfoModel, result);
                    if ("10B".equals(userInfoModel.getUseStatus())) {
                        showDangerDialog();
                        return;
                    }
                    StorageCustomerInfo02Util.putInfo(context, "phoneNum", phone);
                    if (checkbox.isChecked()) {
                        StorageCustomerInfo02Util.putInfo(context, "passwd", password);
                        StorageAppInfoUtil.putInfo(context, "rememberPass", "1");
                    } else {
                        StorageCustomerInfo02Util.removeKey("passwd", context);
                        StorageAppInfoUtil.removeKey("rememberPass", context);
                    }

                    if ("W8".equals(result)) {
                        checkDialog();
                    } else {
                        if (!TextUtils.isEmpty(baseEntity.getStr44())) {
                            String constant = baseEntity.getStr44();
                            String version = constant.split("-")[2];
                            int newVerCode = Integer.parseInt(version.replace(".", ""));
                            int curVerCode = Integer.parseInt(AppUtils.packageName(context).replace(".", ""));
                            if (newVerCode > curVerCode) {
                                UpdateDialog.getInstance(false, baseEntity.getStr45()).show(getSupportFragmentManager(), "update");
                            } else {
                                goMainActivity();
                            }
                        } else {
                            goMainActivity();
                        }
                    }
                } else if ("ZV".equals(result)) {
                    StorageCustomerInfo02Util.putInfo(context, "apkUrl", baseEntity.getStr47());
                    UpdateDialog.getInstance(true, baseEntity.getStr45()).show(getSupportFragmentManager(), "update");
                } else {
                    ViewUtils.makeToast(context, result, 500);
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

    private void goMainActivity() {
        startActivity(new Intent(context, TheBufferActivity.class));
        ViewUtils.overridePendingTransitionComeFinish(context);
    }

    /**
     * 危险账号弹框
     */
    private void showDangerDialog() {
        Disabledialog = ViewUtils.showChoseDialog(context, false, "风险账号，暂被停用", View.GONE, new ViewUtils.OnChoseDialogClickCallback() {
            @Override
            public void clickOk() {
                Disabledialog.dismiss();
            }

            @Override
            public void clickCancel() {

            }
        });
        Disabledialog.show();
    }

    /**
     * 风控审核提示框
     */
    private void checkDialog() {
        TipDialog tipDialog = TipDialog.getInstance("商户资料审核不通过\n请重新提交", "recertification");
        tipDialog.show(getSupportFragmentManager(), "recertification");
    }

    /**
     * 保存其他信息
     *
     * @param
     */
    private void saveOtherData(BaseEntity model) {

        StorageCustomerInfo02Util.putInfo(context, "serviceKF", model.getStr16());

        StorageCustomerInfo02Util.putInfo(context, "medMoney", model.getStr28());

        //TODO 个人
        StorageCustomerInfo02Util.putInfo(context, "planMsg", model.getStr26());
        //TODO 系统
        StorageCustomerInfo02Util.putInfo(context, "PTRMsg", model.getStr27());


        StorageCustomerInfo02Util.putInfo(context, "scoreCost", model.getStr19());
        StorageCustomerInfo02Util.putInfo(context, "honorCost", model.getStr20());

        StorageCustomerInfo02Util.putInfo(context, "level2Cost", model.getStr21());
        StorageCustomerInfo02Util.putInfo(context, "level3Cost", model.getStr22());

        StorageCustomerInfo02Util.putInfo(context, "level4Cost", model.getStr24());
        StorageCustomerInfo02Util.putInfo(context, "level5Cost", model.getStr25());

        StorageCustomerInfo02Util.putInfo(context, "apkUrl", model.getStr47());
        List<ImageTypeModel> list = JSONArray.parseArray(model.getStr57(), ImageTypeModel.class);
        for (ImageTypeModel item : list) {
            StorageCustomerInfo02Util.putInfo(context, "infoImageUrl_" + item.getType(), item.getImageUrl());
        }

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
        StorageCustomerInfo02Util.putInfo(context, "isMember", item.getIsMember());

        StorageCustomerInfo02Util.putInfo(context, "level", item.getLevel());
        StorageCustomerInfo02Util.putInfo(context, "merchantCnName", item.getMerchantCnName());
        StorageCustomerInfo02Util.putInfo(context, "bankAccount", item.getBankAccount());
        StorageCustomerInfo02Util.putInfo(context, "bankAccountName", item.getBankAccountName());
        StorageCustomerInfo02Util.putInfo(context, "idCardNumber", item.getIdCardNumber());
        StorageCustomerInfo02Util.putInfo(context, "bankDetail", item.getBankDetail());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        etPass.setText("");
        switch (requestCode) {
            case 0:
                etPhone.setText(data.getStringExtra("phone"));
                break;
            case 1:
                etPhone.setText(data.getStringExtra("phone"));
                break;
        }
    }


    /**
     * 清除安装包
     */
    private void deleteCache() {
        DataCleanManager.clearAllCache(context);
    }

    private void initTextChangedListener() {
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                etPass.setText("");
            }
        });

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String string = editable.toString();
                setBtnLogin(string);
            }
        });

        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String string = editable.toString();
                setBtnLogin(string);

            }
        });
    }


    void setBtnLogin(String string) {
        if (string.isEmpty()) {
            btnLogin.setImageDrawable(context.getResources().getDrawable(R.drawable.login_un_btn));
        } else {
            btnLogin.setImageDrawable(context.getResources().getDrawable(R.drawable.login_btn));
        }
    }


    private void sendCheckCode(final String phoneValue) {
        loadingDialog.show();
        HttpParams map = OkClient.getParamsInstance().getParams();
        map.put("1", phoneValue);
        map.put("3", "190919");
        OkClient.getInstance().post(map, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                super.onSuccess(response);
                BaseEntity model = response.body();
                loadingDialog.dismiss();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    ViewUtils.makeToast(context, "验证码已发送，请注意查收", 500);
                    time();
                }
            }
        });
    }

    void time() {
        countDownTimer.start();
    }

}
