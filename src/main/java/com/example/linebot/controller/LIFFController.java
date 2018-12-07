package com.example.linebot.controller;


import com.example.linebot.Bean.ReportBean;
import com.example.linebot.dao.IReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@EnableCaching // cache使うため
@Controller
public class LIFFController {

    @Autowired
    IReportDao reportDao;

    @Autowired
    CacheManager cacheManager;

    String flag = "false";

    @GetMapping("/liff")
    public String hello(Model model) {

        String dir = System.getProperty("user.dir");
        System.out.println("ルート：" + dir);

        model.addAttribute("test", "報告フォーム");
        model.addAttribute("flag", flag);

        //ここでflag==trueのときfalseに戻す？
        if( flag.equals("true") ) {
            // ここでaddAttributeしたあとにflagを戻す文追加
            flag = "false";
        }

        return "liff";
    }

    //-------------------------------以下キャッシュ-------------------------------------------

    /**
     * Spring cacheの値を更新する
     * */
    @CachePut(value = "liffCache", key = "#lineid")
    public void putCache(String lineid, String tf) {
        System.out.println("put");
        flag = tf;
//        System.out.println("flag -> " + flag);
    }

    /**
     * ReportBeanクラスをnewしてキャッシュ作成
     * */
    public void cacheReport(final String lineid, String imagePath) {
        Cache cache = cacheManager.getCache("report");
        cache.put(lineid, new ReportBean(lineid, imagePath));
    }

    /**
     * キャッシュの更新
     */
    public void putReport(String lineid, ReportBean reportBean) {
        Cache cache = cacheManager.getCache("report");
        cache.put(lineid, reportBean);
    }

    /**
     * キャッシュの取り出し
     * */
    public ReportBean getReport(String lineid) {
        Cache cache = cacheManager.getCache("report");

        // 画像なし（Null）の場合はキャッシュを作成する
        if (Objects.isNull(cache.get(lineid))) {
            cacheReport(lineid, "");
        }
        return (ReportBean) cache.get(lineid).get();
    }

    /**
     * キャッシュの削除
     * */
    public void evictReport(String lineid) {
        Cache cache = cacheManager.getCache("report");
        cache.evict(lineid);
    }

//    /*testページに遷移*/
//    @RequestMapping(value = "result", params = "testlink", method = RequestMethod.POST)
//    public String testpage() {
//        return "android_test";
//    }

}