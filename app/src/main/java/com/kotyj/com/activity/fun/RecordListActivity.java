package com.kotyj.com.activity.fun;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.kotyj.com.R;
import com.kotyj.com.adapter.RecordListAdapter;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.RecordListModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/8/20
 */
public class RecordListActivity extends BaseActivity {
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
    @BindView(R.id.tv_no)
    TextView tvNo;
    @BindView(R.id.ll_no)
    RelativeLayout llNo;
    @BindView(R.id.iv_no_4)
    ImageView ivNo4;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.ll_invite_num)
    LinearLayout llInviteNum;
    @BindView(R.id.ll_phone)
    LinearLayout llPhone;
    @BindView(R.id.iv_level_icon)
    ImageView ivLevelIcon;
    @BindView(R.id.tv_invite_title_1)
    TextView tvInviteTitle1;
    @BindView(R.id.tv_invite_title_2)
    TextView tvInviteTitle2;
    @BindView(R.id.cl_no)
    CardView clNo;
    private List<RecordListModel> mList = new ArrayList<>();
    private RecordListAdapter mRecordListAdapter;
    private String type = "10A";
    private RecordListModel mRecordListModel;

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        ViewUtils.overridePendingTransitionBack(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.act_record_list;
    }

    @Override
    public void initData() {
        tvTitle.setText("排行榜");
        rvList.setLayoutManager(new LinearLayoutManager(context));
        mRecordListAdapter = new RecordListAdapter(mList);
        mRecordListAdapter.bindToRecyclerView(rvList);
        rvList.addItemDecoration(new DividerItemDecoration(context, OrientationHelper.VERTICAL));
        loadData();
    }


    /**
     * 获取龙虎榜数据
     */
    private void loadData() {
        loadingDialog.show();
        HttpParams httpParams = OkClient.getParamsInstance().getParams();
        httpParams.put("3", "390015");
        httpParams.put("42", getMerNo());
        httpParams.put("43", type);
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
                    List<RecordListModel> mDayList = JSONArray.parseArray(model.getStr22(), RecordListModel.class);
                    mRecordListModel = JSONArray.parseArray(model.getStr21(), RecordListModel.class).get(0);
                    updateMineDate();
                    mList.addAll(mDayList);
                    mRecordListAdapter.setNewData(mList);
                }
            }


        });
    }

    /**
     * 显示自己排名
     */
    private void updateMineDate() {
        if (mRecordListModel == null) {
            return;
        }
        GlideUtils.loadAvatar(context, mRecordListModel.getHEAD_URL(), ivNo4);
        tvPhone.setText(CommonUtils.translateShortNumber(mRecordListModel.getPHONE(), 3, 4));
        tvNumber.setText(CommonUtils.formatNewWithScale(mRecordListModel.getCount().toString(), 0) + "");
        tvNo.setText(mRecordListModel.getRownum() == 0 ? "暂无排名" : "NO." + mRecordListModel.getRownum());
        tvInviteTitle1.setText("直推");
        tvInviteTitle2.setText("人");
    }
}
