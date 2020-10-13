package com.kotyj.com.activity.wkyk.newwkyk;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.kotyj.com.R;
import com.kotyj.com.activity.fun.X5WebViewActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.BankCardEvent;
import com.kotyj.com.model.BankNameModel;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.utils.BitmapManage;
import com.kotyj.com.utils.CheckOutMessage;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.Constant;
import com.kotyj.com.utils.DateUtil;
import com.kotyj.com.utils.KeyBoardUtils;
import com.kotyj.com.utils.LogUtils;
import com.kotyj.com.utils.PermissionUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kotyj.com.utils.Constant.IMAGE_PICKER;


public class AddBankCardActivity extends BaseActivity {

    private static final int REWUEST_FINISH = 0x20;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_credit_card_no)
    EditText etCreditCardNo;
    @BindView(R.id.et_bank_name)
    EditText etBankName;
    @BindView(R.id.et_limit)
    EditText etLimit;
    @BindView(R.id.et_day_bill)
    EditText etDayBill;
    @BindView(R.id.et_day_pay)
    EditText etDayPay;
    @BindView(R.id.et_dead_line)
    EditText etDeadLine;
    @BindView(R.id.et_cvv)
    EditText etCvv;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.rl_container)
    RelativeLayout rlContainer;
    private String name, idCardNumber, bankAccount, deadLine, limit, bankCode, billDay, payDay, cvv, phone, merchantId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_add_bank_card;
    }

    @Override
    public void initData() {
        tvTitle.setText("添加信用卡");
        initListener();

        name = getIntent().getStringExtra("name");
        idCardNumber = getIntent().getStringExtra("idCard");
        merchantId = getIntent().getStringExtra("merchantId");

        if (!StringUtil.isEmpty(name)) {
            etName.setText(CommonUtils.translateShortNumber(name, 0, name.length() - 1));
        }
        etName.setFocusable(false);
        etDeadLine.setFocusable(false);
        tip();
    }

    private void tip() {
        ViewUtils.showTipCard(context, new ViewUtils.OnshowDialogStandard() {
            @Override
            public void clickOk() {

            }

            @Override
            public void clickCancel() {
                ViewUtils.overridePendingTransitionBack(context);
            }
        });
    }


    @OnClick({R.id.iv_back, R.id.btn_submit, R.id.tv_dead_line, R.id.iv_right, R.id.et_dead_line, R.id.tv_cvv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.btn_submit:
                bankAccount = etCreditCardNo.getText().toString().trim();
                name = etName.getText().toString().trim();
                deadLine = etDeadLine.getText().toString().trim();
                limit = etLimit.getText().toString().trim();
                String bankName = etBankName.getText().toString().trim();
                billDay = etDayBill.getText().toString().trim();
                payDay = etDayPay.getText().toString().trim();
                cvv = etCvv.getText().toString().trim();
                phone = etPhone.getText().toString().trim();
                if (CheckOutMessage.isEmpty(context, "信用卡号", bankAccount)) return;
                if (CheckOutMessage.isEmpty(context, "有效期", deadLine)) return;
                if (CheckOutMessage.isEmpty(context, "信用卡额度", limit)) return;
                if (CheckOutMessage.isEmpty(context, "银行", bankName)) return;
                if (CheckOutMessage.isEmpty(context, "账单日", billDay)) return;
                if (CheckOutMessage.isEmpty(context, "还款日", payDay)) return;
                if (CheckOutMessage.isEmpty(context, "CVV2码", cvv)) return;
                if (CheckOutMessage.isEmpty(context, "手机号", phone)) return;
                submit();
                break;
            case R.id.et_dead_line:
                KeyBoardUtils.hideKeyboard(rlContainer);
                showTimePickerDialog();
                break;
            case R.id.iv_right:
                if (!PermissionUtil.CAMERA(context)) {
                    return;
                }
                Intent imgIntent = new Intent(context, ImageGridActivity.class);
                startActivityForResult(imgIntent, Constant.IMAGE_PICKER);
                break;
            case R.id.tv_dead_line:
                ViewUtils.showValidatePhoto(this);
                break;
            case R.id.tv_cvv:
                ViewUtils.showCvvPhoto(this);
                break;
        }
    }

    /**
     * 显示日期选择 7.0 7.1显示年月日
     */
    private void showTimePickerDialog() {
        //获取当前日期
        TimePickerView pvTime = new TimePickerBuilder(context, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                LogUtils.i("date=" + DateUtil.formateDateTOYMD(date.getTime()));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                String yearStr = String.valueOf(year).substring(2, 4);
                String monthStr = month < 10 ? "0" + month : month + "";
                etDeadLine.setText(String.format("%s/%s", monthStr, yearStr));
            }
        })
                .setType(new boolean[]{true, true, false, false, false, false})
                .isDialog(false)
                .build();
        pvTime.show();


    }

    /**
     * 提交
     */
    private void submit() {
        loadingDialog.show();
        HttpParams map = new HttpParams();
        map.put("3", "490008");
        map.put("5", limit);
        map.put("6", billDay);
        map.put("7", payDay);
        map.put("37", "0");
        map.put("42", idCardNumber);
        map.put("43", merchantId);
        map.put("45", bankAccount);
        map.put("46", phone);
        map.put("47", deadLine.replace("/", ""));
        map.put("48", cvv);
        map.put("49", "A");

        OkClient.getInstance().post(map, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
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
                String code = model.getStr39();
                if ("00".equals(code)) {
                    String url = model.getStr38();
                    if ("00".equals(url)) {
                        ViewUtils.makeToast(context, "尊敬的客户，您的信用卡已绑定成功", 500);
                        ViewUtils.overridePendingTransitionBack(context);
                    } else {
                        Intent intent = new Intent(context, X5WebViewActivity.class);
                        intent.putExtra("title", "添加信用卡");
                        intent.putExtra("url", url);
                        intent.putExtra("back", true);
                        startActivityForResult(intent, REWUEST_FINISH);
                    }

                    StorageCustomerInfo02Util.putInfo(context, "isAuth", "1");
                    EventBus.getDefault().post(new BankCardEvent());
                } else if (code.contains("重复签约") || code.contains("已绑定")) {
                    StorageCustomerInfo02Util.putInfo(context, "isAuth", "1");
                    EventBus.getDefault().post(new BankCardEvent());
                    ViewUtils.overridePendingTransitionBack(context);
                    ViewUtils.makeToast(context, code,
                            500);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REWUEST_FINISH && resultCode == RESULT_OK) {
            finish();
        }
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                uploadImage(BitmapManage.bitmapToString(images.get(0).path, 600, 600), "10A");
            } else {
                ViewUtils.makeToast(context, "没有数据", 500);
            }
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
        httpParams.put("43", getMerId());
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

                    etCreditCardNo.setText(model.getStr42());
                    getBankName(model.getStr42());

                }
            }
        });

    }

    /**
     * 获取银行卡名称
     *
     * @param bankNum 卡号
     */
    private void getBankName(final String bankNum) {

        final HttpParams map = OkClient.getParamsInstance().getParams();
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
                    etBankName.setText(bankNameModel.getShortCnName());
                }
            }

        });
    }


    private void initListener() {
        etDeadLine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4 && !s.toString().contains("/")) {
                    s.insert(2, "/");
                }

            }
        });
        etDayBill.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2) {
                    int first = Integer.parseInt(s.toString().charAt(0) + "");
                    if (first > 3) {
                        s.clear();
                        s.append("31");
                    } else {
                        if (first != 0) {
                            if (Integer.parseInt(s.toString()) > 31) {
                                s.clear();
                                s.append("31");
                            }
                        }
                    }
                }
            }
        });
        etDayPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2) {
                    int first = Integer.parseInt(s.toString().charAt(0) + "");
                    if (first > 3) {
                        s.clear();
                        s.append("31");
                    } else {
                        if (first != 0) {
                            if (Integer.parseInt(s.toString()) > 31) {
                                s.clear();
                                s.append("31");
                            }
                        }
                    }
                }
            }
        });
        etCreditCardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etCreditCardNo.getText().toString().length() >= 16) {
                    getBankName(etCreditCardNo.getText().toString().trim());
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post("clo_MeRealName");
    }
}
