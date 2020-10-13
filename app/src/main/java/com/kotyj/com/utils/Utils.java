/*
 * Copyright 2011 ??辩?ヤ?????
 * Website:http://www.azsy.cn/
 * Email:info锛?azsy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kotyj.com.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.kotyj.com.R;
import com.kotyj.com.model.ItemModel;
import com.kotyj.com.model.NoticeModel;
import com.kotyj.com.model.ResultsModel;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Utils {

    public static String getMaxNumber(List<ResultsModel> wk_list, List<ResultsModel> yk_list) {
        List<ResultsModel> sortWkList = wk_list;
        List<ResultsModel> sortYkList = yk_list;
        Collections.sort(sortWkList);
        Collections.sort(sortYkList);
        String wkMax = new BigDecimal(sortWkList.get(0).getMoney()).setScale(0, BigDecimal.ROUND_DOWN).toString();
        String ykMax = new BigDecimal(sortYkList.get(0).getMoney()).setScale(0, BigDecimal.ROUND_DOWN).toString();
        if (Integer.parseInt(wkMax) > Integer.parseInt(ykMax)) {
            return wkMax;
        } else {
            return ykMax;
        }
    }

    public static NoticeModel getNoticeModel(String content) {
        NoticeModel noticeModel = new Gson().fromJson(content, NoticeModel.class);
        return noticeModel;
    }


    public static String getPayStatus(String type) {
        String typeName = "";
        switch (type) {
            case "10A":
                typeName = "等待付款";
                break;
            case "10B"://支付中
                typeName = "支付中";
                break;
            case "10C"://待发货
                typeName = "等待发货";
                break;
            case "10D"://待签收 显示确认收货按钮
                typeName = "等待收货";
                break;
            case "10E"://已签收
                typeName = "交易完成";
                break;
            case "10F"://已取消
                typeName = "交易关闭";
                break;
            case "70A"://支付失败
                typeName = "支付失败";
                break;
            default:
                typeName = "未知状态";
                break;
        }
        return typeName;
    }


    public static String getShopPayType(String money, String point) {
        String shopMoney = "";
        int num = new BigDecimal(point).compareTo(BigDecimal.ZERO);
        int num1 = new BigDecimal(money).compareTo(BigDecimal.ZERO);
        if (num == 0) {
            shopMoney = "￥" + ToGet00(money) + "元";
        } else if (num1 == 0) {
            shopMoney = ToGet00(point) + "积分";
        } else {
            shopMoney = ToGet00(point) + "积分" + "+" + "￥" + ToGet00(money) + "元";
        }
        return shopMoney;
    }


    public static String ToGet00(String num) {
        return new BigDecimal(num).stripTrailingZeros().toPlainString();
    }


    public static String getShopTotalMoney(ItemModel mItemModel) {

        int num = new BigDecimal(mItemModel.getPoint()).compareTo(BigDecimal.ZERO);
        int num1 = new BigDecimal(mItemModel.getPrice()).compareTo(BigDecimal.ZERO);

        if (num == 0) {
            BigDecimal add3 = new BigDecimal(mItemModel.getPrice()).multiply(new BigDecimal(mItemModel.getGoodsCount())).setScale(2, BigDecimal.ROUND_HALF_UP);
            return "合计：" + add3.stripTrailingZeros().toPlainString() + "元";
        } else if (num1 == 0) {
            return "合计：" + ToGet00(mItemModel.getPoint() * mItemModel.getGoodsCount() + "") + "积分";
        } else {
            BigDecimal add = new BigDecimal(mItemModel.getPrice()).multiply(new BigDecimal(mItemModel.getGoodsCount())).setScale(2, BigDecimal.ROUND_HALF_UP);
            return "合计：" + ToGet00(mItemModel.getPoint() * mItemModel.getGoodsCount() + "") + "积分" + "+" + add.stripTrailingZeros().toPlainString() + "元";
        }
    }


    public static String getMedFreezeStatus(String freezeStatus) {
        String freeConten = "";
        if ("10B".equals(freezeStatus)) {
            freeConten = "审核通过";
        } else if ("10C".equals(freezeStatus)) {
            freeConten = "审核拒绝";
        } else if ("10A".equals(freezeStatus)) {
            freeConten = "未认证";
        } else if ("10F".equals(freezeStatus)) {
            freeConten = "审核中";
        } else if ("10E".equals(freezeStatus)) {
            freeConten = "照片不全";
        } else if ("10D".equals(freezeStatus)) {
            freeConten = "重新认证";
        } else {
            freeConten = "";
        }
        return freeConten;
    }

    public static String GetLeveName(String level) {
        String leveName;
        if (TextUtils.isEmpty(level)) {
            leveName = "用户";
        } else if ("1".equals(level)) {
            leveName = "用户";
        } else if ("2".equals(level)) {
            leveName = "VIP";
        } else if ("3".equals(level)) {
            leveName = "经理";
        } else if ("4".equals(level)) {
            leveName = "总监";
        } else if ("5".equals(level)) {
            leveName = "董事";
        } else if ("6".equals(level)) {
            leveName = "分公司";
        } else if ("0".equals(level)) {
            leveName = "中介";
        } else {
            leveName = "";
        }
        return leveName;
    }

    /**
     * ?????扮?????
     *
     * @return
     */
    public static String encode(String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLEncoder.encode(s, "UTF-8").replace("+", "%20")
                    .replace("*", "%2A").replace("%7E", "~")
                    .replace("#", "%23");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String decode(String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String getCurrentTime() {
        return getCurrentTime(new Date(), "yyyy-MM-dd  HH:mm:ss");
    }

    public static String getCurrentTime(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String parseTime(long time) {
        return getCurrentTime(time, "yyyy-MM-dd  HH:mm:ss");
    }

    public static String getCurrentTime(long time, String format) {
        Date date = new Date(time);
        return getCurrentTime(date, format);
    }

    public static boolean isTime(String filterString) {
        if (StringUtil.isEmpty(filterString)) {
            return false;
        }
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(filterString);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return false;
        }
        return true;
    }

    /**
     * 检测String是否全是中文
     *
     * @param name
     * @return
     */
    public static boolean checkNameChinese(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (isChinese(cTemp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 判定输入汉字
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static void initBankCodeColorIcon(String bankCode, ImageView iv_bankIcon, Context context) {
        if (bankCode == null) {
            return;
        }
        if (bankCode.equals("304")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_huaxia));
        } else if (bankCode.equals("105")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_jianshe));
        } else if (bankCode.equals("301")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_jiaotong));
        } else if (bankCode.equals("305")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_mingsheng));
        } else if (bankCode.equals("103")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_nongye));
        } else if (bankCode.equals("310")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_pudong));
        } else if (bankCode.equals("308")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_zhaoshang));
        } else if (bankCode.equals("104")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_zhongguo));
        } else if (bankCode.equals("403")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_youzheng));
        } else if (bankCode.equals("102")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_gongshang));
        } else if (bankCode.equals("303")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_guangda));
        } else if (bankCode.equals("313062")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_shanghai));
        } else if (bankCode.equals("309")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_xingye));
        } else if (bankCode.equals("302")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_zhongxin));
        } else if (bankCode.equals("307")) {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_pinan));
        } else {
            iv_bankIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_yinlian));
        }
    }

}

