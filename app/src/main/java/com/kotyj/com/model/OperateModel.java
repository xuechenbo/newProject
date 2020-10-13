package com.kotyj.com.model;

import java.io.Serializable;

/**
 * @作者 chenlanxin
 * @创建日期 2019/10/14 15:34
 * @功能
 **/
public class OperateModel implements Serializable {


    /**
     * createTime : {"date":19,"day":3,"hours":22,"minutes":8,"month":1,"nanos":0,"seconds":31,"time":1582121311000,"timezoneOffset":-480,"year":120}
     * id : 3A42BC35DE38435082CEC1B512744F17
     * title : 问：分享二维码不显示图片二维码
     */

    private CreateTimeBean createTime;
    private String id;
    private String title;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public CreateTimeBean getCreateTime() {
        return createTime;
    }

    public void setCreateTime(CreateTimeBean createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static class CreateTimeBean implements Serializable {
        /**
         * date : 19
         * day : 3
         * hours : 22
         * minutes : 8
         * month : 1
         * nanos : 0
         * seconds : 31
         * time : 1582121311000
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
