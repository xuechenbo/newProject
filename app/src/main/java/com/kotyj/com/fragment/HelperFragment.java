package com.kotyj.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kotyj.com.R;
import com.kotyj.com.activity.fun.OperateDetailActivity;
import com.kotyj.com.adapter.OperateAdapter;
import com.kotyj.com.base.BaseFragment;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.OperateModel;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HelperFragment extends BaseFragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private OperateAdapter operateAdapter = null;
    private List<OperateModel> list = new ArrayList<>();

    public static HelperFragment newInstance() {
        return new HelperFragment();
    }

    @Override
    public int initLayout() {
        return R.layout.fragment_helper;
    }

    @Override
    public void initData(View v) {
        tvTitle.setText("帮助中心");
        ivBack.setVisibility(View.GONE);

        operateAdapter = new OperateAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        operateAdapter.bindToRecyclerView(recyclerView);

        operateAdapter.setNewData(list);
        initListener();
        getData();
    }

    private void initListener() {
        operateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(context, OperateDetailActivity.class)
                        .putExtra("model", list.get(position))
                        .putExtra("title", "帮助中心"));
            }
        });
    }


    private void getData() {
        loadingDialog.show();
        list.clear();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "792001");
        httpParams.put("21", "10B");
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
                    list = JSONArray.parseArray(model.getStr57(), OperateModel.class);
                    operateAdapter.setNewData(list);
                }
            }
        });
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
