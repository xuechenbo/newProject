package com.kotyj.com.activity.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.utils.BitmapManage;
import com.kotyj.com.utils.Constant;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.LogUtil;
import com.kotyj.com.utils.PermissionUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.viewone.dialog.ChangePhotoBottomDialog;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyHeaduCropActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.iv_headImage)
    ImageView ivHeadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.act_head_photo;
    }

    public void initData() {
        String headImage = StorageCustomerInfo02Util.getInfo("headImage", context);
        GlideUtils.loadAvatar(context, headImage, ivHeadImage);
        tvTitle.setText("我的头像");
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageDrawable(context.getResources().getDrawable(R.drawable.top_more));
    }


    @OnClick({R.id.iv_back, R.id.iv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
                ChangePhotoBottomDialog instance = ChangePhotoBottomDialog.getInstance();
                instance.setOnClickListener(new ChangePhotoBottomDialog.OnClickListener() {
                    @Override
                    public void changePhoto() {
                        goPhotoHead();
                    }

                    @Override
                    public void takePhoto() {
                        goPhotoHead();
                    }
                });
                instance.show(getSupportFragmentManager(), "");
                break;
        }
    }

    void goPhotoHead() {
        if (!PermissionUtil.CAMERA(context)) {
            return;
        }
        Intent imgIntent = new Intent(context, ImageGridActivity.class);
        startActivityForResult(imgIntent, Constant.IMAGE_PICKER);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            LogUtil.e("uCrop:::::", resultUri.toString());
            setImageUrl(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            LogUtil.e("uCrop:::::", cropError.toString());
        } else if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == Constant.IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);

                showImg(images);
            } else {
                ViewUtils.makeToast(context, "没有数据", 500);
            }
        }
    }


    //裁剪操作
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    private void showImg(ArrayList<ImageItem> images) {
        ImageItem imageItem = images.get(0);
        Uri uri = Uri.fromFile(new File(imageItem.path));


        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;
        destinationFileName += ".png";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop = uCrop.withAspectRatio(1, 1);

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        //方框中的圆形
        options.setCircleDimmedLayer(true);
        //是否显示裁剪框网格
        options.setShowCropGrid(false);

        options.setCompressionQuality(50);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        uCrop.withOptions(options);
        uCrop.start(MyHeaduCropActivity.this);
    }


    private void setImageUrl(Uri resultUri) {

        GlideUtils.loadAvatar(context, resultUri.getPath(), ivHeadImage);

        uploadImage(BitmapManage.bitmapToString(resultUri.getPath(), 600, 600));
    }

    private void uploadImage(String imageData) {
        loadingDialog.show();
        HttpParams httpParams = OkClient.getParamsInstance().getParams();
        httpParams.put("3", "190949");
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
                    GlideUtils.loadAvatar(context, model.getStr57(), ivHeadImage);
                    StorageCustomerInfo02Util.putInfo(context, "headImage", model.getStr57());
                }
            }
        });
    }


}
