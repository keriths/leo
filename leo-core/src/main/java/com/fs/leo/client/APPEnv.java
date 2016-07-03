package com.fs.leo.client;

/**
 * Created by fanshuai on 16/7/2.
 */
public class APPEnv {
    private static String appName="leo";
    private static String configZKaddress="192.168.137.128:2181";


    public static String getAppName() {
        return appName;
    }


    public static String getConfigZKaddress() {
        return configZKaddress;
    }

}
