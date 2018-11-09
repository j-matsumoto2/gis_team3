package com.example.linebot.web;


public class UserCache {

    /*
    *  LIFFでの報告を完了時にCookieの削除をおこなう
    *
    * */
    private String t, c, d;

    public  void insertCache(String type, String category, String detail) {
        this.t = type;
        this.c = category;
        this.d = detail;
    }

    public String getC_Type() {
        return this.t;
    }

    public String getC_Category() {
        return this.c;
    }

    public String getC_Detail() {
        return this.d;
    }
}
