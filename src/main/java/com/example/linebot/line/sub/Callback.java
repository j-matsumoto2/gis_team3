package com.example.linebot.line.sub;

import com.example.linebot.line.ContentColumn;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.postback.PostbackContent;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.spring.boot.annotation.EventMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

//@LineMessageHandler
public class Callback{

    private static final String CHANNEL_SECRET ="取得したものをいれる";
    private static final String CHANNEL_ACCESS_TOKEN ="取得したものをいれる";

    @Autowired
    private LineMessagingClient lineMessagingClient;    //画像の取得のため
    private String ImagePath;   //受信した画像をコンテンツのサムネにする

    private static final Logger log = LoggerFactory.getLogger(Callback.class);

    private List<String> reportFlag;  //必ず「報告」という単語から報告が始まるように前の単語を管理する
    List<String> reportList;          //報告の内容を一時的に保存する

    public Callback(LineMessagingClient client) {
        this.lineMessagingClient = client;
    }

    // フォローイベントに対応する
    @EventMapping
    public TextMessage handleFollow(FollowEvent event) {
        // 実際の開発ではユーザIDを返信せず、フォロワーのユーザIDをデータベースに格納しておくなど
        String userId = event.getSource().getUserId();
        return reply("あなたのユーザIDは " + userId);
    }

    // 返答メッセージを作る
    private TextMessage reply(String text) {
        return new TextMessage(text);
    }


    //会話で報告をすすめる(LIFFは考えない)
    @EventMapping
    public Message handleMessage(MessageEvent<TextMessageContent> event) {
        TextMessageContent tmc = event.getMessage();
        String text = tmc.getText();

        switch (text) {
            case "報告":
                return reply("報告は、位置情報・種別・内容・詳細の4つを入力してください。（中断は「キャンセル」を入力）" + "\n1.位置情報を入力してください");
            case "キャンセル":
                return reply("報告を中断します");
            case "liff":
                //LIFFIDいちいち投げるのだるいのでこっちから返す
                return reply("LIFF -> " + "line://app/*****");
            default:
                if(text.isEmpty()) {
                    return reply("？");
                } else {
                    return reply(text);
                }
        }
    }

    //位置情報を受け取ったときのイベント
    //@EventMapping
    public Message handleLocationMessage(MessageEvent<LocationMessageContent> event) {
        LocationMessageContent lmc = event.getMessage();
        double lat = lmc.getLatitude();     //
        double lon = lmc.getLongitude();    //
        String location = lmc.getAddress(); //住所名

        return reply("位置情報->\n住所: " + location + "\n緯度: " + lat + "\n経度: " + lon + "\nで報告します\n\n2.報告の種別を選択してください");
        //return handleTextMessageEvent(event);   //種別の選択肢を送信したい
    }

    //確認フォームテンプレ
    public ConfirmTemplate confirmTemplate(String text) {
        Action left = new PostbackAction("はい","y");
        Action right = new PostbackAction("いいえ","n");
        return new ConfirmTemplate(text,left,right);
    }

    //ボタンテンプレートの設計的な部分(種別返信用ボタン)
    @EventMapping
    public Message handleCategoryMessageEvent(MessageEvent<LocationMessageContent> event) {

        //ボタンに必要ぽいサムネイル画像
        String thumbnailImageUrl = "https://puu.sh/BbfNS/c2fa6e5411.jpg";
        //種別の選択肢
        Action a = new PostbackAction("舗装", "hosou");
        Action b = new PostbackAction("除雪", "josetu");
        Action c = new PostbackAction("その他", "sonota");
        //種別のリスト
        List<Action> actions = Arrays.asList(a, b, c);
        //ユーザーに選択させるときのボタンテンプレ
        ButtonsTemplate bt = new ButtonsTemplate(thumbnailImageUrl,"種別選択", "ひとつ選んで", actions);

        String altTitle = "種別選択";
        //return new ReplyMessage(event.getReplyToken(), new TemplateMessage(altTitle, bt));
        return new TemplateMessage(altTitle, bt);
    }
    //↑の画像をおくられたときばーじょん
    public Message handleCategoryPostBack(MessageEvent<ImageMessageContent> event) {

        //下の方にあるパス取得のためのメソッドを一回呼び出す
        handleImageMessageEvent(event);

        //送信された画像をそのままサムネイルにする
        String thumbnailImageUrl = ImagePath;
        Action a = new PostbackAction("舗装", "hosou");
        Action b = new PostbackAction("除雪", "josetu");
        Action c = new PostbackAction("その他", "sonota");
        //種別のリスト
        List<Action> actions = Arrays.asList(a, b, c);
        //ユーザーに選択させるときのボタンテンプレ
        ButtonsTemplate bt = new ButtonsTemplate(thumbnailImageUrl,"種別選択", "ひとつ選んで", actions);

        String altTitle = "種別選択";
        //return new ReplyMessage(event.getReplyToken(), new TemplateMessage(altTitle, bt));

        return new TemplateMessage(altTitle, bt);
    }
    //↑の選択に応じて返答する
    @EventMapping
    public Message handleCategoryPostback(PostbackEvent event) {

        ContentColumn cc = new ContentColumn();
        PostbackContent pbc = event.getPostbackContent();

        //ボタンで選んだやつを取ってくる
        String data = pbc.getData();

        final String altText;
        final CarouselTemplate template;
        final String text;

        //選んだもので分岐して内容選択させる(種別)
        if("hosou".equals(data)) {
            altText = "「舗装」の内容を選んで";
            template = cc.handleContentMessageEvent1();
            return new TemplateMessage(altText,template);
        } else if("josetu".equals(data)) {
            altText = "「除雪」の内容を選んで";
            template = cc.handleContentMessageEvent2();
            return new TemplateMessage(altText,template);
        } else if("sonota".equals(data)) {
            altText = "「その他」の内容を選んで";
            template = cc.handleContentMessageEvent99();
            return new TemplateMessage(altText,template);
        }

        //選んだもので分岐させて内容選択させる(内容)
        if("hole".equals(data)) {
            text = "「道路に穴が空いている」で報告します。\n\n詳細を記入しますか？";   //ここで、詳細記入に対してYES/NOの回答を求める
            return new TemplateMessage(text,confirmTemplate(text));
        } else if("other".equals(data)) {
            text = "「その他」で報告します。\n詳細を記入してください";  //その他だと詳細記入は必須にしてY/Nを通さず入力させる
            return reply(text);
        }

        //確認フォームのボタンに対するアクション
        if("y".equals(data)) {
            return reply("詳細記入フォームは考え中だから待って(終了)");
        } else if("n".equals(data)) {
            return reply("報告を送信しました。\nありがとうございます。");
        } else {
            return reply("どうすることもできんゾ : data -> " + data);
        }

    }

    //写真（画像）を受け取ったとき(未完:画像を取得できるが、https://で始まるURLにしなければテンプレには貼れない)
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
        ImagePath = path;
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


    public String getChannelSecret() {
        return CHANNEL_SECRET;
    }

    public String getChannelAccessToken() {
        return CHANNEL_ACCESS_TOKEN;
    }
}
