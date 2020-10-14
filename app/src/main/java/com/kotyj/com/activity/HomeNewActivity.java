package com.kotyj.com.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.fragment.HelperFragment;
import com.kotyj.com.fragment.InforFragment;
import com.kotyj.com.fragment.MainFragment;
import com.kotyj.com.fragment.MineFragment;
import com.kotyj.com.fragment.ShopFragment;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.UserInfoModel;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//TODO 修改更新
public class HomeNewActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.fl_content)
    FrameLayout flContent;
    @BindView(R.id.rb_shop)
    RadioButton rbShop;
    @BindView(R.id.rb_infor)
    RadioButton rbInfor;
    @BindView(R.id.rb_home)
    RadioButton rbHome;
    @BindView(R.id.rb_help)
    RadioButton rbHelp;
    @BindView(R.id.rb_mine)
    RadioButton rbMine;
    @BindView(R.id.rg_tab)
    RadioGroup rgTab;
    @BindView(R.id.cl_container)
    ConstraintLayout clContainer;
    private FragmentManager fm;
    private MainFragment mainFragment = null;
    private MineFragment mineFragment = null;
    private ShopFragment shopFragment = null;
    private HelperFragment helperFragment = null;
    private InforFragment inforFragment = null;
    private long firstime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_home;
    }

    @Override
    public void initData() {
        mainFragment = MainFragment.newInstance();
        fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.fl_content, mainFragment);
        transaction.commit();
        rbShop.setOnCheckedChangeListener(this);
        rbInfor.setOnCheckedChangeListener(this);
        rbHome.setOnCheckedChangeListener(this);
        rbHelp.setOnCheckedChangeListener(this);
        rbMine.setOnCheckedChangeListener(this);

        getMessage();
    }

    private void getMessage() {
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
                    try {
                        List<UserInfoModel> list = JSONArray.parseArray(model.getStr57(), UserInfoModel.class);
                        if (list.size() == 0) {
                            return;
                        }

                        UserInfoModel userInfoModel = list.get(0);


                        StorageCustomerInfo02Util.putInfo(context, "isIntermediary", userInfoModel.getIsIntermediary());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /** 设置双击退出 */
            long secondtime = System.currentTimeMillis();
            if (secondtime - firstime > 3000) {
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                firstime = System.currentTimeMillis();
                return true;
            } else {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            FragmentTransaction transaction = fm.beginTransaction();
            switch (compoundButton.getId()) {
                case R.id.rb_shop:
                    if (inforFragment != null)
                        transaction.hide(inforFragment);
                    if (mainFragment != null)
                        transaction.hide(mainFragment);
                    if (helperFragment != null)
                        transaction.hide(helperFragment);
                    if (mineFragment != null)
                        transaction.hide(mineFragment);
                    if (shopFragment == null) {
                        shopFragment = new ShopFragment();
                        transaction.add(R.id.fl_content, shopFragment);
                    } else {
                        transaction.show(shopFragment);
                    }
                    break;
                case R.id.rb_infor:
                    if (shopFragment != null)
                        transaction.hide(shopFragment);
                    if (mainFragment != null)
                        transaction.hide(mainFragment);
                    if (helperFragment != null)
                        transaction.hide(helperFragment);
                    if (mineFragment != null)
                        transaction.hide(mineFragment);
                    if (inforFragment == null) {
                        inforFragment = new InforFragment();
                        transaction.add(R.id.fl_content, inforFragment);
                    } else {
                        transaction.show(inforFragment);
                    }
                    break;
                case R.id.rb_home:
                    if (shopFragment != null)
                        transaction.hide(shopFragment);
                    if (inforFragment != null)
                        transaction.hide(inforFragment);
                    if (helperFragment != null)
                        transaction.hide(helperFragment);
                    if (mineFragment != null)
                        transaction.hide(mineFragment);
                    if (mainFragment == null) {
                        mainFragment = new MainFragment();
                        transaction.add(R.id.fl_content, mainFragment);
                    } else {
                        transaction.show(mainFragment);
                    }
                    break;
                case R.id.rb_help:
                    if (shopFragment != null)
                        transaction.hide(shopFragment);
                    if (mainFragment != null)
                        transaction.hide(mainFragment);
                    if (inforFragment != null)
                        transaction.hide(inforFragment);
                    if (mineFragment != null)
                        transaction.hide(mineFragment);
                    if (helperFragment == null) {
                        helperFragment = new HelperFragment();
                        transaction.add(R.id.fl_content, helperFragment);
                    } else {
                        transaction.show(helperFragment);
                    }
                    break;
                case R.id.rb_mine:
                    if (shopFragment != null)
                        transaction.hide(shopFragment);
                    if (mainFragment != null)
                        transaction.hide(mainFragment);
                    if (helperFragment != null)
                        transaction.hide(helperFragment);
                    if (inforFragment != null)
                        transaction.hide(inforFragment);
                    if (mineFragment == null) {
                        mineFragment = new MineFragment();
                        transaction.add(R.id.fl_content, mineFragment);
                    } else {
                        transaction.show(mineFragment);
                    }
                    break;
                default:
                    break;
            }
            transaction.commit();
        }
    }
}
