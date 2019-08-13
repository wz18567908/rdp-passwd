package com.clustertech.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class DBConf {

    private static DBConf instance = null;
    private static final Logger logger = Logger.getLogger(DBConf.class);
    private static final Properties properties = new Properties();

    public DBConf() {
        String path = System.getProperty("CRYPT_TOP");
        logger.debug("CRYPT_TOP: " + path);
        String confFile = path + File.separator + "conf" + File.separator + "jdbc.properties";
        logger.debug("DB confFile: " + confFile);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(confFile);
            properties.load(inputStream);
            trimSpace(properties);
            inputStream.close();
        } catch (IOException e) {
            logger.error(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
    }
    
    public static DBConf getInstance() {
        if (instance == null) {
            synchronized (DBConf.class) {
                if (instance == null) {
                    instance = new DBConf();
                }
            }
        }
        return instance;
    }

    public String getDBDriver() {
        return properties.getProperty("jdbc.driverClassName");
    }

    public String getCloudDBUrl() {
        return properties.getProperty("jdbc.url");
    }

    public String getCloudDBUser() {
        return properties.getProperty("jdbc.username");
    }

    public String getCloudDBPasswd() {
        return properties.getProperty("jdbc.password");
    }

    private void trimSpace(Properties properties) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            entry.setValue(entry.getValue().toString().trim());
        }
    }
}
