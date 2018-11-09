package com.example.linebot.web;

import com.example.linebot.dao.ReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.SQLException;

@EnableCaching // cache使うため
@Controller
public class LIFFController {

    @Autowired
    ReportDao reportDao;

    static String flag = "false";

    @GetMapping("/liff")
    public String hello(Model model) {
        //Project route
        String dir = System.getProperty("user.dir");
        System.out.println("ルート：" + dir);

        model.addAttribute("test", "報告フォーム");
        model.addAttribute("flag", flag);
        System.out.println("flag -> " + flag);
//        model.addAttribute("c_type", cache.getC_Type());
//        model.addAttribute("c_category", cache.getC_Category());
//        model.addAttribute("c_detail", cache.getC_Detail());
        return "liff";
    }

    /*
    * Spring cacheで種別・内容・詳細を保存する
    * key -> LINE_id
    * */
    @Cacheable(value="liffCache", key="#lineid")
    public void setCache(String lineid, String tf) {
        System.out.println("insert");
    }

    /*
    * Spring cacheの値を更新する
    * */
    @CachePut(value = "liffCache", key = "#lineid")
    public void putCache(String lineid, String tf) {
        System.out.println("put");
        flag = tf;
        System.out.println("flag -> " + flag);
    }

    /*
    * LINE_idをkeyとしてSpring cacheを削除
    * */
    @CacheEvict(cacheNames="liffCache", key = "#lineid")
    public void deleteCache(String lineid) {
        System.out.println("delete");
    }


    /*
    * @param type 報告の種別
    * @param cat 報告の内容
    * @param det 詳細記入
    * @param fname 送信するファイル名
    * @param locate 送信する位置情報
    * @param mfile Base64変換するファイルのバイナリデータ?
    *
    * */
    @RequestMapping(value = "/result")
    public String pic(ModelMap modelMap,
                      @RequestParam("type")String type,
                      @RequestParam("category")String cat,
                      @RequestParam("detail")String det,
                      @RequestParam("filename")String fname,
                      @RequestParam("file") MultipartFile mfile,
                      @RequestParam("lat")String lat,
                      @RequestParam("lng")String lng,
                      @RequestParam("lineId")String lineid

    ) throws IOException, SQLException {

        //DBにアクセス(lineIdもここで保存)
        reportDao.insert(type,cat,det,lat,lng);

        modelMap.addAttribute("type",type);
        modelMap.addAttribute("category",cat);
        modelMap.addAttribute("detail",det);
        modelMap.addAttribute("filename",fname);
        modelMap.addAttribute("lat", lat);
        modelMap.addAttribute("lng", lng);
//        modelMap.addAttribute("lineId",lineid);
        System.out.println("LINEid:" + lineid);

        //画像の有無
        boolean exist = false;

        //nullチェック
        if(mfile.isEmpty()) {
            //画像がなかった場合は画像の変換がいらないのでそのままresultに遷移
            return "/result";
        }

        //リサイズのためのインスタンス
        ResizeImage resizeImage = new ResizeImage();

        //base64にした画像データ
        //String base64encodingStr = base64EncodeStr(mfile);
        String base64encodingStr = resizeImage.base64EncodeStrV2(mfile);
        modelMap.addAttribute("base64","data:image/png;base64," + base64encodingStr);

        //画像がNULLじゃないはずなので、trueにして送信
        exist = true;
        modelMap.addAttribute("exist",exist);

        return "/result";
    }


    /*testページに遷移*/
    @RequestMapping(value = "result", params = "testlink", method = RequestMethod.POST)
    public String testpage() {
        return "android_test";
    }
}
