package com.kotyj.com.model;

import com.google.gson.annotations.SerializedName;

public class EarningModel {


    /**
     * PHONE : 15239585210
     * ID : WQEQWE13123123123
     * CREATE_TIME : {"date":15,"day":2,"hours":16,"minutes":35,"month":8,"nanos":0,"seconds":59,"time":1600158959000,"timezoneOffset":-480,"year":120}
     * cde_name : 刷卡分润
     * TRX_AMT : 12.73
     */

    @SerializedName(value = "phone", alternate = {"PHONE"})
    private String PHONE;
    private String ID;
    private CREATETIMEBean CREATE_TIME;
    private String cde_name;
    private String TRX_AMT;

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public CREATETIMEBean getCREATE_TIME() {
        return CREATE_TIME;
    }

    public void setCREATE_TIME(CREATETIMEBean CREATE_TIME) {
        this.CREATE_TIME = CREATE_TIME;
    }

    public String getCde_name() {
        return cde_name;
    }

    public void setCde_name(String cde_name) {
        this.cde_name = cde_name;
    }

    public String getTRX_AMT() {
        return TRX_AMT;
    }

    public void setTRX_AMT(String TRX_AMT) {
        this.TRX_AMT = TRX_AMT;
    }

    public static class CREATETIMEBean {
        /**
         * date : 15
         * day : 2
         * hours : 16
         * minutes : 35
         * month : 8
         * nanos : 0
         * seconds : 59
         * time : 1600158959000
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
