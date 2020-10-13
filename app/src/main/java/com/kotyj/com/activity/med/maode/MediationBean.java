package com.kotyj.com.activity.med.maode;

import java.io.Serializable;

public class MediationBean implements Serializable {


    /**
     * planCount : 0
     * MERCHANT_NO : C220701120099942
     * MERCHANT_CN_NAME : 魏晓磊
     * PHONE : 15694804272
     * cardCount : 0
     * ID : 49B48909B5E54C07845CF29E6E00801D
     */

    private String planCount;
    private String MERCHANT_NO;
    private String MERCHANT_CN_NAME;
    private String PHONE;
    private String cardCount;
    private String ID;
    private String FREEZE_STATUS;
    private String BANK_ACCOUNT;

    public String getID_CARD_NUMBER() {
        return ID_CARD_NUMBER;
    }

    public void setID_CARD_NUMBER(String ID_CARD_NUMBER) {
        this.ID_CARD_NUMBER = ID_CARD_NUMBER;
    }

    private String ID_CARD_NUMBER;

    public String getBANK_ACCOUNT() {
        return BANK_ACCOUNT == null ? "" : BANK_ACCOUNT;
    }

    public void setBANK_ACCOUNT(String BANK_ACCOUNT) {
        this.BANK_ACCOUNT = BANK_ACCOUNT;
    }


    public String getFREEZE_STATUS() {
        return FREEZE_STATUS;
    }

    public void setFREEZE_STATUS(String FREEZE_STATUS) {
        this.FREEZE_STATUS = FREEZE_STATUS;
    }


    public String getPlanCount() {
        return planCount;
    }

    public void setPlanCount(String planCount) {
        this.planCount = planCount;
    }

    public String getMERCHANT_NO() {
        return MERCHANT_NO;
    }

    public void setMERCHANT_NO(String MERCHANT_NO) {
        this.MERCHANT_NO = MERCHANT_NO;
    }

    public String getMERCHANT_CN_NAME() {
        return MERCHANT_CN_NAME;
    }

    public void setMERCHANT_CN_NAME(String MERCHANT_CN_NAME) {
        this.MERCHANT_CN_NAME = MERCHANT_CN_NAME;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getCardCount() {
        return cardCount;
    }

    public void setCardCount(String cardCount) {
        this.cardCount = cardCount;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
