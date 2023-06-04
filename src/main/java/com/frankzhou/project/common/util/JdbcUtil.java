package com.frankzhou.project.common.util;

import com.frankzhou.project.common.eunms.DbTypeEnum;
import com.frankzhou.project.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Jdbc连接工具类
 * @date 2023-05-18
 */
@Slf4j
public class JdbcUtil {

    public static Connection getConnection(String dbType,String url,String user,String password) {
        Properties prop = new Properties();
        prop.put("user",user);
        prop.put("password",password);

        Connection conn = null;
        try {
            conn = getConnection(dbType,url,prop);
        } catch (Exception e) {
            log.warn("数据库连接获取失败,dbType:{} url:{} user:{}",dbType,url,prop.get("user"));
            throw new BusinessException("数据库连接获取失败");
        }

        return conn;
    }

    public static Connection getConnection(String dbType, String url, Properties prop) {
        Connection conn = null;
        try {
            Class.forName(DbTypeEnum.getDb(dbType).getDriverClassName());
            conn = DriverManager.getConnection(url, prop);
        } catch (Exception e) {
            log.warn("数据库连接获取失败,dbType:{} url:{} user:{}",dbType,url,prop.get("user"));
            throw new BusinessException("数据库连接获取失败");
        }

        return conn;
    }

    public static List<String> getDatabaseList(Connection conn) {
        List<String> databaseList = new ArrayList<>();
        String sql = "show databases";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                databaseList.add(rs.getString(1));
            }
        } catch (SQLException e) {
            log.warn("执行show databases语句异常");
            return databaseList;
        }

        return databaseList;
    }

    public static List<String> getTableList(Connection conn,String database) {
        List<String> tableList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "use " + database;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                tableList.add(rs.getString(1));
            }
        } catch (SQLException e) {
            log.warn("执行show tables语句异常");
            return tableList;
        }

        return tableList;
    }

    public static List<Map<String,Object>> executeSelect(Connection conn,String sql) {
        return null;
    }

    public static Integer executeCount(Connection conn,String sql) {
        return null;
    }

    public static void releaseResource(Connection conn, PreparedStatement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BusinessException(e.getMessage());
            }
        }

        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                throw new BusinessException(e.getMessage());
            }
        }

        if (conn != null) {
            try {
                conn.close();;
            } catch (SQLException e) {
                throw new BusinessException(e.getMessage());
            }
        }
    }

}
