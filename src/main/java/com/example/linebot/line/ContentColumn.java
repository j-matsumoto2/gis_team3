package com.example.linebot.line;

import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.postback.PostbackContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.spring.boot.annotation.EventMapping;

import java.util.Arrays;
import java.util.List;
/*
* 種別選択で選ばれたものに対応する報告内容のカルーセルカラムたち
*
* */

public class ContentColumn {

    public String originalContentUrl;   //送られた画像のURL

    private Message reply(String text) {
        return new TextMessage(text);
    }

    //舗装1
    public CarouselColumn carouselColumn1() {

        String title = "報告内容選択";
        String text = "種別「舗装」での内容を選択して";

        Action a = new PostbackAction("道路に穴","hole");
        Action b = new PostbackAction("陥没","subsidence");
        Action c = new PostbackAction("爆発","explosion");

        List<Action> actions = Arrays.asList(a, b, c);

        return new CarouselColumn("https://puu.sh/BbfNS/c2fa6e5411.jpg",title,text,actions);
    }
    //舗装2
    public CarouselColumn carouselColumn2() {

        String title = "報告内容選択";
        String text = "種別「舗装」での内容を選択して";

        Action a = new PostbackAction("見通しが悪い","unclear");
        Action b = new PostbackAction("歩道がない","walk");
        Action c = new PostbackAction("その他","other");

        List<Action> actions = Arrays.asList(a, b, c);

        return new CarouselColumn("https://puu.sh/BbfNS/c2fa6e5411.jpg",title,text,actions);
    }




    //種別->舗装のときのやつ
    public CarouselTemplate handleContentMessageEvent1() {
        List<CarouselColumn> columns = Arrays.asList(carouselColumn1(),carouselColumn2());  //カラムのリスト化
        CarouselTemplate ct = new CarouselTemplate(columns);
        return ct;
    }

    //種別->除雪のときのやつ
    public CarouselTemplate handleContentMessageEvent2() {
        List<CarouselColumn> columns = Arrays.asList();     //未完->ContentColumnにメソッドを作ってカラムを追加
        CarouselTemplate ct = new CarouselTemplate(columns);
        return ct;
    }

    //種別->その他のときのやつ
    public CarouselTemplate handleContentMessageEvent99() {
        List<CarouselColumn> columns = Arrays.asList();     //未完->ContentColumnにメソッドを作ってカラムを追加
        CarouselTemplate ct = new CarouselTemplate(columns);
        return ct;
    }

}
