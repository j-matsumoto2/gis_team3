package com.example.linebot.line;


import com.example.linebot.Bean.ReportBean;
import com.example.linebot.controller.LIFFController;
import com.example.linebot.dao.IReportDao;
import com.example.linebot.line.sub.Callback;
import com.example.linebot.others.ConvertId;
import com.example.linebot.others.Substring;
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
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@LineMessageHandler
public class CallbackV3 {

    //ボタンテンプレのボタンを二回押さないようにするflag
    private int flag2;

    @Autowired
    IReportDao reportDao;

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

    // 画像の保存に使う
    private static String messageId;

    // 確認フォームテンプレ(非対応メッセージ用)
    public ConfirmTemplate confirmTemplateM1(String text) {
        Action left = new URIAction("はい", "line://app/1619229116-6eGmdl7z");
        Action right = new PostbackAction("いいえ","MN");
        return new ConfirmTemplate(text,left,right);
    }

    // 確認フォームテンプレ(LIFF対応用)
    public ConfirmTemplate confirmTemplateLIFF(String text) {
        //Action left = new PostbackAction("はい","LY");
        //LIFFの修正確認をロード時に flag2=1にする
        flag2 = 1;
        Action left = new URIAction("はい", "line://app/1619229116-6eGmdl7z");
        Action right = new PostbackAction("いいえ","LN");
        return new ConfirmTemplate(text,left,right);
    }

    // 確認フォームテンプレ(imageContent返答)
    public ConfirmTemplate confirmTemplateImage(String text) {
        //Action left = new PostbackAction("はい","IY");
        Action left = new PostbackAction("はい", "IY");
        Action right = new PostbackAction("いいえ","IN");
        return new ConfirmTemplate(text,left,right);
    }

    // 画像を保存したあとにLIFFを起動するためのテンプレ
    public Message goLiff() {
        //ボタンに必要ぽいサムネイル画像
        String thumbnailImageUrlCavet = "https://puu.sh/BbfNS/c2fa6e5411.jpg";
        String thumbnailImageUrlSiokawa = "https://puu.sh/C1nXK/5a48f5c50b.jpg";
        //種別の選択肢
        Action a = new URIAction("ENTER", "line://app/1619229116-6eGmdl7z");
        //種別のリスト
        List<Action> actions = Arrays.asList(a);
        //ユーザーに選択させるときのボタンテンプレ
        ButtonsTemplate bt = new ButtonsTemplate(thumbnailImageUrlSiokawa,"報告フォームを起動", "報告して", actions);

        String altTitle = "報告フォームはこちら";
        //return new ReplyMessage(event.getReplyToken(), new TemplateMessage(altTitle, bt));
        return new TemplateMessage(altTitle, bt);
    }

