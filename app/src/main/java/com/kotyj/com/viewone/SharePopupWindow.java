package com.kotyj.com.viewone;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.kotyj.com.R;


public class SharePopupWindow extends PopupWindow {
    ImageView wexin_tv, pengyouquan_tv, qq_tv, kongjian_tv;
    private View mMenuView;

    public SharePopupWindow(Activity context, View.OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.dialog_share, null);

        wexin_tv = mMenuView.findViewById(R.id.iv_wx);
        pengyouquan_tv = mMenuView.findViewById(R.id.iv_pyq);
        qq_tv = mMenuView.findViewById(R.id.iv_qq);
        kongjian_tv = mMenuView.findViewById(R.id.iv_kj);
        //设置按钮监听
        wexin_tv.setOnClickListener(itemsOnClick);
        pengyouquan_tv.setOnClickListener(itemsOnClick);
        qq_tv.setOnClickListener(itemsOnClick);
        kongjian_tv.setOnClickListener(itemsOnClick);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.DropDownUp);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(-00000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }
}
