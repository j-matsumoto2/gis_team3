package com.example.linebot.others;


/*
 * 設計ミスをどうにかするためのクラス
 * 文字列で受け取ってしまった「種別」「内容」をIDに変換する
 * */
public class ConvertId {

    //
    // ArrayList<String> templateArray = new ArrayList<>();

    //
    private int c_flag;

    public ConvertId() {
        c_flag = 0;
//        templateArray.add(1, "道路に穴が開いています");
//        templateArray.add(2, "道路がへこんでいて振動します");
//        templateArray.add(3, "段差があり走りづらいです");
//        templateArray.add(4, "歩道がでこぼこしていて歩きづらいです");
//        templateArray.add(5, "その他");
//        templateArray.add(6, "");
//        templateArray.add(7, "");
//        templateArray.add(8, "");
//        templateArray.add(9, "");
//        templateArray.add(10, "");
//        templateArray.add(11, "");
//        templateArray.add(12, "");
//        templateArray.add(13, "");
//        templateArray.add(14, "");
//        templateArray.add(15, "");
//        templateArray.add(16, "");
//        templateArray.add(17, "");
//        templateArray.add(18, "");
//        templateArray.add(19, "");
//        templateArray.add(20, "");
//        templateArray.add(21, "");
//        templateArray.add(22, "");
//        templateArray.add(23, "");
//        templateArray.add(24, "");
//        templateArray.add(25, "");
//        templateArray.add(26, "");
//        templateArray.add(27, "");
//        templateArray.add(28, "");
//        templateArray.add(29, "");
//        templateArray.add(30, "");
//        templateArray.add(31, "");
//        templateArray.add(32, "");
//        templateArray.add(33, "");
//        templateArray.add(34, "");
//        templateArray.add(35, "");
//        templateArray.add(36, "");
    }

    // 種別8種類
    public int convertGenre(String genre) {
        int genreId;

        if( genre.equals("舗装")) {
            genreId = 1;
            c_flag = 1;
        } else if ( genre.equals("照明灯") ) {
            genreId = 2;
            c_flag = 2;
        } else if ( genre.equals("道路付属物") ) {
            genreId = 3;
            c_flag = 3;
        } else if ( genre.equals("雨水・排水") ) {
            genreId = 4;
            c_flag = 4;
        } else if ( genre.equals("小動物の死骸") ) {
            genreId = 5;
            c_flag = 5;
        } else if ( genre.equals("樹木・雑草") ) {
            genreId = 6;
            c_flag = 6;
        } else if ( genre.equals("除雪") ) {
            genreId = 7;
            c_flag = 7;
        } else if ( genre.equals("その他") ) {
            genreId = 8;
            c_flag = 8;
        } else {
            // 例外
            genreId = 9;
            c_flag = 36;
        }

        return genreId;
    }

    // 内容36種類
    public int convertTmpl(String template) {

        if (c_flag == 1) {
            // 種別＝舗装
            if (template.equals("道路に穴が開いています")) {
                return 1;
            } else if (template.equals("道路がへこんでいて振動します")) {
                return 2;
            } else if (template.equals("段差があり走りづらいです")) {
                return 3;
            } else if (template.equals("歩道がでこぼこしていて歩きづらいです")) {
                return 4;
            } else {
                return 5;
            }
        } else if (c_flag == 2) {
            // 照明灯
            if (template.equals("照明灯が消えています")) {
                return 6;
            } else if (template.equals("照明灯に穴が開いています")) {
                return 7;
            } else if (template.equals("照明灯が錆ついています")) {
                return 8;
            } else if (template.equals("照明灯が傾いています")) {
                return 9;
            } else {
                return 10;
            }
        } else if (c_flag == 3) {
            // 道路付属物
            if (template.equals("ガードパイプが曲がっています")) {
                return 11;
            } else if (template.equals("標識が曲がっています")) {
                return 12;
            } else if (template.equals("標識が傾いています")) {
                return 13;
            } else if (template.equals("標識が見えづらくなっています")) {
                return 14;
            } else if (template.equals("縁石が壊れています")) {
                return 15;
            } else {
                return 16;
            }
        } else if (c_flag == 4) {
            // 雨水・排水
            if (template.equals("雨水が排水されません")) {
                return 17;
            } else if (template.equals("雨水枡の周りが壊れています")) {
                return 18;
            } else if (template.equals("いつも水が溜まっています")) {
                return 19;
            } else if (template.equals("マンホールの周りが壊れています")) {
                return 20;
            } else {
                return 21;
            }
        } else if (c_flag == 5) {
            // 小動物
            if (template.equals("動物が車に轢かれています")) {
                return 22;
            } else if (template.equals("鳥が死んでいます")) {
                return 23;
            } else {
                return 24;
            }
        } else if (c_flag == 6) {
            // 樹木・雑草
            if (template.equals("雑草が伸びているので確認してください")) {
                return 25;
            } else if (template.equals("街路樹の枝が伸びているので確認してください")) {
                return 26;
            } else if (template.equals("樹木が伸びて交差点が見えづらくなっています")) {
                return 27;
            } else if (template.equals("樹木が枯れています")) {
                return 28;
            } else {
                return 29;
            }
        } else if (c_flag == 7) {
            // 除雪
            if (template.equals("雪山があって見通しが悪い")) {
                return 30;
            } else if (template.equals("道路が凸凹で走りづらい")) {
                return 31;
            } else if (template.equals("歩道が歩きづらい")) {
                return 32;
            } else if (template.equals("砂箱に砂を補充してほしい")) {
                return 33;
            } else {
                return 34;
            }
        } else {
            // その他
            if (template.equals("その他")) {
                return 35;
            } else {
                return 36;
            }
        }
    }
}