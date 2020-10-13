package com.kotyj.com.model;


public class YueModel {


    /**
     * id : 9A3D8E5CEB8C49328254CD40DF90F6A7
     * create_time : {"date":5,"day":3,"hours":11,"minutes":22,"month":7,"nanos":0,"seconds":53,"time":1596597773000,"timezoneOffset":-480,"year":120}
     * score : -1
     * type : 积分转让
     * merchant_id : 770DEF9C271145419EC1CFE6D9067A40
     */

    private String id;
    private CreateTimeBean create_time;
    private String score;
    private String type;
    private String merchant_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CreateTimeBean getCreate_time() {
        return create_time;
    }

    public void setCreate_time(CreateTimeBean create_time) {
        this.create_time = create_time;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public static class CreateTimeBean {
        /**
         * date : 5
         * day : 3
         * hours : 11
         * minutes : 22
         * month : 7
         * nanos : 0
         * seconds : 53
         * time : 1596597773000
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
