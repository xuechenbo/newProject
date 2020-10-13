package com.kotyj.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.kotyj.com.R;
import com.kotyj.com.activity.fun.ShareListActivity;
import com.kotyj.com.activity.mine.BankManagerActivity;
import com.kotyj.com.activity.mine.MyClientActivity;
import com.kotyj.com.activity.mine.MyIntegerActivity;
import com.kotyj.com.activity.mine.MyResultsActivity;
import com.kotyj.com.activity.mine.MyWalletActivity;
import com.kotyj.com.activity.mine.SelfMessageActivity;
import com.kotyj.com.activity.mine.ServiceCenterActivity;
import com.kotyj.com.activity.mine.SettingActivity;
import com.kotyj.com.activity.mine.VIPActivity;
import com.kotyj.com.base.BaseFragment;
import com.kotyj.com.event.AuthEvent;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.UserInfoModel;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.Utils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotlin.com.wkyk.web.AgentWebActivity;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MineFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.iv_leve)
    ImageView ivLeve;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_level)
    TextView tvLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public int initLayout() {
        return R.layout.fragment_my;
    }

    @Override
    public void initData(View v) {
        loadBenefitData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadBenefitData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String headImage = StorageCustomerInfo02Util.getInfo("headImage", context);
        String nickname = StorageCustomerInfo02Util.getInfo("nickname", context);
        String merchantCnName = StorageCustomerInfo02Util.getInfo("merchantCnName", context);
        GlideUtils.loadAvatar(context, headImage, ivAvatar);

        if (!nickname.isEmpty()) {
            tvName.setText(nickname);
        } else {
            tvName.setText(merchantCnName);
        }
    }

    @OnClick({R.id.iv_update, R.id.iv_real, R.id.iv_pack, R.id.iv_up, R.id.iv_share, R.id.tv_wall, R.id.tv_team, R.id.tv_results, R.id.tv_integer, R.id.tv_Insur, R.id.tv_ustomer, R.id.tv_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_update:
                startActivity(new Intent(context, SelfMessageActivity.class));
                break;
            case R.id.iv_real:
                if (checkCustomerInfoCompleteShowToast()) {
                    ViewUtils.makeToast(context, "已通过实名认证", 1200);
                }
                break;
            case R.id.iv_pack:
                if (!checkCustomerInfoCompleteShowToast()) {
                    return;
                }
                startActivity(new Intent(context, BankManagerActivity.class));
                break;
            case R.id.iv_up:
                startActivity(new Intent(context, VIPActivity.class));
                break;
            case R.id.iv_share:
                startActivity(new Intent(context, ShareListActivity.class));
                break;
            case R.id.tv_wall:
                if (!checkCustomerInfoCompleteShowToast()) {
                    return;
                }
                startActivity(new Intent(context, MyWalletActivity.class));
                break;
            case R.id.tv_team:
                if (checkCustomerInfoCompleteShowToast()) {
                    startActivity(new Intent(context, MyClientActivity.class));
                }
                break;
            case R.id.tv_results:
                //我的业绩
                if (!checkCustomerInfoCompleteShowToast()) {
                    return;
                }
                startActivity(new Intent(context, MyResultsActivity.class));
                break;
            case R.id.tv_integer:
                if (!checkCustomerInfoCompleteShowToast()) {
                    return;
                }
                startActivity(new Intent(context, MyIntegerActivity.class));
                break;
            case R.id.tv_Insur:
                startActivity(new Intent(context, AgentWebActivity.class)
                        .putExtra("title", "我的保单")
                        .putExtra("url", "https://url.cn/5xzqvvb"));
                break;
            case R.id.tv_ustomer:
                if (!checkCustomerInfoCompleteShowToast()) {
                    return;
                }
                startActivity(new Intent(context, ServiceCenterActivity.class));
                break;
            case R.id.tv_setting:
                startActivity(new Intent(context, SettingActivity.class));
                break;
        }
    }

    private void loadBenefitData() {
        HttpParams httpParams = OkClient.getParamsInstance().getParams();
        httpParams.put("3", "190112");
        httpParams.put("42", getMerNo());
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onSuccess(Response<BaseEntity> response) {
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    GlideUtils.loadAvatar(context, model.getStr48(), ivAvatar);
                    if (!StringUtil.isEmpty(model.getStr48())) {
                        StorageCustomerInfo02Util.putInfo(context, "headImage", model.getStr48());
                    }

                    try {
                        List<UserInfoModel> list = JSONArray.parseArray(model.getStr57(), UserInfoModel.class);
                        if (list.size() == 0) {
                            return;
                        }
                        UserInfoModel userInfoModel = list.get(0);


                        StorageCustomerInfo02Util.putInfo(context, "isPay", userInfoModel.getIsPay());

                        //昵称
                        StorageCustomerInfo02Util.putInfo(context, "nickname", userInfoModel.getMerchantEnName());

                        StorageCustomerInfo02Util.putInfo(context, "isIntermediary", userInfoModel.getIsIntermediary());
                        StorageCustomerInfo02Util.putInfo(context, "payPsd", userInfoModel.getPayPassword());
                        StorageCustomerInfo02Util.putInfo(context, "bankAccount", userInfoModel.getBankAccount());
                        StorageCustomerInfo02Util.putInfo(context, "merchantCnName", userInfoModel.getMerchantCnName());

                        String intermediarySwitch = userInfoModel.getIntermediarySwitch();
                        StorageCustomerInfo02Util.putInfo(context, "intermediarySwitch", intermediarySwitch);

                        StorageCustomerInfo02Util.putInfo(context, "freezeStatus", userInfoModel.getFreezeStatus());
                        String level = userInfoModel.getLevel();

                        StorageCustomerInfo02Util.putInfo(context, "isMember", userInfoModel.getIsMember());

                        if (userInfoModel.getIsMember().equals("1")) {
                            ivLeve.setVisibility(View.VISIBLE);
                        }
                        StorageCustomerInfo02Util.putInfo(context, "level", level);

                        tvLevel.setText(Utils.GetLeveName(level));
                        StorageCustomerInfo02Util.putInfo(context, "withdrawFee", model.getStr25());

                        updateAuthData(userInfoModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateAuthData(UserInfoModel userInfoModel) {
        String phone = userInfoModel.getPhone();
        String name = userInfoModel.getMerchantCnName();

        if (!userInfoModel.getMerchantEnName().isEmpty()) {
            tvName.setText(userInfoModel.getMerchantEnName());
        } else {
            tvName.setText(!StringUtil.isEmpty(name) ? name : phone);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Subscribe
    public void onEvent(AuthEvent authEvent) {
        loadBenefitData();
    }

}
