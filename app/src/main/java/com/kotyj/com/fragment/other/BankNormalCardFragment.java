package com.kotyj.com.fragment.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.activity.mine.ChangeAuthBankCardActivity;
import com.kotyj.com.base.BaseFragment;
import com.kotyj.com.event.BankChangeEvent;
import com.kotyj.com.utils.StorageCustomerInfo02Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BankNormalCardFragment extends BaseFragment {


    @BindView(R.id.btn_change)
    Button btnChange;
    Unbinder unbinder;
    @BindView(R.id.tv_bank_account)
    TextView tvBankAccount;
    @BindView(R.id.tv_exdate)
    TextView tvExdate;


    public static BankNormalCardFragment newInstance() {
        return new BankNormalCardFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int initLayout() {
        return R.layout.frag_bank_normal_card;
    }

    @Override
    public void initData(View v) {
        EventBus.getDefault().register(this);
        updateData();
    }

    private void updateData() {
        tvExdate.setVisibility(View.GONE);
        String bankAccount = StorageCustomerInfo02Util.getInfo("bankAccount", context);
        String bankNum2 = "", bankNum1 = "";
        if (bankAccount.length() > 4) {
            bankNum1 = bankAccount.substring(0, 4);
            bankNum2 = bankAccount.substring(bankAccount.length() - 4, bankAccount.length());
        }
        tvBankAccount.setText(bankNum1 + String.format(" **** **** %s", bankNum2));

    }

    @Subscribe
    public void onEvent(BankChangeEvent event) {

        updateData();
    }


    @OnClick(R.id.btn_change)
    public void onViewClicked() {
        startActivity(new Intent(context, ChangeAuthBankCardActivity.class));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
