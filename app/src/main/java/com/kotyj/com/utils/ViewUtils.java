package com.kotyj.com.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kotyj.com.R;
import com.kotyj.com.model.NoticeModel;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.Timer;
import java.util.TimerTask;


public class ViewUtils {


    public static Dialog showValidatePhoto(Context context) {
        final Dialog dialog = new Dialog(context, R.style.MyProgressDialog);
        dialog.setContentView(R.layout.dialog_show_validate_photo);
        dialog.findViewById(R.id.ll_showValidatePhoto).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }


    public static Dialog showCvvPhoto(Context context) {
        final Dialog dialog = new Dialog(context, R.style.MyProgressDialog);
        dialog.setContentView(R.layout.dialog_show_cvv_photo);
        dialog.findViewById(R.id.ll_showValidatePhoto).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    public static void makeToast(Context context, String text, int duration) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_toast_layout, null);
        TextView chapterNameTV = (TextView) view.findViewById(R.id.chapterName);
        chapterNameTV.setText(text);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(duration);
        toast.setView(view);
        toast.show();
    }

    public static void makeToast2(final Activity context,
                                  CharSequence text, int duration,
                                  final Class<?> intentClassName, final String intentType) {
        if (!TextUtils.isEmpty(text)) {
            Toast result = new Toast(context);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.toast_layout, null);
            TextView textView = (TextView) layout.findViewById(R.id.toast_text);
            textView.setText(text);

            result.setView(layout);
            result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            result.setDuration(duration);
            result.show();
        }


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (null == intentClassName) {
                    context.finish();
                    return;
                }
                if (!StringUtil.isEmpty(intentType)) {
                    Intent intent = new Intent(context, intentClassName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        }, 1000);
    }


    public static void overridePendingTransitionCome(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public static void overridePendingTransitionComeFinish(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        activity.finish();
    }

    public static void overridePendingTransitionBack(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg, boolean isCancel) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
        // main.xml中的ImageView  
        CircleProgressBar img = (CircleProgressBar) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        img.setColorSchemeResources(R.color.black);

        tipTextView.setText(msg);

        Dialog loadingDialog = new Dialog(context, R.style.transparentBackDialog);
        loadingDialog.setCancelable(isCancel);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
        return loadingDialog;

    }


    public static Dialog showChoseDialog(Context context, boolean isCanceledOnTouchOutside, String msg, int cancelVisibale,
                                         final OnChoseDialogClickCallback onChoseDialogClickCallback) {
        final Dialog dialog = new Dialog(context, R.style.MyProgressDialog);
        dialog.setContentView(R.layout.chose_dialog);
        dialog.setCanceledOnTouchOutside(false);
        Button confirmBt = (Button) dialog.findViewById(R.id.left_bt);
        Button cancleBt = (Button) dialog.findViewById(R.id.right_bt);
        dialog.findViewById(R.id.verticalbars_iv).setVisibility(cancelVisibale);
        cancleBt.setVisibility(cancelVisibale);
        ((TextView) dialog.findViewById(R.id.title_text)).setText(msg);
        cancleBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChoseDialogClickCallback != null) {
                    dialog.dismiss();
                    onChoseDialogClickCallback.clickCancel();
                } else {
                    dialog.dismiss();
                }
            }
        });
        confirmBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChoseDialogClickCallback != null) {
                    dialog.dismiss();
                    onChoseDialogClickCallback.clickOk();
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        return dialog;
    }

    /**
     * 用于获取状态栏的高度。 使用Resource对象获取（推荐这种方式）
     *
     * @return 返回状态栏高度的像素值。
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public interface OnChoseDialogClickCallback {
        void clickOk();

        void clickCancel();
    }


    public static Dialog showDialogStandard(Context context, String title, String content, String leftName, String rightName, final OnshowDialogStandard onshowDialogStandard) {
        final Dialog dialog = new Dialog(context, R.style.MyProgressDialog);
        dialog.setContentView(R.layout.act_standardialog);
        TextView tv_title = dialog.findViewById(R.id.tv_title);
        TextView tv_content = dialog.findViewById(R.id.tv_content);
        tv_title.setText(title);
        TextView bt_diss = dialog.findViewById(R.id.bt_diss);
        TextView bt_known = dialog.findViewById(R.id.bt_known);

        tv_content.setText(content);

        if (!leftName.isEmpty()) {
            bt_diss.setText(leftName);
        }
        if (!rightName.isEmpty()) {
            bt_known.setText(rightName);
        }

        bt_diss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onshowDialogStandard != null) {
                    onshowDialogStandard.clickCancel();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        });
        bt_known.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onshowDialogStandard != null) {
                    dialog.dismiss();
                    onshowDialogStandard.clickOk();
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        return dialog;
    }


    public static Dialog showDialogStandard3(Context context, String title, String leftName, String rightName, final OnshowDialogStandard onshowDialogStandard) {
        final Dialog dialog = new Dialog(context, R.style.MyProgressDialog);
        dialog.setContentView(R.layout.act_standardialog3);
        TextView tv_title = dialog.findViewById(R.id.tv_title);
        tv_title.setText(title);
        TextView bt_diss = dialog.findViewById(R.id.bt_diss);
        TextView bt_known = dialog.findViewById(R.id.bt_known);
        if (!leftName.isEmpty()) {
            bt_diss.setText(leftName);
        }
        if (!rightName.isEmpty()) {
            bt_known.setText(rightName);
        }

        bt_diss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onshowDialogStandard != null) {
                    onshowDialogStandard.clickCancel();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        });
        bt_known.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onshowDialogStandard != null) {
                    dialog.dismiss();
                    onshowDialogStandard.clickOk();
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        return dialog;
    }


    public interface OnshowDialogStandard {
        void clickOk();

        void clickCancel();
    }


    public static Dialog showTipCard(Context context, final OnshowDialogStandard onshowDialogStandard) {

        final Dialog dialog = new Dialog(context, R.style.MyProgressDialog);
        dialog.setContentView(R.layout.dialog_tip);
        TextView bt_diss = dialog.findViewById(R.id.btn_cancel);
        TextView bt_known = dialog.findViewById(R.id.btn_admit);
        bt_diss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onshowDialogStandard != null) {
                    dialog.dismiss();
                    onshowDialogStandard.clickCancel();
                } else {
                    dialog.dismiss();
                }
            }
        });
        bt_known.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onshowDialogStandard != null) {
                    dialog.dismiss();
                    onshowDialogStandard.clickOk();
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        return dialog;
    }


    public static Dialog showNoticeDetail(Context context, final NoticeModel noticeModel, final OnshowNoticeListener onshowNoticeListener) {
        final Dialog dialog = new Dialog(context, R.style.MyProgressDialog);
        dialog.setContentView(R.layout.dialog_show_notice_details);
        TextView bt_diss = dialog.findViewById(R.id.btn_cancel);
        TextView bt_known = dialog.findViewById(R.id.btn_admit);
        TextView tv_content = dialog.findViewById(R.id.tv_content);
        TextView tv_time = dialog.findViewById(R.id.tv_time);
        TextView tv_title = dialog.findViewById(R.id.tv_title);
        tv_title.setText(noticeModel.getTitle());
        tv_content.setText(noticeModel.getContent());
        tv_time.setText("信聚e家" + DateUtil.formatDateToHM_(noticeModel.getUpdateDate().getTime()));

        bt_known.setText(noticeModel.getHasRead() == 0 ? "标记已读" : "确定");

        bt_diss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onshowNoticeListener != null) {
                    dialog.dismiss();
                    onshowNoticeListener.clickCancel();
                } else {
                    dialog.dismiss();
                }
            }
        });
        bt_known.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onshowNoticeListener != null) {
                    dialog.dismiss();
                    onshowNoticeListener.clickOk(noticeModel);
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        return dialog;
    }

    public interface OnshowNoticeListener {
        void clickOk(NoticeModel noticeModel);

        void clickCancel();
    }


}
