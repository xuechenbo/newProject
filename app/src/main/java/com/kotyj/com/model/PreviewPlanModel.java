package com.kotyj.com.model;

import java.io.Serializable;
import java.util.HashMap;


public class PreviewPlanModel implements Serializable {

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }


    public String getSingMoney() {
        return singMoney;
    }

    public void setSingMoney(String singMoney) {
        this.singMoney = singMoney;
    }

    private String singMoney;
    private String rate;

    private String totalXfNum;
    private String totalXfMoney;


    private String workingFund;
    //计划时间
    private String planTime;

    private String hkBs;

    private String fee;
    private String repayAmount;

    private String timesFee;
    private String dayTimes;
    private String startDate;
    private String endDate;
    private String acqcode;
    private String f57;
    private HashMap<String, Area> area;
    private String isLuodi;
    private String isZiXuan;
    private String industryJson;

    private String totalFee;
    private boolean zhia;
    private String feeLossAmount;
    private boolean isGround;
    private String totalServiceFee;
    private String channelName;
    private boolean channels;
    private String channelType;


    public String getTotalXfNum() {
        return totalXfNum;
    }

    public void setTotalXfNum(String totalXfNum) {
        this.totalXfNum = totalXfNum;
    }

    public String getTotalXfMoney() {
        return totalXfMoney;
    }

    public void setTotalXfMoney(String totalXfMoney) {
        this.totalXfMoney = totalXfMoney;
    }

    public String getHkBs() {
        return hkBs;
    }

    public void setHkBs(String hkBs) {
        this.hkBs = hkBs;
    }

    public String getChannelType() {
        return channelType == null ? "" : channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public boolean isChannels() {
        return channels;
    }

    public void setChannels(boolean channels) {
        this.channels = channels;
    }

    public String getChannelName() {
        return channelName == null ? "" : channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getTotalServiceFee() {
        return totalServiceFee == null ? "" : totalServiceFee;
    }

    public void setTotalServiceFee(String totalServiceFee) {
        this.totalServiceFee = totalServiceFee;
    }

    public boolean isGround() {
        return isGround;
    }

    public void setGround(boolean ground) {
        isGround = ground;
    }

    public String getFeeLossAmount() {
        return feeLossAmount == null ? "" : feeLossAmount;
    }

    public void setFeeLossAmount(String feeLossAmount) {
        this.feeLossAmount = feeLossAmount;
    }

    public boolean isZhia() {
        return zhia;
    }

    public void setZhia(boolean zhia) {
        this.zhia = zhia;
    }

    public String getTotalFee() {
        return totalFee == null ? "" : totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getIndustryJson() {
        return industryJson == null ? "" : industryJson;
    }

    public void setIndustryJson(String industryJson) {
        this.industryJson = industryJson;
    }

    public String getIsLuodi() {
        return isLuodi == null ? "" : isLuodi;
    }

    public void setIsLuodi(String isLuodi) {
        this.isLuodi = isLuodi;
    }

    public String getIsZiXuan() {
        return isZiXuan == null ? "" : isZiXuan;
    }

    public void setIsZiXuan(String isZiXuan) {
        this.isZiXuan = isZiXuan;
    }

    public HashMap<String, Area> getArea() {
        return area;
    }

    public void setArea(HashMap<String, Area> area) {
        this.area = area;
    }

    public String getF57() {
        return f57 == null ? "" : f57;
    }

    public void setF57(String f57) {
        this.f57 = f57;
    }

    public String getWorkingFund() {
        return workingFund == null ? "" : workingFund;
    }

    public void setWorkingFund(String workingFund) {
        this.workingFund = workingFund;
    }

    public String getFee() {
        return fee == null ? "" : fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getRepayAmount() {
        return repayAmount == null ? "" : repayAmount;
    }

    public void setRepayAmount(String repayAmount) {
        this.repayAmount = repayAmount;
    }

    public String getTimesFee() {
        return timesFee == null ? "" : timesFee;
    }

    public void setTimesFee(String timesFee) {
        this.timesFee = timesFee;
    }

    public String getDayTimes() {
        return dayTimes == null ? "" : dayTimes;
    }

    public void setDayTimes(String dayTimes) {
        this.dayTimes = dayTimes;
    }

    public String getStartDate() {
        return startDate == null ? "" : startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate == null ? "" : endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAcqcode() {
        return acqcode == null ? "" : acqcode;
    }

    public void setAcqcode(String acqcode) {
        this.acqcode = acqcode;
    }

    public String getPlanTime() {
        return planTime;
    }

    public void setPlanTime(String planTime) {
        this.planTime = planTime;
    }
}