    // 普通のメッセージに対するイベント
    @EventMapping
    public Message handleMessage(MessageEvent<TextMessageContent> event) {

        // Converter
        ConvertId convertId = new ConvertId();

        TextMessageContent tmc = event.getMessage();
        String text = tmc.getText();

        //LIFFの報告の場合
        if (text.startsWith("種別：")) {

            Substring substring = new Substring();
            ArrayList<String> arrayList;

            try {
                // 受け取った文字列の分割
                arrayList = substring.getString(text);
            } catch (ArrayIndexOutOfBoundsException e) {
                return reply("さてはオメー、タダ者じゃねえな？");
            }

            // 開発用：ArrayListの中身表示
            for (String s : arrayList) {
                System.out.println(s);
            }

            try {
                // 緯度経度を分割
                ArrayList<String> latlng = substring.getLatLng(arrayList.get(3));
                // Report(DBに保存する値の格納クラス)に値を渡す
                ReportBean reportBean = controller.getReport(event.getSource().getUserId());
                reportBean.setType(convertId.convertGenre(arrayList.get(0)));   // 文字列を対応するID(int)に変換
                reportBean.setCategory(convertId.convertTmpl(arrayList.get(1))); // 文字列を対応するID(int)に変換
                reportBean.setDetail(arrayList.get(2));
                reportBean.setLatitude(latlng.get(0));
                reportBean.setLongitude(latlng.get(1));
                // LINEidを取得
                reportBean.setLineId(event.getSource().getUserId());
                // キャッシュ更新
                controller.putReport(event.getSource().getUserId(), reportBean);

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
            // DBにぶちこみ
            if (flag2 == 1) {
                controller.putCache(userId, "true");
                // DBにぶちこみ
                // select文でLINEidの確認
                if (!reportDao.existLineAccount(userId)) {
                    // DB内のidの最大値
                    int num = reportDao.getMaxId();
                    // DBに userId を insert
                    reportDao.insertAccountId(num, 2);
                    reportDao.registerLine(num, userId);
                }
                // DBに報告を送信（ここで画像なしだとNULL->ReportBeanがnewされてないからだと思う）
                reportDao.insertContribution(controller.getReport(userId));
                // PathをDBに送信
                ReportBean reportBean = controller.getReport(userId);
//                System.out.println(reportBean.getImagePath());
                if (!reportBean.getImagePath().equals("")) reportDao.insertContributionImage(controller.getReport(userId));
                // キャッシュの削除
                controller.evictReport(userId);
                //flag2を0にする　以降、報告内容のボタンテンプレメッセージが送られない限り何もしない
                flag2 = 0;
                return reply("報告を送信しました。（仮）\nありがとうございます。");
            } else {
                //何もしない 警告出るけど問題ない(はず)
                return reply("送信済みなんだから\nそういうことするのやめなさいよあんた");
            }

        } else if("IY".equals(data)) {
            // 画像を保存してLIFF起動
            getImageContent(event);
            // 入力フォームのテンプレを返す
            return goLiff();
        } else if("IN".equals(data)) {
            // 画像を保存せずになんかメッセージ
            return reply("画像を送信するか、下の「報告フォーム」から報告できます。");
        } else {
            return reply("どうすることもできんゾ : data -> " + data);
        }
    }

    // 画像メッセージに対応するイベント
    @EventMapping
    public Message handleImageMessageEvent(MessageEvent<ImageMessageContent> event) {

        ImageMessageContent imc = event.getMessage();
        this.messageId = imc.getId();

//        return reply("画像を受信し、次のパスで保存しました\nPath -> " + path);
        String string = "画像を送信して報告する？";
        return new TemplateMessage(string, confirmTemplateImage(string));
    }

    // 「画像を送信して報告」を IY で答えたときの処理
    private void getImageContent(PostbackEvent event) {

        Optional<String> opt = Optional.empty();

        try {
            //画像メッセージのmessageIdでMessageContentResponseを取得
            MessageContentResponse response = lineMessagingClient.getMessageContent(this.messageId).get();
            log.info("get content{}:", response);
            // MessageContentResponse からファイルをローカルに保存する
            // ※LINEでは、どの解像度で写真を送っても、サーバ側でjpgファイルに変換される
            opt = makeTmpFile(response, ".jpg", event);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // ファイルが保存できたことが確認できるように、ローカルのファイルパスをコールバックする
        // 運用ではファイルパスを表示することは避けましょう
        String path = opt.orElseGet(() -> "ファイル書き込みNG");
    }

    // MessageContentResponse野中のバイト入力ストリームを、拡張子を指定してファイルに書き込む
    // また保存先のファイルパスをOptional型で返す
    private Optional<String> makeTmpFile(MessageContentResponse resp, String extention, PostbackEvent event) {

        existDir();
        try(InputStream is = resp.getStream()){
            Path tmpFilePath = Files.createTempFile(Paths.get("C:/linebot-image"),"linebot", extention);
            Files.copy(is, tmpFilePath,REPLACE_EXISTING);

            // 保存したらCacheを作成
            controller.cacheReport(event.getSource().getUserId(), tmpFilePath.toString());

            return Optional.ofNullable(tmpFilePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // 画像保存ディレクトリの存在チェック
    private void existDir() {
        File file = new File("c:\\linebot-image");
        if (file.exists()) {
            System.out.println("ファイルは存在します。");
        } else {
            System.out.println("ファイルは存在しません。");
            file.mkdir();
            System.out.println("mkdir -> " + file.getPath());
        }
    }
}




