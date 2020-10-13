package com.kotyj.com.model;

public class InfoModel {


    /**
     * fenlei : 0
     * create_time : {"date":7,"day":5,"hours":16,"minutes":53,"month":7,"nanos":0,"seconds":10,"time":1596790390000,"timezoneOffset":-480,"year":120}
     * level : null
     * image_url : http://nbyihuan.com/image/uploadImage/news/202008071652570.3855906290305138.jpg
     * share_url : http://nbyihuan.com/image/news/C2ABD030B2D44E78B5779745005CBB35.html
     * id : C2ABD030B2D44E78B5779745005CBB35
     * order_num : 0
     * title : 升级标准及费率返佣
     * content : <p><img src="http://nbyihuan.com/image/uploadImage/news/202008071652570.3855906290305138.jpg" title="202008071652570.3855906290305138" alt="202008071652570.3855906290305138"/></p>
     */

    private String fenlei;
    private CreateTimeBean create_time;
    private Object level;
    private String image_url;
    private String share_url;
    private String id;
    private int order_num;
    private String title;
    private String content;

    public String getFenlei() {
        return fenlei;
    }

    public void setFenlei(String fenlei) {
        this.fenlei = fenlei;
    }

    public CreateTimeBean getCreate_time() {
        return create_time;
    }

    public void setCreate_time(CreateTimeBean create_time) {
        this.create_time = create_time;
    }

    public Object getLevel() {
        return level;
    }

    public void setLevel(Object level) {
        this.level = level;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrder_num() {
        return order_num;
    }

    public void setOrder_num(int order_num) {
        this.order_num = order_num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static class CreateTimeBean {
        /**
         * date : 7
         * day : 5
         * hours : 16
         * minutes : 53
         * month : 7
         * nanos : 0
         * seconds : 10
         * time : 1596790390000
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
