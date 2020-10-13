package com.kotyj.com.model;

import java.io.Serializable;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/5/10
 */
public class QueryModel implements Serializable {


    /**
     * rate : 0.7900
     * ORDER_NO : XJEJ44c175ee
     * ID : 9E0A5B593EE747428D48B83A8DFAA501
     * CREATE_TIME : {"date":16,"day":3,"hours":16,"minutes":29,"month":8,"nanos":0,"seconds":1,"time":1600244941000,"timezoneOffset":-480,"year":120}
     * cde_name : 代还还款
     * cardNo : 6225767530146943
     * TRX_AMT : 142.35
     * status : 交易成功
     */

    private String rate;
    private String ORDER_NO;
    private String ID;
    private CREATETIMEBean CREATE_TIME;
    private String cde_name;
    private String cardNo;
    private String TRX_AMT;
    private String status;

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getORDER_NO() {
        return ORDER_NO;
    }

    public void setORDER_NO(String ORDER_NO) {
        this.ORDER_NO = ORDER_NO;
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

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getTRX_AMT() {
        return TRX_AMT;
    }

    public void setTRX_AMT(String TRX_AMT) {
        this.TRX_AMT = TRX_AMT;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class CREATETIMEBean implements Serializable{
        /**
         * date : 16
         * day : 3
         * hours : 16
         * minutes : 29
         * month : 8
         * nanos : 0
         * seconds : 1
         * time : 1600244941000
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
