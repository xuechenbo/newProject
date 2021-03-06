package com.kotyj.com.activity.wkyk;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/4/2
 */
public class ImageActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    private String url, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.act_image;
    }

    @Override
    public void initData() {
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        tvTitle.setText(title);
        GlideUtils.loadImage(context, url, ivImage);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        ViewUtils.overridePendingTransitionBack(context);
    }
}
