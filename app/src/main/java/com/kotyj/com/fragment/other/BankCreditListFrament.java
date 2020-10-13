package com.kotyj.com.fragment.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.kotyj.com.R;
import com.kotyj.com.activity.wkyk.newwkyk.AddBankCardActivity;
import com.kotyj.com.base.BaseFragment;
import com.kotyj.com.event.BankCardEvent;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.BindCard;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class BankCreditListFrament extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.banner)
    MZBannerView banner;
    @BindView(R.id.bt_addCard)
    Button btAddCard;
    private List<BindCard> mList = new ArrayList<>();

    public static BankCreditListFrament newInstance() {
        return new BankCreditListFrament();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int initLayout() {
        return R.layout.frag_bank_credit;
    }

    @Override
    public void initData(View v) {
        requestData();
    }


    @OnClick(R.id.bt_addCard)
    public void onViewClicked() {
        String merchantCnName = StorageCustomerInfo02Util.getInfo("merchantCnName", context);
        String idCardNumber = StorageCustomerInfo02Util.getInfo("idCardNumber", context);
        Intent intent = new Intent(context, AddBankCardActivity.class);
        intent.putExtra("name", merchantCnName);
        intent.putExtra("idCard", idCardNumber);
        intent.putExtra("merchantId", getMerId());
        startActivity(intent);
    }

    public class BannerViewHolder implements MZViewHolder<BindCard> {
        private TextView tv_bank_account, tv_exdate;

        @Override
        public View createView(Context context) {
            // 返回页面布局
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item, null);
            tv_bank_account = view.findViewById(R.id.tv_bank_account);
            tv_exdate = view.findViewById(R.id.tv_exdate);
            return view;
        }

        @Override
        public void onBind(Context context, int i, BindCard bindCard) {
            tv_bank_account.setText(CommonUtils.translateShortNumber(bindCard.getBANK_ACCOUNT(), 4, 4));
            StringBuffer stringBuilder1 = new StringBuffer(bindCard.getExpdate());
            stringBuilder1.insert(2, "/");


            tv_exdate.setText(stringBuilder1.toString());
        }
    }


    /**
     * 获取信用卡列表
     */
    private void requestData() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "190932");
        httpParams.put("42", getMerNo());
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
                    mList = JSONArray.parseArray(model.getStr57(), BindCard.class);
                    banner.setPages(mList, new MZHolderCreator<BannerViewHolder>() {
                        @Override
                        public BannerViewHolder createViewHolder() {
                            return new BannerViewHolder();
                        }
                    });
                }
            }
        });
    }

    /**
     * 绑卡成功后，自动刷新数据
     *
     * @param event
     */
    @Subscribe
    public void onEvent(BankCardEvent event) {
        requestData();
    }


}
