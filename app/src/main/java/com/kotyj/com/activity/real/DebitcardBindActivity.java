package com.kotyj.com.activity.real;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.AuthEvent;
import com.kotyj.com.model.BankNameModel;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.RealPersonModel;
import com.kotyj.com.utils.BitmapManage;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.Constant;
import com.kotyj.com.utils.FileUtil;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.LogUtil;
import com.kotyj.com.utils.LogUtils;
import com.kotyj.com.utils.PermissionUtil;
import com.kotyj.com.utils.RecognizeService;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DebitcardBindActivity extends BaseActivity {
    private static final int REQUEST_CODE_BANKCARD = 111;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.iv_bank)
    ImageView ivBank;
    @BindView(R.id.et_bank_account)
    EditText etBankAccount;
    @BindView(R.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R.id.et_bank_phone)
    EditText etBankPhone;
    @BindView(R.id.btn_next)
    Button btnNext;

    private String bankUrl;
    private String bankCode;
    private boolean hasGotToken = false;
    private String phone;
    private boolean isPhoto;
    private RealPersonModel realPersonModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.act_auth_bank;
    }

    @Override
    public void initData() {
        tvTitle.setText("绑定储蓄卡");
        phone = StorageCustomerInfo02Util.getInfo("phone", context);
        isPhoto = getIntent().getBooleanExtra("isPhoto", false);
        realPersonModel = (RealPersonModel) getIntent().getSerializableExtra("realPersonModel");
        etBankPhone.setText(phone);
        if (!PermissionUtil.getPermission(context)) {
            return;
        }
        initAccessToken();

        etBankAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etBankAccount.getText().toString().length() >= 16) {
                    getBankName(etBankAccount.getText().toString());
                }
            }
        });
    }

    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                LogUtil.e("code", "获取失败" + error.getErrorCode());
            }
        }, getApplicationContext());
    }


    private void getBankName(String bankNum) {

        HttpParams map = OkClient.getParamsInstance().getParams();
        map.put("15", bankNum);
        map.put("3", "690013");
        OkClient.getInstance().post(map, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onSuccess(Response<BaseEntity> response) {
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    BankNameModel bankNameModel = JSONObject.parseObject(model.getStr16(), BankNameModel.class);
                    tvBankName.setText(bankNameModel.getShortCnName());
                    bankCode = bankNameModel.getBankCode();
                }
            }

        });
    }

    @OnClick({R.id.iv_back, R.id.tv_bank_name, R.id.btn_next, R.id.iv_bank})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ViewUtils.overridePendingTransitionBack(context);
                break;
            case R.id.iv_bank:
                if (!hasGotToken) {
                    ViewUtils.makeToast(context, "未初始化成功", 1200);
                    return;
                }
                Intent intent = new Intent(context, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_BANK_CARD);
                startActivityForResult(intent, REQUEST_CODE_BANKCARD);
                break;
            case R.id.tv_bank_name:
                break;
            case R.id.btn_next:
                String bankAccount = etBankAccount.getText().toString();
                String bankPhone = etBankPhone.getText().toString();
                if (StringUtil.isEmpty(bankUrl)) {
                    ViewUtils.makeToast(context, "请先上传储蓄卡正面照", 1000);
                    return;
                }
                if (StringUtil.isEmpty(bankAccount)) {
                    ViewUtils.makeToast(context, "储蓄卡卡号为空", 1000);
                    return;
                }
                if (StringUtil.isEmpty(bankPhone)) {
                    ViewUtils.makeToast(context, "请输入银行预留手机号", 1000);
                    return;
                }
                if (!CommonUtils.checkBankCard(bankAccount)) {
                    ViewUtils.makeToast(context, "请输入正确的银行卡号", 1000);
                    return;
                }
                sendSubmit(bankAccount);
                break;
            default:
                break;
        }
    }

    /**
     * 提交信息
     */
    private void sendSubmit(String bankNum) {
        loadingDialog.show();
        HttpParams httpParams = OkClient.getParamsInstance().getParams();
        httpParams.put("3", isPhoto ? "190938" : "190935");

        if (isPhoto) {
            httpParams.put("1", etBankPhone.getText().toString().trim());
            httpParams.put("2", getMerId());
            httpParams.put("5", realPersonModel.getName());
            httpParams.put("6", realPersonModel.getIdcard());
            httpParams.put("7", bankNum);
            httpParams.put("43", bankCode);

        } else {
            httpParams.put("21", bankCode);
            httpParams.put("22", bankNum);
            httpParams.put("42", getMerNo());
            httpParams.put("35", etBankPhone.getText().toString().trim());
        }


        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                super.onSuccess(response);
                loadingDialog.dismiss();
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    ViewUtils.makeToast(context, "信息已提交", 500);
                    if (isPhoto) {
                        StorageCustomerInfo02Util.putInfo(context, "bankAccount", model.getStr7());
                        StorageCustomerInfo02Util.putInfo(context, "bankCode", bankCode);
                        StorageCustomerInfo02Util.putInfo(context, "bankDetail", tvBankName.getText().toString());
                    } else {
                        StorageCustomerInfo02Util.putInfo(context, "bankAccount", model.getStr22());
                        StorageCustomerInfo02Util.putInfo(context, "bankCode", model.getStr45());
                        StorageCustomerInfo02Util.putInfo(context, "bankDetail", model.getStr44());
                    }
                    EventBus.getDefault().post(new AuthEvent());
                    finish();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 识别成功回调，银行卡识别
        if (requestCode == REQUEST_CODE_BANKCARD && resultCode == Activity.RESULT_OK) {
            RecognizeService.recBankCard(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            LogUtil.e(result);
                            String bankNumber = result.replaceAll(" ", "");
                            etBankAccount.setText(bankNumber);
                            getBankName(bankNumber);
                            String absolutePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
                            uploadImage(BitmapManage.bitmapToString(absolutePath, 600, 600), "10K");
                        }
                    });
        } else {
            ViewUtils.makeToast(context, "重新识别", 1200);
        }

    }

    /**
     * 上传图片
     *
     * @param imageData
     * @param imageType
     */
    private void uploadImage(String imageData, final String imageType) {
        LogUtils.i("imageData=" + imageData);
        loadingDialog.show();
        HttpParams httpParams = OkClient.getParamsInstance().getParams();
        httpParams.put("3", "190948");
        httpParams.put("9", imageType);
        httpParams.put("42", getMerNo());
        httpParams.put("40", imageData);
        OkClient.getInstance().post(Constant.UPLOADIMAGE, httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                super.onSuccess(response);
                loadingDialog.dismiss();
                BaseEntity model = response.body();
                if ("00".equals(model.getStr39())) {
                    bankUrl = model.getStr57();
                    GlideUtils.loadImage(context, bankUrl, ivBank);
                    StorageCustomerInfo02Util.putInfo(context, "infoImageUrl_10K", model.getStr57());
                }
            }
        });

    }


}
