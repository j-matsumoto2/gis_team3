package com.example.linebot.web;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/*
* 送信する画像をリサイズするクラス
* */
public class ResizeImage {

    /*
     *   ちょっと変えたやつ。Ver2.0
     *   少し早くなった、でもまだ遅い
     * */
    public String base64EncodeStrV2(MultipartFile mfile) throws IOException {

        //画像をローカルに保存
        Optional<String> opt;
        opt = makeTmpFile(mfile, ".png");
        String path = opt.orElseGet(()->"ファイル書き込みNG");
        System.out.println("ファイルの保存先->" + path);

        // ファイルインスタンスを取得し、ImageIOへ読み込ませる
        File f = new File(path);
        BufferedImage image = ImageIO.read(f);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        image.flush();

        // リサイズしちゃって
        // 読み終わった画像をバイト出力へ
        ImageIO.write(resizeImageV2(image), "png", bos);
        bos.flush();
        bos.close();

        // バイト配列→BASE64へ変換する
        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

        return base64Image;
    }

    // MultipartFileのいんぷっとストリームを、拡張子を指定してファイルに書き込む
    // また保存先のファイルパスをOptional型で返す
    private Optional<String> makeTmpFile(MultipartFile multipartFile, String extention) {

        try(InputStream is = multipartFile.getInputStream()){
            Path tmpFilePath = Files.createTempFile("liff", extention);
            Files.copy(is, tmpFilePath,REPLACE_EXISTING);
            return Optional.ofNullable(tmpFilePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    /*
     *   成功したやつ
     * */
    private BufferedImage resizeImageV2(BufferedImage image) {

        //初期の値保存
        int targetWidth = image.getWidth();
        int targetHeight = image.getHeight();

        //リサイズ
        float width = 400;
        float height = targetHeight * (width / targetWidth);

        BufferedImage resizedImg = new BufferedImage( (int)width, (int)height, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, (int)width, (int)height, null);
        g2.dispose();

        return resizedImg;
    }

}
