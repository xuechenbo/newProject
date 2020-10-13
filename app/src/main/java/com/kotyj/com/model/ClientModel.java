package com.kotyj.com.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/4/7
 */
public class ClientModel implements Serializable {


    /**
     * phone : 171****2614
     * CREATE_TIME : {"date":6,"day":4,"hours":15,"minutes":54,"month":7,"nanos":0,"seconds":16,"time":1596700456000,"timezoneOffset":-480,"year":120}
     * teamNum : 0
     * ID : 41EDEF198A4249F5AF0C5343BCC793A3
     * merchant_cn_name : 17194242614
     * MERCHANT_CODE : 000080000100010
     */

    @SerializedName(value = "phone", alternate = {"PHONE"})
    private String phone;
    private CREATETIMEBean CREATE_TIME;
    private String teamNum;
    private String ID;
    @SerializedName(value = "merchant_cn_name", alternate = {"MERCHANT_CN_NAME"})
    private String merchant_cn_name;
    private String MERCHANT_CODE;

    public String getLEVEL() {
        return LEVEL;
    }

    public void setLEVEL(String LEVEL) {
        this.LEVEL = LEVEL;
    }

    private String LEVEL;

    public String getIsDirect() {
        return isDirect;
    }

    public void setIsDirect(String isDirect) {
        this.isDirect = isDirect;
    }

    private String isDirect;

    public String getPhone() {
        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CREATETIMEBean getCREATE_TIME() {
        return CREATE_TIME;
    }

    public void setCREATE_TIME(CREATETIMEBean CREATE_TIME) {
        this.CREATE_TIME = CREATE_TIME;
    }

    public String getTeamNum() {
        return teamNum == null ? "0" : teamNum;
    }

    public void setTeamNum(String teamNum) {
        this.teamNum = teamNum;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMerchant_cn_name() {
        return merchant_cn_name;
    }

    public void setMerchant_cn_name(String merchant_cn_name) {
        this.merchant_cn_name = merchant_cn_name;
    }

    public String getMERCHANT_CODE() {
        return MERCHANT_CODE;
    }

    public void setMERCHANT_CODE(String MERCHANT_CODE) {
        this.MERCHANT_CODE = MERCHANT_CODE;
    }

    public static class CREATETIMEBean {
        /**
         * date : 6
         * day : 4
         * hours : 15
         * minutes : 54
         * month : 7
         * nanos : 0
         * seconds : 16
         * time : 1596700456000
         * timezoneOffset : -480
         * year : 120
         */

        private int date;
        private int day;
        private int hours;
        private int minutes;
        private int month;
        private int nanos;
        private int seconds;
        private long time;
        private int timezoneOffset;
        private int year;

        public int getDate() {
            return date;
        }

        public void setDate(int date) {
            this.date = date;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHours() {
            return hours;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getNanos() {
            return nanos;
        }

        public void setNanos(int nanos) {
            this.nanos = nanos;
        }

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public int getTimezoneOffset() {
            return timezoneOffset;
        }

        public void setTimezoneOffset(int timezoneOffset) {
            this.timezoneOffset = timezoneOffset;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }
}
