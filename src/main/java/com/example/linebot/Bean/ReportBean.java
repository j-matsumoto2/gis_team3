package com.example.linebot.Bean;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ReportBean implements Serializable {

    /*
     * 値を保存しておくで
     * これをDBに入れるで
     * */
    private int contribution_id;
    private String lineId; //LINEid
    private int type;   //種別
    private int category;   //内容
    private String detail; //詳細
    private double latitude;
    private double longitude;
    private String accountId;
    private String imagePath;
    public String flag;

    public ReportBean(String userId, String imagePath) {
        this.lineId = userId;
        this.type = 0;
        this.category = 0;
        this.detail = "";
        this.latitude = 0L;
        this.longitude = 0L;
        this.imagePath = imagePath;
    }


    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setLatitude(String latitude) { this.latitude = Double.parseDouble(latitude); }

    public void setLongitude(String longitude) {
        this.longitude = Double.parseDouble(longitude);
    }

    public void setFlag(String flag) { this.flag = flag; }

    public void setAccountId(String accountId) { this.accountId = accountId; }

    public void setImagePath(String path) { this.imagePath = path; }

    public void setContribution_id(int id){ this.contribution_id = id; }

    public String getLineId() { return this.lineId; }

    public int getType(){ return this.type; }

    public int getCategory(){ return this.category; }

    public String getDetail(){ return this.detail; }

    public double getLatitude(){ return this.latitude; }

    public double getLongitude(){ return this.longitude; }

    public String getFlag() { return this.flag; }

    public String getAccountId() { return this.accountId; }

    public String getImagePath() { return this.imagePath; }

    public int getContribution_id() { return this.contribution_id; }

    /**
     * civic-mapのimportができないので代替
     * 本家と違って変数を用意していない
     * */
    public LocalDateTime getPostTime() { return LocalDateTime.now(); }

    public LocalDateTime getLastUpdateTime() { return LocalDateTime.now(); }

}