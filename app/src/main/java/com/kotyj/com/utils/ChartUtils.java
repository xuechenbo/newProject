package com.kotyj.com.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.kotyj.com.R;
import com.kotyj.com.viewone.MyMarkerView;

import java.math.BigDecimal;

public class ChartUtils {


    //TODO XY轴设置
    public static void initChart(LineChart lineChart, Context context, float num) {
        /***图表设置***/

        lineChart.setBackgroundColor(context.getResources().getColor(R.color.themeColor3));
        //是否展示网格线
        lineChart.setDrawGridBackground(false);
        //是否显示边界
        lineChart.setDrawBorders(false);
        //是否可以拖动
        lineChart.setDragEnabled(false);
        //是否有触摸事件
        lineChart.setTouchEnabled(true);

        //设置图表的描述文字，会显示在图表的右下角。
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);


        //设置点击交点的提示
        MyMarkerView mv = new MyMarkerView(context, R.layout.mark_layout);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);


        //设置XY轴动画效果
        lineChart.animateY(2500);
        lineChart.animateX(1500);

        /***XY轴的设置***/
        XAxis xAxis = lineChart.getXAxis();


        YAxis leftYAxis = lineChart.getAxisLeft();

        YAxis rightYaxis = lineChart.getAxisRight();


        leftYAxis.enableGridDashedLine(10f, 10f, 0f);


        Log.e("最大值设置：：：", num + "");
        //设置y轴最大值
        leftYAxis.setAxisMaximum(num + 50);

        //禁掉X Y轴自己的网格线，
        xAxis.setDrawAxisLine(false);
        rightYaxis.setDrawGridLines(false);
        leftYAxis.setDrawGridLines(false);

        //设置X Y轴网格线为虚线
        //leftYAxis.enableGridDashedLine(10f, 10f, 0f);

        //去掉右侧y轴
        rightYaxis.setEnabled(false);
        leftYAxis.setEnabled(false);
        xAxis.setEnabled(true);

        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        //绘制标签
        xAxis.setDrawLabels(true);
        //X轴字体颜色
        xAxis.setTextColor(Color.WHITE);
        // 设置显示的坐标点数量
        xAxis.setLabelCount(6, false);
        //X轴的自定义数据格式
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return getWeekName(value);
            }
        });


        //保证Y轴从0开始，不然会上移一点
        leftYAxis.setAxisMinimum(0f);
        rightYaxis.setAxisMinimum(0f);


//        lineChart.setViewPortOffsets(50, 50, 60, 100);


        /***折线图例 标签 设置***/
        Legend legend = lineChart.getLegend();
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        legend.setTextColor(context.getResources().getColor(R.color.themeColor3));

        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        //是否绘制在图表里面
        legend.setDrawInside(false);

    }


    //TODO 曲线设置
    public static void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        //设置折线图圆边颜色 circle （画圆圈）
        lineDataSet.setCircleColor(Color.BLACK);
        //设置折线图圆中心颜色
        lineDataSet.setCircleColorHole(color);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(true);

        lineDataSet.setDrawValues(false);

        //设置折线图填充
        lineDataSet.setDrawFilled(true);
        //设置填充颜色
        lineDataSet.setFillColor(color);
        //设置透明度
        lineDataSet.setFillAlpha(200);

        //显示文字颜色
        lineDataSet.setValueTextColor(color);
        lineDataSet.setValueTextSize(10f);

        lineDataSet.setFormLineWidth(1f);
        //设置图例大小
        lineDataSet.setFormSize(0);


//        lineDataSet.setValueFormatter(new IValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//                BigDecimal bigDecimal = new BigDecimal(value).setScale(2, BigDecimal.ROUND_DOWN);
//                return bigDecimal.toString();
//            }
//        });


        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }

    public static void showChartLine(LineDataSet one, LineDataSet two, LineChart lineChart) {
        LineData lineData = new LineData();// //线的总管理
        lineData.addDataSet(one);//每加一条就add一次
        lineData.addDataSet(two);//每加一条就add一次
        lineChart.setData(lineData);//把线条设置给你的lineChart上
        lineChart.invalidate();//刷新
    }


    public static String getWeekName(float value) {
        String weekName = "";
        switch ((int) value) {
            case 0:
                weekName = "MON";
                break;
            case 1:
                weekName = "TUE";
                break;
            case 2:
                weekName = "WED";
                break;
            case 3:
                weekName = "THU";
                break;
            case 4:
                weekName = "FRI";
                break;
            case 5:
                weekName = "SAT";
                break;
            case 6:
                weekName = "SUN";
                break;
        }
        return weekName;
    }
}