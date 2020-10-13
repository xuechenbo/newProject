package com.kotyj.com.activity.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.activity.wkyk.ImageActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.FriendMessage;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.utils.LogUtils;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.viewone.MyGridView;
import com.kotyj.com.viewone.common.CommonAdapter;
import com.kotyj.com.viewone.common.CommonViewHolder;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/8/20
 */
public class FriendListActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    Bitmap bitmap;
    private FriendMessageAdapter adapter;
    private int mPage = 1;//页码，总页数
    private List<FriendMessage> data = new ArrayList<>();
    private List<FriendMessage> mList = new ArrayList<>();
    private File file;
    private int textCount;
    private List<Bitmap> bitmapList;

    private String[] url;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String message = (String) msg.obj;
                ViewUtils.makeToast(context, message.toString(), 1000);
                smartRefreshLayout.autoRefresh();
            } else if (msg.what == 2) {
                FriendMessage friendMessage = (FriendMessage) msg.obj;
                submitRequestData(friendMessage.getId());
            }
        }
    };

    @Override
    public int initLayout() {
        return R.layout.act_friend_list;
    }

    @Override
    public void initData() {
        tvTitle.setText("素材中心");
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        smartRefreshLayout.setRefreshHeader(new ClassicsHeader(this));
        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(this));

        adapter = new FriendMessageAdapter(mList);
        adapter.bindToRecyclerView(recyclerView);

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPage = 1;
                mList.clear();
                requestData(mPage + "");
            }
        });

        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mPage++;
                requestData(mPage + "");
            }
        });
        requestData(mPage + "");
    }

    /**
     * 获取朋友圈信息
     *
     * @param page 页码
     */
    private void requestData(final String page) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "690032");
        httpParams.put("43", page);
        httpParams.put("44", "10");
        loadingDialog.show();
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {

            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
                smartRefreshLayout.finishRefresh();
                smartRefreshLayout.finishLoadmore();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                loadingDialog.dismiss();
                smartRefreshLayout.finishRefresh();
                smartRefreshLayout.finishLoadmore();
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    data = new ArrayList<>();
                    String f57 = model.getStr57();
                    try {
                        JSONArray jsonArray = new JSONArray(f57);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String createTime = jsonObject.getString("createTime");
                            JSONObject json = new JSONObject(createTime);
                            String time = json.getString("time");
                            FriendMessage friendMessage = new FriendMessage();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date date = null;
                            try {
                                date = sdf.parse(sdf.format(new Date(Long.parseLong(time))));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            friendMessage.setTime(sdf.format(date));
                            friendMessage.setMessage(jsonObject.getString("context"));
                            friendMessage.setImages(jsonObject.getString("picPaths"));
                            friendMessage.setDownsload(jsonObject.getString("downloadCount"));
                            friendMessage.setId(jsonObject.getString("id"));
                            data.add(friendMessage);
                        }
                        mList.addAll(data);
                        adapter.setNewData(mList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ViewUtils.makeToast(context, model.getStr39(), 1000);
                }
            }
        });
    }

    /**
     * 下载图片
     *
     * @param id
     */
    private void submitRequestData(String id) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "690033");
        httpParams.put("24", id);
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    loadingDialog.dismiss();
                    Message message = new Message();
                    message.what = 1;
                    message.obj = "下载图片成功";
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = 1;
                    message.obj = context.getString(R.string.server_error);
                    handler.sendMessage(message);
                }
            }
        });
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    public Bitmap returnBitMap(final String[] urlList, final FriendMessage item) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < urlList.length; i++) {
                    Bitmap bitmapTemp = null;
                    try {
                        bitmapTemp = downloadImage(urlList[i]);
                        Log.d("urlTemp", bitmapTemp.toString());
                        saveCurrentImage(bitmapTemp, urlList[i]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                Message message = new Message();
                message.what = 2;
                message.obj = item;
                handler.sendMessage(message);
            }
        }).start();

        return bitmap;
    }

    /**
     * 保存图片
     */
    private void saveCurrentImage(Bitmap bg, String url) {
        String[] PERMISSIONS = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"};
        //检测是否有写的权限
        int permission = ContextCompat.checkSelfPermission(this,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        if (url == null || url.length() <= 0) {
            return;
        }

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            String urltemp = "";
            String bitName = url.substring(url.lastIndexOf("/"));

            String fileName = "";
            if (Build.BRAND.equals("Xiaomi")) { // 小米手机
                fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + bitName;
            } else {  // Meizu 、Oppo
                fileName = Environment.getExternalStorageDirectory().getPath() + "/xjej/" + bitName;
            }
            file = new File(fileName);
            Log.d("file", file.toString());
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bg.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(new File(fileName))));
        }

    }

    private Bitmap downloadImage(String urlstr) throws Exception {
        Bitmap bitmap = null;

        //把传过来的路径转成URL
        URL url = new URL(urlstr);
        //获取连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //使用GET方法访问网络
        connection.setRequestMethod("GET");
        //超时时间为10秒
        connection.setConnectTimeout(10000);
        //获取返回码
        int code = connection.getResponseCode();
        if (code == 200) {
            InputStream inputStream = connection.getInputStream();
            //使用工厂把网络的输入流生产Bitmap
            bitmap = BitmapFactory.decodeStream(inputStream);
        }


        return bitmap;

    }

    class FriendMessageAdapter extends BaseQuickAdapter<FriendMessage, BaseViewHolder> {


        public FriendMessageAdapter(@Nullable List<FriendMessage> data) {
            super(R.layout.friend_information_list, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, final FriendMessage item) {
            url = item.getImages().split(",");
            helper.setText(R.id.time, item.getTime());
            helper.setText(R.id.tv_download, "下载量:" + item.getDownsload() + "次");
            final TextView more = helper.getView(R.id.more);
            TextView saveImage = helper.getView(R.id.save_image);
            TextView copyMessage = helper.getView(R.id.copy_message);

            final TextView message = helper.getView(R.id.message);
            message.setText(item.getMessage());
            ViewTreeObserver vto = message.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Layout layout = message.getLayout();
                    if (layout == null) {
                        return;
                    }
                    textCount = layout.getLineCount();
                    if (textCount > 3 && more.getText().toString().equals("全文")) {
                        more.setVisibility(View.VISIBLE);
                        message.setMaxLines(3);
                    } else if (textCount < 3) {
                        more.setVisibility(View.GONE);
                    }
                }
            });
            MyGridView gridView = helper.getView(R.id.gridView);
            final List<String> urlList = new ArrayList<>();
            bitmapList = new ArrayList<>();
            urlList.clear();
            bitmapList.clear();
            if (url != null && !url[0].equals("") && url.length > 0) {
                for (String i : url) {
                    urlList.add(i);
                }
                helper.setGone(R.id.tv_download, true);
                helper.setGone(R.id.save_image, true);
            } else {
                helper.setGone(R.id.tv_download, false);
                helper.setGone(R.id.save_image, false);
            }
            LogUtils.i("urlList=" + urlList.size());
            gridView.setAdapter(new MyAdapter(context, urlList, R.layout.image_list));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(context, ImageActivity.class).putExtra("url", urlList.get(position)));
                }
            });
            saveImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] urlTemp = item.getImages().split(",");
                    if (StringUtil.isEmpty(item.getImages())) {
                        ViewUtils.makeToast(context, "暂无图片", 500);
                        return;
                    }
                    returnBitMap(urlTemp, item);

                }
            });
            copyMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", message.getText().toString());
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    ViewUtils.makeToast(context, "复制成功", 1000);
                }
            });
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (message.getMaxLines() == 3) {
                        more.setText("收起");
                        message.setMaxLines(999);
                    } else if (message.getMaxLines() == 999) {
                        more.setText("全文");
                        message.setMaxLines(3);
                    }


                }
            });
        }
    }

    class MyAdapter extends CommonAdapter<String> {

        public MyAdapter(Context context, List datas, int layoutResId) {
            super(context, datas, layoutResId);
        }

        @Override
        public void convert(CommonViewHolder holder, String url) {
            holder.setImageURI(R.id.image, url);
        }
    }
}
