package com.kotyj.com.activity.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.kotyj.com.R;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.model.TakePhotoOptions;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyHeadPhotoActivity extends TakePhotoActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        setContentView(R.layout.act_head_photo);
        ButterKnife.bind(this);
        initData();
    }

    public void initData() {


    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        showImg(result.getImages());
    }

    private void showImg(ArrayList<TImage> images) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("images", images);
        startActivity(intent);
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                gotuPhoto(true);
                break;
        }
    }


    void gotuPhoto(boolean flag) {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);
        TakePhoto takePhoto = getTakePhoto();
        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);
        if (flag) {
            takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
        } else {
            takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
        }

    }


    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        //用系统相册
//        builder.setWithOwnGallery(true);
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());

    }


    //TODO 裁剪配置
    private CropOptions getCropOptions() {
        CropOptions.Builder builder = new CropOptions.Builder();
        //宽*搞
        builder.setAspectX(1).setAspectY(1);
        builder.setWithOwnCrop(false);//设置这个为false
        return builder.create();

        //take自带裁剪
//        builder.setWithOwnCrop(true);
//        return builder.create();
    }

    private void configCompress(TakePhoto takePhoto) {
        //是否压缩
//        if (rgCompress.getCheckedRadioButtonId() != R.id.rbCompressYes) {
//            takePhoto.onEnableCompress(null, false);
//            return;
//        }
        //尺寸比例
        int maxSize = Integer.parseInt("102400");
        int width = Integer.parseInt("800");
        int height = Integer.parseInt("800");

        CompressConfig config;

        //自带压缩工具
        config = new CompressConfig.Builder().setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(true)//压缩后是否保存原图
                .create();
        //luban压缩
//        LubanOptions option = new LubanOptions.Builder().setMaxHeight(height).setMaxWidth(width).setMaxSize(maxSize).create();
//        config = CompressConfig.ofLuban(option);
//        config.enableReserveRaw(enableRawFile);
        //true 显示压缩进度条
        takePhoto.onEnableCompress(config, true);
    }
}
