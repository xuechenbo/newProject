package com.kotyj.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kotyj.com.R;
import com.kotyj.com.activity.fun.OperateDetailActivity;
import com.kotyj.com.base.BaseFragment;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.InfoModel;
import com.kotyj.com.utils.DateUtil;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InforFragment extends BaseFragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private MyAdapter operateAdapter = null;
    private List<InfoModel> list = new ArrayList<>();

    public static InforFragment newInstance() {
        return new InforFragment();
    }

    @Override
    public int initLayout() {
        return R.layout.fragment_infor;
    }

    @Override
    public void initData(View v) {
        tvTitle.setText("资讯");
        ivBack.setVisibility(View.GONE);

        operateAdapter = new MyAdapter(R.layout.item_operate, list);
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
                        .putExtra("type", true)
                        .putExtra("title", "资讯详情")
                        .putExtra("Toptitle", list.get(position).getTitle())
                        .putExtra("time", DateUtil.formatDateToHM(list.get(position).getCreate_time().getTime()))
                        .putExtra("content", list.get(position).getContent()));
            }
        });

    }


    private void getData() {
        loadingDialog.show();
        list.clear();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "898905");
        httpParams.put("41", "0");
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
                    list = new Gson().fromJson(model.getStr40(), new TypeToken<List<InfoModel>>() {
                    }.getType());
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


    class MyAdapter extends BaseQuickAdapter<InfoModel, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<InfoModel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, InfoModel item) {
            helper.setText(R.id.tv_title, item.getTitle())
                    .setGone(R.id.iv, false)
                    .setText(R.id.tv_time, DateUtil.formatDateToHM_(item.getCreate_time().getTime()));

        }
    }
}
