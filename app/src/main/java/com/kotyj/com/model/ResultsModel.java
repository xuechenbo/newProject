package com.kotyj.com.model;

import android.support.annotation.NonNull;

public class ResultsModel implements Comparable<ResultsModel> {

    /**
     * "week\":\"星期"money\"type\":\"W"day\":\"2020-09-14\"
     */

    private String week;
    private String money;
    private String type;
    private String day;

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }


    @Override
    public int compareTo(@NonNull ResultsModel resultsModel) {
        return new Double(resultsModel.money).compareTo(new Double(this.money));
    }


    @Override
    public String toString() {
        return money;
    }
}
