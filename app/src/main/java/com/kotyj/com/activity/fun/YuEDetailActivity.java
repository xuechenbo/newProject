package com.kotyj.com.activity.fun;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.YueModel;
import com.kotyj.com.utils.DateUtil;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class YuEDetailActivity extends BaseActivity {
    int page = 1;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    List<YueModel> mList = new ArrayList<>();
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    private MyAdapter myAdapter;


    @Override
    public int initLayout() {
        return R.layout.act_yuedetail;
    }

    @Override
    public void initData() {
        String title = getIntent().getStringExtra("title");
        tvTitle.setText(title);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        myAdapter = new MyAdapter(R.layout.item_yue, mList);
        myAdapter.bindToRecyclerView(recyclerView);
        myAdapter.setNewData(mList);
        initListener();
        requestData(page);
    }

    private void initListener() {
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                mList.clear();
                requestData(page);
            }
        });
        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                requestData(page);
            }
        });
    }

    private void requestData(int page) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "792004");
        httpParams.put("7", page + "");
        httpParams.put("42", getMerNo());
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onSuccess(Response<BaseEntity> response) {
                loadingDialog.dismiss();
                smartRefreshLayout.finishLoadmore();
                smartRefreshLayout.finishRefresh();
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    List<YueModel> list = new Gson().fromJson(model.getStr57(), new TypeToken<List<YueModel>>() {
                    }.getType());
                    mList.addAll(list);
                    myAdapter.setNewData(mList);
                } else {
                    ViewUtils.makeToast(context, model.getStr39(), 1200);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    class MyAdapter extends BaseQuickAdapter<YueModel, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<YueModel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, YueModel item) {
            helper.setText(R.id.tv_name, item.getType())
                    .setText(R.id.tv_date, DateUtil.formatDateToHMS(item.getCreate_time().getTime()))
                    .setText(R.id.tv_money, item.getScore());
        }
    }
}
