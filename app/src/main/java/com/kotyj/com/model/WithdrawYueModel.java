package com.kotyj.com.model;

public class WithdrawYueModel {


    /**
     * money  : 10
     * create_time : {"date ":16,"day ":3,"hours ":16,"minutes ":57,"month ":8,"nanos ":0,"seconds ":44,"time ":1600246664000,"timezoneOffset ":-480,"year ":120}
     * id : 23D87BAF48464946A2D4F5832D25E4E4
     */

    private int money;
    private CreateTimeBean create_time;
    private String id;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public CreateTimeBean getCreate_time() {
        return create_time;
    }

    public void setCreate_time(CreateTimeBean create_time) {
        this.create_time = create_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class CreateTimeBean {
        /**
         * date  : 16
         * day  : 3
         * hours  : 16
         * minutes  : 57
         * month  : 8
         * nanos  : 0
         * seconds  : 44
         * time  : 1600246664000
         * timezoneOffset  : -480
         * year  : 120
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
