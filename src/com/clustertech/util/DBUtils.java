package com.clustertech.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mysql.jdbc.PreparedStatement;

public final class DBUtils {
    
    private static final Logger logger = Logger.getLogger(DBUtils.class);
    private static DBUtils instance = null;
    private Connection connection = null;
    private String sql = "replace into RDP_USER values (?, ?)";

    private DBUtils() {
        try {
            connectDB();
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public static DBUtils getInstance() {
        if (instance == null) {
            synchronized (DBUtils.class) {
                if (instance == null) {
                    instance = new DBUtils();
                }
            }
        }
        return instance;
    }

    static {
        try {
            String driver = DBConf.getInstance().getDBDriver();
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.error(e);
        }
    }

    private void connectDB() throws SQLException {
        String encPassword = DBConf.getInstance().getCloudDBPasswd();
        String decPassword = DesEncrypter.getInstance().decrypt(encPassword);
        connection = DriverManager.getConnection(DBConf.getInstance().getCloudDBUrl(),
                    DesEncrypter.getInstance().decrypt(DBConf.getInstance().getCloudDBUser()),
                    decPassword);
        logger.debug("connect to DB...");
//        connection = DriverManager.getConnection(DBConf.getInstance().getCloudDBUrl(),DBConf.getInstance().getCloudDBUser(),DBConf.getInstance().getCloudDBPasswd());
    }

    public void insertDB(String userName, String password) {
        logger.debug("insert user into db..." + userName + " " + password);
        try {
            PreparedStatement stmt = (PreparedStatement) connection.prepareStatement(sql);
            stmt.setString(1, userName);
            stmt.setString(2, password);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            logger.error(e);
        }
    }
}
