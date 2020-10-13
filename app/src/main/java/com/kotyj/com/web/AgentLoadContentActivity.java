package com.kotyj.com.web;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.agentweb.AgentWeb;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgentLoadContentActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.frame)
    FrameLayout frame;
    private AgentWeb agentWeb;


    @Override
    public int initLayout() {
        return R.layout.activity_agent_web;
    }

    @Override
    public void initData() {

        tvTitle.setText(getIntent().getStringExtra("title"));
        String url = getIntent().getStringExtra("url");

        agentWeb = AgentWeb.with(this)
                .setAgentWebParent(frame, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
//                .setWebChromeClient(mWebChromeClient)
//                .setWebViewClient(mWebViewClient)
                .createAgentWeb()
                .ready()
                .go(url);


        WebView mWebView = agentWeb.getWebCreator().getWebView();
        WebSettings mSettings = mWebView.getSettings();
        mSettings.setJavaScriptEnabled(true);
        mSettings.setSupportZoom(true);
        mSettings.setBuiltInZoomControls(true);
        //不显示缩放按钮
        mSettings.setDisplayZoomControls(false);
        //设置自适应屏幕，两者合用
        //将图片调整到适合WebView的大小
        mSettings.setUseWideViewPort(true);
        //缩放至屏幕的大小
        mSettings.setLoadWithOverviewMode(true);
        //自适应屏幕
        mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
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
}
