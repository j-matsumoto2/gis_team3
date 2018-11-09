package com.example.linebot.line;

import com.example.linebot.Report;
import com.example.linebot.web.LIFFController;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.postback.PostbackContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@LineMessageHandler
public class CallbackV2 {

    Report report  = new Report();

    // Spring cache用
    @Autowired
    LIFFController controller;

    @Autowired
    private LineMessagingClient lineMessagingClient;

    // 返答メッセージを作る
    private TextMessage reply(String text) {
        return new TextMessage(text);
    }

    // 確認フォームテンプレ(非対応メッセージ用)
    public ConfirmTemplate confirmTemplateM1(String text) {
        Action left = new URIAction("はい", "line://app/1596332300-P5e9377Y");
        Action right = new PostbackAction("いいえ","MN");
        return new ConfirmTemplate(text,left,right);
    }

    // 確認フォームテンプレ(LIFF対応用)
    public ConfirmTemplate confirmTemplateLIFF(String text) {
        //Action left = new PostbackAction("はい","LY");
        Action left = new URIAction("はい", "line://app/1596332300-P5e9377Y");
        Action right = new PostbackAction("いいえ","LN");
        return new ConfirmTemplate(text,left,right);
    }

    // 普通のメッセージに対するイベント
    @EventMapping
    public Message handleMessage(MessageEvent<TextMessageContent> event) {

        TextMessageContent tmc = event.getMessage();
        String text = tmc.getText();

        //LIFFの報告の場合
        if (text.startsWith("種別：")) {

            Substring substring = new Substring();
            ArrayList<String> arrayList = substring.getString(text);

            for (String s : arrayList) {
                System.out.println(s);
            }

            try {
                // 緯度経度を分割
                ArrayList<String> latlng = substring.getLatLng(arrayList.get(3));
                // ほかクラスに保存
                report.setType(arrayList.get(0));
                report.setCategory(arrayList.get(1));
                report.setDetail(arrayList.get(2));

                report.setLatitude(latlng.get(0));
                report.setLongitude(latlng.get(1));

                System.out.println(report.getType()+"\n"
                        +report.getCategory()+"\n"
                        +report.getDetail()+"\n"
                        +report.getLatitude()+"\n"
                        +report.getLongitude()
                );

            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            return new TemplateMessage("内容を修正しますか", confirmTemplateLIFF(text+"\n内容を修正しますか？"));

        } else {
            String string = "対応していないメッセージです。\n報告フォームを表示しますか？";
            return new TemplateMessage(string, confirmTemplateM1(string));
        }
    }


    // YES・NOに対応するイベント
    @EventMapping
    public Message handlePostback(PostbackEvent event) {

        // Postback
        PostbackContent pbc = event.getPostbackContent();

        // ボタンで選んだやつを取ってくる
        String data = pbc.getData();
        // UserId
        String userId = event.getSource().getUserId();

        // 確認フォームのボタンに対するアクション
        if("MN".equals(data)) {
            return reply("画面下の「報告フォーム」から報告ができます。");

        } else if("LN".equals(data)) {
            // 送信完了時にLIFFのCookieを削除する?
            // 消す=="true"  消さない=="false"
            // すでにtrueだったらfalseに変更？もしくはCache削除
            controller.putCache(userId, "true");

            return reply("報告を送信しました。（仮）\nありがとうございます。");
        } else {

            return reply("どうすることもできんゾ : data -> " + data);

        }
    }
}
