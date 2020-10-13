package com.kotyj.com.model;

import java.io.Serializable;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/9/5
 */
public class UrlModel implements Serializable {


    private String DK;
    private String BK;
    private String JF;
    private String DS;

    public String getDS() {
        return DS == null ? "" : DS;
    }

    public void setDS(String DS) {
        this.DS = DS;
    }

    public String getDK() {
        return DK;
    }

    public void setDK(String DK) {
        this.DK = DK;
    }

    public String getBK() {
        return BK;
    }

    public void setBK(String BK) {
        this.BK = BK;
    }

    public String getJF() {
        return JF;
    }

    public void setJF(String JF) {
        this.JF = JF;
    }
}
