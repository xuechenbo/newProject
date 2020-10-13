package com.kotyj.com.activity.mine;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.ResultsModel;
import com.kotyj.com.utils.ChartUtils;
import com.kotyj.com.utils.Utils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.github.mikephil.charting.data.LineDataSet.Mode.CUBIC_BEZIER;

public class MyResultsActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lineChart)
    LineChart lineChart;

    List<Entry> list1 = new ArrayList<>();
    List<Entry> list2 = new ArrayList<>();
    @BindView(R.id.tl_tab)
    TabLayout tlTab;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_compare)
    TextView tvCompare;
    @BindView(R.id.tv_hk)
    TextView tvHk;
    @BindView(R.id.tv_sk)
    TextView tvSk;

    @Override
    public int initLayout() {
        return R.layout.act_myresults;
    }

    @Override
    public void initData() {
        //990009

        tvTitle.setText("我的业绩");


        initListener();
        loadData("10A", true);

    }


    private void initListener() {
        tlTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadData("10A", false);
                } else if (tab.getPosition() == 1) {
                    loadData("10B", false);
                } else {
                    loadData("10C", false);
                }
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    private void loadData(String type, final boolean flag) {
        loadingDialog.show();
        HttpParams map = OkClient.getParamsInstance().getParams();
        map.put("3", "990009");
        map.put("43", type);
        map.put("42", getMerId());
        loadingDialog.show();
        OkClient.getInstance().post(map, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
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
                String result = model.getStr39();
                if ("00".equals(result)) {
                    List<ResultsModel> wk_list = new Gson().fromJson(model.getStr45(), new TypeToken<List<ResultsModel>>() {
                    }.getType());
                    List<ResultsModel> yk_list = new Gson().fromJson(model.getStr46(), new TypeToken<List<ResultsModel>>() {
                    }.getType());


                    initMsg(wk_list, yk_list, flag);


                    String str21 = model.getStr21();
                    String str22 = model.getStr22();
                    String str23 = model.getStr23();
                    String str24 = model.getStr24();

                    tvMoney.setText("￥" + str21);
                    tvCompare.setText(str22 + "  上周");
                    tvHk.setText(str23);
                    tvSk.setText(str24);

                } else {
                    ViewUtils.makeToast(context, result, 500);
                }
            }
        });
    }


    private void initMsg(List<ResultsModel> wk_list, List<ResultsModel> yk_list, boolean flag) {
        //循环添加设置x轴和y轴的点
        list1.clear();
        list2.clear();


        for (int i = 0; i < yk_list.size(); i++) {
            list1.add(new Entry(i, Float.parseFloat(yk_list.get(i).getMoney())));
        }
        for (int i = 0; i < wk_list.size(); i++) {
            list2.add(new Entry(i, Float.parseFloat(wk_list.get(i).getMoney())));
        }


        String maxNumber = Utils.getMaxNumber(wk_list, yk_list);

        ChartUtils.initChart(lineChart, context, Integer.parseInt(maxNumber));

        if (flag) {
            initLineChart();
        } else {
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }

    }

    private void initLineChart() {
        //TODO 第一条曲线
        LineDataSet one = new LineDataSet(list1, "one");
        //TODO 第二条曲线
        LineDataSet two = new LineDataSet(list2, "two");


        ChartUtils.initLineDataSet(one, getResources().getColor(R.color.chart1), CUBIC_BEZIER);
        ChartUtils.initLineDataSet(two, getResources().getColor(R.color.chart2), CUBIC_BEZIER);

        ChartUtils.showChartLine(one, two, lineChart);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }


}
