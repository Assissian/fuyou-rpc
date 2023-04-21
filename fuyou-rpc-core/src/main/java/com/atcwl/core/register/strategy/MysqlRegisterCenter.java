package com.atcwl.core.register.strategy;

import com.atcwl.common.config.SimpleRpcUrl;
import com.atcwl.common.network.HookEntity;
import com.atcwl.common.util.MysqlHelper;
import com.atcwl.core.register.AbstractRegisterCenter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: mysql注册中心
 *
 * @author: WuChengXing
 * @create: 2022-05-14 12:57
 **/
public class MysqlRegisterCenter extends AbstractRegisterCenter {

    private static Connection connection;
    private static MysqlHelper mysqlHelper;
    private static String database;
    private static String table;

    @Override
    public void init(SimpleRpcUrl url) {
        database = url.getDatabase();
        table = url.getTable();
        mysqlHelper = new MysqlHelper(url.getHost(), url.getPort(), url.getUsername(), url.getPassword(), url.getDatabase());
        connection = mysqlHelper.getConnection(database);
    }

    @Override
    protected Boolean buildDataAndSave(String key, String hostPort, String request) {
        return insert(key, hostPort, request);
    }

    @Override
    protected Boolean buildAppName(String appName, String rpcService) {
        return null;
    }

    @Override
    protected Map<String, String> getLoadBalanceData(String key) {
        return getService(key);
    }

    @Override
    public Boolean unregister(HookEntity hookEntity) {
        List<String> rpcServiceNames = hookEntity.getRpcServiceNames();
        String urlName = hookEntity.getServerUrl() + "_" + hookEntity.getServerPort();
        for (String rpcServiceName : rpcServiceNames) {
            delete(rpcServiceName, urlName);
        }
        return true;
    }

    @Override
    protected List<String> getMultiKeyValue(List<String> keys, String machine) {
        return null;
    }

    private Connection getConnection(SimpleRpcUrl url) {
        MysqlHelper mysqlHelper = new MysqlHelper(url.getHost(), url.getPort(), url.getUsername(), url.getPassword(), url.getDatabase());
        return mysqlHelper.getConnection(url.getDatabase());
    }

    private Boolean insert(String key, String hostPort, String value) {
        try {
            String sql = "insert into " + table + " (register_key, host_port, register_value) values('" + key + "','" + hostPort + "','" + value + "'" + ")";
            //SimpleRpcLog.info(sql);
            Statement statement = connection.createStatement();
            return statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            // closeConnection(connection);
        }
    }

    private Boolean update(String key, String value) {
        try {
            String sql = "update " + table + " set register_value = '" + value + "' where register_key ='" + key + "'";
            //SimpleRpcLog.info(sql);
            Statement statement = connection.createStatement();
            return statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            //closeConnection(connection);
        }
    }

    private Boolean delete(String key, String hostPort) {
        try {
            String sql = "delete from " + table + " where register_key ='" + key + "'" + " and host_port ='" + hostPort + "'";
            //SimpleRpcLog.info(sql);
            Statement statement = connection.createStatement();
            return statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            //closeConnection(connection);
        }
    }

    private Map<String, String> getService(String key) {
        Map<String, String> urlMap = new ConcurrentHashMap<>(4);
        try {
            String sql = "select * from " + table + " where register_key = " + "'" + key + "'";
            //SimpleRpcLog.info(sql);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String urlKey = rs.getString("host_port");
                String urlValue = rs.getString("register_value");
                urlMap.put(urlKey, urlValue);
            }
            return urlMap;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            //closeConnection(connection);
        }
    }

    private String getOne(String key, String hostPort) {
        try {
            String sql = "select * from " + table + " where register_key ='" + key + "' and host_port = '" + hostPort + "'";
            //SimpleRpcLog.info(sql);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            String value = "";
            while (rs.next()) {
                value = rs.getString("register_value");
            }
            return value;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            //closeConnection(connection);
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
}
