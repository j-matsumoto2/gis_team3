package com.example.linebot.web;

import com.example.linebot.dao.ReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@EnableCaching // cache使うため
@Controller
public class LIFFController {

    @Autowired
    ReportDao reportDao;

    String flag = "false";

    @GetMapping("/liff")
    public String hello(Model model) {
        //Project route
        String dir = System.getProperty("user.dir");
        System.out.println("ルート：" + dir);

//        System.out.println("LIFF起動時 flag -> " + flag);

        model.addAttribute("test", "報告フォーム");
        model.addAttribute("flag", flag);
        //ここでflag==trueのときfalseに戻す？
        if( flag.equals("true") ) {
            // ここでaddAttributeしたあとにflagを戻す文追加
            flag = "false";
        }
//        System.out.println("LIFF起動後 flag -> " + flag);

        return "liff";
    }


    /*
     * Spring cacheの値を更新する
     * */
    @CachePut(value = "liffCache", key = "#lineid")
    public void putCache(String lineid, String tf) {
        System.out.println("put");
        flag = tf;
//        System.out.println("flag -> " + flag);
    }

    /*testページに遷移*/
    @RequestMapping(value = "result", params = "testlink", method = RequestMethod.POST)
    public String testpage() {
        return "android_test";
    }

}