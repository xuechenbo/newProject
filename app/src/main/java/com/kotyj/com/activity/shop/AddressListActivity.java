package com.kotyj.com.activity.shop;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kotyj.com.R;
import com.kotyj.com.adapter.AddressAdapter;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.AddressEvent;
import com.kotyj.com.model.AddressModel;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.utils.FastJsonUtils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class AddressListActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.srl_refresh)
    SmartRefreshLayout srlRefresh;
    @BindView(R.id.cl_container)
    RelativeLayout clContainer;
    private AddressAdapter myAdapter;
    private List<AddressModel> mList = new ArrayList<>();
    private boolean fromOrder;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_refresh_recycler_view;
    }

    @Override
    public void initData() {
        tvTitle.setText("收货地址");
        fromOrder = getIntent().getBooleanExtra("formOrder", false);
        EventBus.getDefault().register(this);
        srlRefresh.setRefreshHeader(new ClassicsHeader(context));
        rvList.setLayoutManager(new LinearLayoutManager(context));
        myAdapter = new AddressAdapter(mList);
        myAdapter.bindToRecyclerView(rvList);
        initListener();
        requestData();
    }


    private void initListener() {
        srlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                requestData();
            }
        });
        myAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.tv_edit:
                        //TODO：编辑
                        startActivity(new Intent(context, AddressAddActivity.class).putExtra("addressBean", mList.get(position)).putExtra("isModify", true));
                        break;
                    case R.id.tv_delete:
                        //TODO：删除
                        requestAddressDelete(mList.get(position));
                        break;
                    default:
                        break;
                }
            }
        });
        myAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (fromOrder) {
                    Intent intent = new Intent();
                    intent.putExtra("data", mList.get(position));
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    startActivity(new Intent(context, AddressAddActivity.class).putExtra("addressBean", mList.get(position)).putExtra("isModify", true));
                }
            }
        });
    }

    /**
     * 请求删除收货地址
     *
     * @param addressBean
     */
    private void requestAddressDelete(AddressModel addressBean) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "590018");
        httpParams.put("9", addressBean.getId());//收货地址ID
        httpParams.put("10", getMerId());//商户Id
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
                    ViewUtils.makeToast(context, "删除成功", 1500);

                    EventBus.getDefault().post(new AddressEvent());
                }
            }


        });
    }

    /**
     * 收货地址列表
     */
    private void requestData() {
        loadingDialog.show();
        mList.clear();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "590012");
        httpParams.put("9", getMerId());
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
                    mList.addAll(FastJsonUtils.toList(model.getStr40(), AddressModel.class));
                } else if ("01".equals(model.getStr39())) {
                    ViewUtils.makeToast(context, model.getStr40(), 1000);
                }
                myAdapter.notifyDataSetChanged();
            }
        });

    }

    @Subscribe
    public void onEvent(AddressEvent addressEvent) {
        requestData();
    }

    @OnClick({R.id.iv_back, R.id.iv_right, R.id.tv_addAddress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ViewUtils.overridePendingTransitionBack(context);
                break;
            case R.id.iv_right:
                startActivity(new Intent(context, AddressAddActivity.class));
                break;
            case R.id.tv_addAddress:
                startActivity(new Intent(context, AddressAddActivity.class));
                return;
        }
    }

}
