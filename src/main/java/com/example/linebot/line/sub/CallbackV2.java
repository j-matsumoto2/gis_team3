package com.example.linebot.line.sub;

import com.example.linebot.Report;
import com.example.linebot.line.Substring;
//import com.example.linebot.web.sub.Report;
import com.example.linebot.line.sub.Callback;
import com.example.linebot.web.LIFFController;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.postback.PostbackContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
/*
 * Version2
 * LIFFを自分で起動 -> 画像の投稿は別個
 *
 * */

//@LineMessageHandler
public class CallbackV2 {

    Report report  = new Report();

    // Spring cache用
    @Autowired
    LIFFController controller;

    @Autowired
    private LineMessagingClient lineMessagingClient;

    // 画像用
    private static final Logger log = LoggerFactory.getLogger(Callback.class);

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

    // 画像メッセージに対応するイベント
    @EventMapping
    public Message handleImageMessageEvent(MessageEvent<ImageMessageContent> event) {

        ImageMessageContent imc = event.getMessage();
        String messageId = imc.getId();
        Optional<String> opt = Optional.empty();

        try {
            //画像メッセージのmessageIdでMessageContentResponseを取得
            MessageContentResponse response = lineMessagingClient.getMessageContent(messageId).get();
            log.info("get content{}:", response);
            // MessageContentResponse からファイルをローカルに保存する
            // ※LINEでは、どの解像度で写真を送っても、サーバ側でjpgファイルに変換される
            opt = makeTmpFile(response, ".jpg");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // ファイルが保存できたことが確認できるように、ローカルのファイルパスをコールバックする
        // 運用ではファイルパスを表示することは避けましょう
        String path = opt.orElseGet(() -> "ファイル書き込みNG");

        return reply("画像を受信し、次のパスで保存しました\nPath -> " + path);
    }

    // MessageContentResponse野中のバイト入力ストリームを、拡張子を指定してファイルに書き込む
    // また保存先のファイルパスをOptional型で返す
    private Optional<String> makeTmpFile(MessageContentResponse resp, String extention) {

        try(InputStream is = resp.getStream()){
            Path tmpFilePath = Files.createTempFile("linebot", extention);
            Files.copy(is, tmpFilePath,REPLACE_EXISTING);
            return Optional.ofNullable(tmpFilePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}