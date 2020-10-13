package com.kotyj.com.viewone.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kotyj.com.R;
import com.kotyj.com.utils.LogUtil;
import com.kotyj.com.viewone.SeparatedEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PayPswDialog extends DialogFragment {
    Unbinder unbinder;
    @BindView(R.id.pas)
    SeparatedEditText pas;
    OnCorrectListener onCorrectListener;

    public static PayPswDialog getIntence() {
        PayPswDialog dialog = new PayPswDialog();
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.custom_Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_pay_psw, container);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        pas.setTextChangedListener(new SeparatedEditText.TextChangedListener() {
            @Override
            public void textChanged(CharSequence changeText) {
                LogUtil.e("textChanged", changeText.toString());
            }

            @Override
            public void textCompleted(CharSequence text) {
                LogUtil.e("textCompleted", text.toString());
                if (onCorrectListener != null) {
                    onCorrectListener.onCorrect(text.toString());
                }
            }
        });
    }


    public interface OnCorrectListener {
        void onCorrect(String pwd);
    }

    public void setOnCorrectListener(OnCorrectListener onCorrectListener) {
        this.onCorrectListener = onCorrectListener;
    }
}
