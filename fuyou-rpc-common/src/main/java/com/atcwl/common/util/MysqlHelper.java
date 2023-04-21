package com.atcwl.common.util;

import cn.hutool.core.util.StrUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *  Mysql注册中心的工具类
 * @author: WuChengXing
 * @create: 2022-05-04 01:03
 **/
public class MysqlHelper {

    private final String host;
    private final Integer port;
    private final String username;
    private final String password;
    private final String database;

    private Properties properties;

    public MysqlHelper(String host, Integer port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        properties = new Properties();
        properties.put("user", this.username);
        properties.put("password", this.password);
        properties.setProperty("remarks", "true");
        properties.put("useInformationSchema", "true");
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false", properties);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Connection getConnection(String database) {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + database + "?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false", properties);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public List<String> getDatabases() {
        Connection conn = getConnection();
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet catalogs = metaData.getCatalogs();
            List<String> rs = new ArrayList<>();
            while (catalogs.next()) {
                String db = catalogs.getString("TABLE_CAT");
                if (StrUtil.equalsIgnoreCase(db, "information_schema")) {
                    continue;
                }
                rs.add(db);
            }
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * 这里的话是UI界面上面的按钮来触发此操作的，进入此处验证数据库是否连接
     *
     * @return
     */
    public String testDatabase() {
        Connection conn = getConnection();

        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT VERSION() AS MYSQL_VERSION");
            ResultSet resultSet = null;
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString("MYSQL_VERSION");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
        return "Err";
    }
}
