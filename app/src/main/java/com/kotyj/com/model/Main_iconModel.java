package com.kotyj.com.model;

public class Main_iconModel {

    /**
     * id : EB60C27E4215404689D23D777BE13119
     * create_time : {"date":10,"day":1,"hours":16,"minutes":53,"month":7,"nanos":0,"seconds":53,"time":1597049633000,"timezoneOffset":-480,"year":120}
     * type_name : 养卡
     * image : http://47.112.106.165:80/image/app_image/027533E54E8D4A8DA54A292BE9CD723E.jpg?0.7185623713387785
     * type : 10A
     */

    private String id;
    private String product_short_name;
    private CreateTimeBean create_time;
    private String type_name;
    private String image;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_short_name() {
        return product_short_name;
    }

    public void setProduct_short_name(String product_short_name) {
        this.product_short_name = product_short_name;
    }

    public CreateTimeBean getCreate_time() {
        return create_time;
    }

    public void setCreate_time(CreateTimeBean create_time) {
        this.create_time = create_time;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class CreateTimeBean {
        /**
         * date : 10
         * day : 1
         * hours : 16
         * minutes : 53
         * month : 7
         * nanos : 0
         * seconds : 53
         * time : 1597049633000
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
