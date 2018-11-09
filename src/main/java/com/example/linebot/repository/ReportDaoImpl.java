package com.example.linebot.repository;

import com.example.linebot.dao.ReportDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;

@Repository
@Transactional
public class ReportDaoImpl implements ReportDao {

    public static final String URL ="jdbc:postgresql://localhost:5432/";
    public static final String USER_ID = "";
    public static final String PASSWD = "";

    @Override
    public void insert(String type, String category, String detail, String latitude, String longitude)
            throws SQLException {

        String sql = "insert into test_db (type, category, detail, location_lat, location_lng) values (?,?,?,?,?)";

        try ( Connection conn = DriverManager.getConnection(URL, USER_ID, PASSWD) ) {

            try ( PreparedStatement ppst = conn.prepareStatement(sql) ) {

                ppst.setString(1, type);
                ppst.setString(2, category);
                ppst.setString(3, detail);
                ppst.setString(4, latitude);
                ppst.setString(5, longitude);

                ppst.executeUpdate();
            }
        }
    }
}
