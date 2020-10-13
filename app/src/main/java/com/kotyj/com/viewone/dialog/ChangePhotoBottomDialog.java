package com.kotyj.com.viewone.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.kotyj.com.R;
import com.kotyj.com.utils.ViewUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChangePhotoBottomDialog extends DialogFragment {


    public Dialog loadingDialog;
    Unbinder unbinder;
    private Activity mActivity;

    public static ChangePhotoBottomDialog getInstance() {
        ChangePhotoBottomDialog shareUrlBottomDialog = new ChangePhotoBottomDialog();
        return shareUrlBottomDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
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
        View view = inflater.inflate(R.layout.dialog_photo, container);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        getDialog().setCanceledOnTouchOutside(false);
        loadingDialog = ViewUtils.createLoadingDialog(getActivity(), getString(R.string.loading_wait), false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = window.getAttributes();
        ((WindowManager.LayoutParams) layoutParams).gravity = Gravity.BOTTOM;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes((WindowManager.LayoutParams) layoutParams);
    }


    @OnClick({R.id.tv_takePhoto, R.id.tv_ChangePhoto, R.id.tv_cancel})
    public void onViewClicked(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.tv_takePhoto:
                if (onClickListener != null) {
                    onClickListener.takePhoto();
                }
                dismiss();
                break;
            case R.id.tv_ChangePhoto:
                if (onClickListener != null) {
                    onClickListener.changePhoto();
                }
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onCorrectListener) {
        this.onClickListener = onCorrectListener;
    }

    public interface OnClickListener {
        void changePhoto();

        void takePhoto();
    }

}
