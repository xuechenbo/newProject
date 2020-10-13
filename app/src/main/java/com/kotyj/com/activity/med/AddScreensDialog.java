package com.kotyj.com.activity.med;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddScreensDialog extends DialogFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_t)
    TextView tvT;
    @BindView(R.id.tv_sub)
    TextView tvSub;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.iv1)
    View iv1;
    @BindView(R.id.bt_diss)
    TextView btDiss;
    @BindView(R.id.bt_go)
    TextView btGo;
    private int shopNum = 10;
    private String money;

    public static AddScreensDialog getIntence(String money) {
        AddScreensDialog dialog = new AddScreensDialog();
        Bundle bundle = new Bundle();
        bundle.putString("money", money);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.custom_Dialog);
        money = getArguments().getString("money");
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
        View view = inflater.inflate(R.layout.dialog_addscreen, container);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        tvTip.setText("友情提示：10个起加，" + money + "元/个，购买个数为10的倍数。");

    }

    @OnClick({R.id.tv_sub, R.id.tv_add, R.id.bt_diss, R.id.bt_go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_sub:
                if (shopNum == 0) {
                    return;
                }
                shopNum -= 10;
                tvNum.setText(shopNum + "");
                break;
            case R.id.tv_add:
                shopNum += 10;
                tvNum.setText(shopNum + "");
                break;
            case R.id.bt_diss:
                dismiss();
                break;
            case R.id.bt_go:
                if (onPayCardListener != null) {
                    if (shopNum == 0) {
                        ViewUtils.makeToast(getActivity(), "数量为0", 1200);
                        return;
                    }
                    onPayCardListener.payNum(shopNum + "");
                    dismiss();
                } else {
                    dismiss();
                }
                break;
        }
    }


    private OnPayCardListener onPayCardListener;

    public void setOnPayCardListener(OnPayCardListener onPayCardListener) {
        this.onPayCardListener = onPayCardListener;
    }

    interface OnPayCardListener {
        void payNum(String num);
    }
}
