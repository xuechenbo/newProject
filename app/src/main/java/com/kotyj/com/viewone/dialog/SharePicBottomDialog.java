package com.kotyj.com.viewone.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kotyj.com.R;
import com.kotyj.com.utils.DeviceUtils;
import com.kotyj.com.utils.ViewUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class SharePicBottomDialog extends DialogFragment {


    public Dialog loadingDialog;

    Unbinder unbinder;
    @BindView(R.id.iv_wx)
    ImageView ivWx;
    @BindView(R.id.iv_pyq)
    ImageView ivPyq;
    @BindView(R.id.iv_qq)
    ImageView ivQq;
    @BindView(R.id.iv_kj)
    ImageView ivKj;
    @BindView(R.id.pop_layout)
    LinearLayout popLayout;
    private Context mContext;

    public static SharePicBottomDialog getInstance(Context context) {
        return new SharePicBottomDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MyProgressDialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        unbinder.unbind();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_share, container);

        mContext = getActivity();
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        getDialog().setCanceledOnTouchOutside(false);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(DeviceUtils.getScreenWidth(mContext), ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog = ViewUtils.createLoadingDialog(getActivity(), getString(R.string.loading_wait), false);
    }


//    @OnClick({R.id.tv_wechat, R.id.tv_wechat_friend, R.id.tv_qq, R.id.tv_qq_space, R.id.tv_cancel})
//    public void onViewClicked(View view) {
//        dismiss();
//        switch (view.getId()) {
//            case R.id.tv_wechat:
//
//                loadingDialog.show();
//                goShare(Wechat.NAME);
//                break;
//            case R.id.tv_wechat_friend:
//                loadingDialog.show();
//                goShare(WechatMoments.NAME);
//                break;
//            case R.id.tv_qq:
//                loadingDialog.show();
//                goShare(QQ.NAME);
//                break;
//            case R.id.tv_qq_space:
//                loadingDialog.show();
//                goShare(QZone.NAME);
//                break;
//            case R.id.tv_cancel:
//                dismiss();
//                break;
//        }
//    }

    private void goShare(String name) {
        Platform.ShareParams sp4 = new Platform.ShareParams();
        sp4.setImagePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tyll/tyll.png");
        Platform qzone = ShareSDK.getPlatform(name);
        qzone.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                loadingDialog.dismiss();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                loadingDialog.dismiss();
            }
        });
        // 执行图文分享
        qzone.share(sp4);
    }


}
