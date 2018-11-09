package com.example.linebot;

public class Report {

    /*
    * 値を保存しておくで
    * これをDBに入れるで
    * */
    private String lineId; //LINEid
    private String type;   //種別
    private String category;   //内容
    private String detail =""; //詳細
    private String latitude ="";
    private String longitude ="";


    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLineId() { return this.lineId; }

    public String getType(){ return this.type; }

    public String getCategory(){ return this.category; }

    public String getDetail(){ return this.detail; }

    public String getLatitude(){ return this.latitude; }

    public String getLongitude(){ return this.longitude; }

}
