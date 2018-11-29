package com.example.linebot.dao;

import com.example.linebot.dao.ReportDaoImpl;
import com.example.linebot.web.ReportBean;
import com.google.inject.ImplementedBy;

import java.sql.SQLException;

/*
 * 現行システムの " IContributionDAO.java" にあたるクラス
 * */
@ImplementedBy(ReportDaoImpl.class)
public interface ReportDao {

    // テスト用
    //登録(画像なし)//void insert(int type, int category, String detail, String latitude, String longitude) throws SQLException;

    /**
     * 引数をもとにDBに画像のパスを保存s
     * */
    boolean insertContributionImage(ReportBean report);

    /**
     * 引数をcontributionテーブルにinsertする
     */
    boolean insertContribution(ReportBean report);

    /**
     * 引数をもとにline_idが存在するか確認する
     * */
    boolean existLineAccount(String line_id);

    /**
     * 引数をもとにline_idを追加する
     * */
    boolean registerLine(int Id, String line_id);

    /**
     * 引数をもとにLINEユーザのaccount_idを取得
     * */
    boolean insertAccountId(int Id, int role);

    /**
     * 存在するaccount_idの最大値を返す
     * */
    int getMaxId();
}