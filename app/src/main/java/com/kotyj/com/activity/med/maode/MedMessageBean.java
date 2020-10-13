package com.kotyj.com.activity.med.maode;

import java.io.Serializable;

public class MedMessageBean implements Serializable {
    private String phone;
    private String idcard;
    private String name;
    private String cardNum;
    private String bankName;
    private String bankCode;
    private String ykRate;
    private String ykRate2;
    private String qykRate;

    public String getYkRate2() {
        return ykRate2;
    }

    public void setYkRate2(String ykRate2) {
        this.ykRate2 = ykRate2;
    }

    public String getQykRate2() {
        return qykRate2;
    }

    public void setQykRate2(String qykRate2) {
        this.qykRate2 = qykRate2;
    }

    private String qykRate2;

    public String getMedNo() {
        return MedNo;
    }

    public void setMedNo(String medNo) {
        MedNo = medNo;
    }

    private String MedNo;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getYkRate() {
        return ykRate;
    }

    public void setYkRate(String ykRate) {
        this.ykRate = ykRate;
    }

    public String getQykRate() {
        return qykRate;
    }

    public void setQykRate(String qykRate) {
        this.qykRate = qykRate;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    private Boolean flag;
}
