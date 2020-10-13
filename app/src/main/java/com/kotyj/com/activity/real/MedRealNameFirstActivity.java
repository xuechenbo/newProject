package com.kotyj.com.activity.real;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.kotyj.com.R;
import com.kotyj.com.activity.med.maode.MediationBean;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.AuthEvent;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.RealPersonModel;
import com.kotyj.com.utils.BitmapManage;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.Constant;
import com.kotyj.com.utils.FileUtil;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.LogUtil;
import com.kotyj.com.utils.PermissionUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


//TODO 中介实名认证
public class MedRealNameFirstActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.iv_id_card_a)
    ImageView ivIdCardA;
    @BindView(R.id.iv_id_card_b)
    ImageView ivIdCardB;
    @BindView(R.id.et_name)
    TextView etName;
    @BindView(R.id.et_idcard)
    TextView etIdcard;
    @BindView(R.id.btn_next)
    Button btnNext;
    private String idcardUrl_a;
    private String idcardUrl_b;
    private boolean hasGotToken = false;
    private static final int REQUEST_CODE_CAMERA = 102;
    private String medFront;
    private String medBack;
    private String medNo;
    private MediationBean mediationBean;
    private boolean update;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

    }

    @Override
    public int initLayout() {
        return R.layout.activity_real_name_first;
    }

    @Override
    public void initData() {

        update = getIntent().getBooleanExtra("update", false);
        mediationBean = (MediationBean) getIntent().getSerializableExtra("MediationBean");
        EventBus.getDefault().register(this);
        tvTitle.setText("实名认证");
        if (!PermissionUtil.getPermission(context)) {
            return;
        }
        initAccessToken();
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


    @OnClick({R.id.iv_back, R.id.iv_id_card_a, R.id.iv_id_card_b, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_id_card_a:
                if (!hasGotToken) {
                    ViewUtils.makeToast(context, "未初始化成功", 1200);
                    return;
                }
                //TODO 百度识别
                Intent intent1 = new Intent(context, CameraActivity.class);
                intent1.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication(), CameraActivity.CONTENT_TYPE_ID_CARD_FRONT).getAbsolutePath());
                intent1.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                startActivityForResult(intent1, REQUEST_CODE_CAMERA);
                break;
            case R.id.iv_id_card_b:
                if (!hasGotToken) {
                    ViewUtils.makeToast(context, "未初始化成功", 1200);
                    return;
                }
                Intent intent2 = new Intent(context, CameraActivity.class);
                intent2.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication(), CameraActivity.CONTENT_TYPE_ID_CARD_BACK).getAbsolutePath());
                intent2.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_BACK);
                startActivityForResult(intent2, REQUEST_CODE_CAMERA);
                break;
            case R.id.btn_next:
                String name = etName.getText().toString();
                String idcard = etIdcard.getText().toString();

                if (StringUtil.isEmpty(name)) {
                    ViewUtils.makeToast(context, "姓名不能为空", 1000);
                    return;
                }
                if (StringUtil.isEmpty(idcard)) {
                    ViewUtils.makeToast(context, "身份证号不能为空", 1000);
                    return;
                }

//                if (StringUtil.isEmpty(idcardUrl_b) || StringUtil.isEmpty(idcardUrl_a)) {
//                    ViewUtils.makeToast(context, "请上传身份证正反面", 1000);
//                    return;
//                }
                if (!CommonUtils.isIdCard(idcard)) {
                    ViewUtils.makeToast(context, "请输入正确的身份证号", 1000);
                    return;
                }

                sendSubmit(idcard, name);
                break;
        }
    }

    private void sendSubmit(final String idCard, final String name) {
        loadingDialog.show();
        HttpParams httpParams = OkClient.getParamsInstance().getParams();
        httpParams.put("3", "390011");
        httpParams.put("8", idCard);
        httpParams.put("10", name);
        httpParams.put("42", getMerNo());
        if (update) {
            httpParams.put("43", mediationBean.getMERCHANT_NO());//被绑定的商户号
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
                    //商户号
                    medNo = model.getStr38();

                    //TODO 传照片
                    uploadImage(medFront, "10E");


                }
            }
        });

    }


    private void recIDCard(final String idCardSide, final String filePath) {
        final IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);

        OCR.getInstance(this).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (result != null) {
                    LogUtil.e("message", result.toString());
                    LogUtil.e("message", filePath);
                    if (IDCardParams.ID_CARD_SIDE_FRONT.equals(idCardSide)) {
                        //正面
                        if (result.getIdNumber().toString().isEmpty() || result.getName().toString().isEmpty()) {
                            ViewUtils.makeToast(context, "未识别到身份信息，请重新拍照", 1200);
                            return;
                        }
                        etName.setText(result.getName().toString());
                        etIdcard.setText(result.getIdNumber() + "");

                        GlideUtils.loadNoChacheImage(context, filePath, ivIdCardA);
                        //TODO 正面
                        medFront = BitmapManage.bitmapToString(filePath, 600, 600);
                        StorageCustomerInfo02Util.putInfo(context, "medFront", medFront);
                    } else {
                        //TOOD 反面
                        medBack = BitmapManage.bitmapToString(filePath, 600, 600);
                        StorageCustomerInfo02Util.putInfo(context, "medBack", medBack);
                        GlideUtils.loadNoChacheImage(context, filePath, ivIdCardB);
                    }
                }
            }

            @Override
            public void onError(OCRError error) {
                ViewUtils.makeToast(context, error.getMessage(), 1200);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);

                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, FileUtil.getSaveFile(getApplicationContext(), CameraActivity.CONTENT_TYPE_ID_CARD_FRONT).getAbsolutePath());
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_BACK, FileUtil.getSaveFile(getApplicationContext(), CameraActivity.CONTENT_TYPE_ID_CARD_BACK).getAbsolutePath());
                    }
                }
            }
        }
    }


    private void uploadImage(String signPath, final String imageType) {
        // 检查网络状态
        loadingDialog.show();
        HttpParams httpParams = OkClient.getParamsInstance().getParams();
        httpParams.put("3", "190948");
        httpParams.put("9", imageType);
        httpParams.put("42", update ? mediationBean.getMERCHANT_NO() : medNo);
        httpParams.put("40", signPath);
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
                    // 上传成功
                    String imageUrl = model.getStr57();
                    saveImagePath(imageUrl, imageType);
                }
            }
        });
    }

    private void saveImagePath(String imageUrl, String type) {
        if ("10E".equals(type)) {
            idcardUrl_a = imageUrl;
            GlideUtils.loadNoChacheImage(context, idcardUrl_a, ivIdCardA);

            uploadImage(medBack, "10F");

        } else {
            idcardUrl_b = imageUrl;
            GlideUtils.loadNoChacheImage(context, idcardUrl_b, ivIdCardB);

            RealPersonModel realPersonModel = new RealPersonModel();
            realPersonModel.setName(etName.getText().toString());
            realPersonModel.setIdcard(etIdcard.getText().toString());
            Intent intent = new Intent(context, MedDebitcardBindActivity.class);
            intent.putExtra("realPersonModel", realPersonModel);
            intent.putExtra("merNo", update ? mediationBean.getMERCHANT_NO() : medNo);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        // 释放本地质量控制模型
        CameraNativeHelper.release();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void onEvent(AuthEvent authEvent) {
        finish();
    }
}
