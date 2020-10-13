package com.kotyj.com.activity.fun;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.OperateModel;
import com.kotyj.com.utils.DateUtil;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.viewone.X5WebView;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OperateDetailActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_small_title)
    TextView tvSmallTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.webview)
    X5WebView webview;
    @BindView(R.id.tv_small_time)
    TextView tvSmallTime;

    private OperateModel model = null;
    private boolean type;

    @Override
    public int initLayout() {
        return R.layout.activity_operate_detail;
    }

    @Override
    public void initData() {
        //TODO 资讯详情进来true    新手指引，帮助中心 false
        type = getIntent().getBooleanExtra("type", false);

        tvTitle.setText(getIntent().getStringExtra("title"));
        model = (OperateModel) getIntent().getSerializableExtra("model");

        if (type) {
            tvSmallTime.setText("信聚e家  " + getIntent().getStringExtra("time"));
        } else {
            tvSmallTime.setText("信聚e家  " + DateUtil.formatDateToHM(model.getCreateTime().getTime()));
        }


        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setSupportZoom(false);
        settings.setUseWideViewPort(false);

        webview.setBackgroundColor(0);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webview.loadUrl(url);
                return true;


            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView webView, boolean b, boolean b1, Message message) {
                WebView newWebView = new WebView(context);
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // 在此处进行跳转URL的处理, 一般情况下_black需要重新打开一个页面,
                        startActivity(new Intent(context, WebViewActivity.class).putExtra("url", url));
                        return true;
                    }

                });
                WebView.WebViewTransport transport = (WebView.WebViewTransport) message.obj;
                transport.setWebView(newWebView);
                message.sendToTarget();
                return true;
            }
        });
        if (type) {
            String replace = getIntent().getStringExtra("content").replace("_self", "_blank");
            webview.loadDataWithBaseURL(null, getHtmlData(replace), "text/html", "UTF-8", null);
            tvSmallTitle.setText(getIntent().getStringExtra("Toptitle"));
        } else {
            getData();
        }

    }

    private void getData() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "792002");
        httpParams.put("21", model.getId());
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
                    OperateModel operateModel = JSONArray.parseArray(model.getStr57(), OperateModel.class).get(0);
                    String replace = operateModel.getContent().replace("_self", "_blank");
                    webview.loadDataWithBaseURL(null, getHtmlData(replace), "text/html", "UTF-8", null);
                    tvSmallTitle.setText(operateModel.getTitle());
                }
            }
        });
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:100%; height:auto;}*{margin:0px;}</style>"
                + "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }
}
