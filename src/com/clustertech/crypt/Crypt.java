package com.clustertech.crypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Formatter;

import org.apache.log4j.Logger;

import com.clustertech.util.DBUtils;
import com.clustertech.util.DesEncrypter;
import com.sun.jna.platform.win32.Crypt32Util;

public class Crypt {

    private static final Logger logger = Logger.getLogger(Crypt.class);
    
    public static void main(String[] args) {

        if (args.length < 1) {
            logger.error("args: the absolutePath of user file.");
            return;
        }
        
        if ("DB".equals(args[0])) {
            String file = args[1];
            saveRdpPassword(file);
        } else {
            String password = null;
            String output = null;
            password = args[1];
            if ("D".equals(args[0])) {//解码
                output = DecodeRdpPassword(password);
            } else if ("E".equals(args[0])) {//编码
                output = cryptRdpPassword(password);
            } else if ("DD".equals(args[0])) {
                output = DesEncrypter.getInstance().decrypt(password);
            } else if ("EE".equals(args[0])) {
                output = DesEncrypter.getInstance().encrypt(password);
            }
            logger.debug("********************password is " + output);
        }
    }

    private static void saveRdpPassword(String f) {
        logger.debug("user info file: " + f);
        File file = new File(f);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            logger.error(e);
        }
        String str = null;
        try {
            while((str = bufferedReader.readLine()) != null) {
                String[] info = str.split(" ");
                String userName = info[0];
                String password = info[1];
                String rdpPassword = cryptRdpPassword(password);
                insert2DB(userName, rdpPassword);
            }
        } catch (IOException e) {
            logger.error(e);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
    
    private static void insert2DB(String name, String password) {
            DBUtils.getInstance().insertDB(name, password);
    }

    private static String cryptRdpPassword(String password) {
        try {
            return ToHexString(Crypt32Util.cryptProtectData(password.getBytes(("UTF-16LE"))));
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
            return "ERROR";
        }
    }

    private static String ToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02X", b);
        }
        formatter.close();
        return sb.toString();
    }

    private static String DecodeRdpPassword(String password) {
        try {
            return new String(Crypt32Util.cryptUnprotectData(toBytes(password)), "UTF-16LE");
        } catch (Exception e1) {
            logger.error(e1);
            return "ERROR";
        }
    }

    private static byte[] toBytes(String str) {// 去掉0x以后,转整数再转型成字节
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }
}