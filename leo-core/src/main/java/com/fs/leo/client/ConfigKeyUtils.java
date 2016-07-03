package com.fs.leo.client;

import com.fs.leo.exceptions.ConfigKeyPatternException;
import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fanshuai on 16/7/2.
 */
public class ConfigKeyUtils {
    private static final String prefix = "${";
    private static final String suffix = "}";
    private static final String zkNodePathPrefix = "/config/";
    private static final String keyPattern = "^[a-zA-Z]+\\.[a-zA-Z0-9]+$";
    private static final String keyNodePathPattern = "^[a-zA-Z]+/[a-zA-Z0-9]+$";
    public static boolean checkConfigKeyPattern(String configKey){
        return checkPattern(keyPattern,configKey);
    }
    public static String zkNodePathToConfigKey(String zkNodePath){
        if (!zkNodePath.startsWith(zkNodePathPrefix)){
            return null;
        }
        String configKeySubPath = zkNodePath.substring(zkNodePathPrefix.length(), zkNodePath.length());
        if(configKeySubPath.endsWith("/")){
            configKeySubPath = configKeySubPath.substring(0,configKeySubPath.length()-1);
        }
        if (!checkPattern(keyNodePathPattern,configKeySubPath)){
            return null;
        }
        return configKeySubPath.replace("/",".");
    }
    public static String configKeyToZKNodePath(String key) throws ConfigKeyPatternException{
        if (Strings.isNullOrEmpty(key)){
            throw new ConfigKeyPatternException(" config key is null or empty ");
        }
        if (!checkPattern(keyPattern,key)){
            throw new ConfigKeyPatternException(" config key pattern error ");
        }
        int pointIndex = key.indexOf(".");
        String domain = key.substring(0, pointIndex);
        String keyName = key.substring(pointIndex+1,key.length());
        return zkNodePathPrefix+domain+"/"+keyName;
    }
    public static String getDomainNodePath(String domain) throws ConfigKeyPatternException {
        if (Strings.isNullOrEmpty(domain)){
            throw new ConfigKeyPatternException(" config key domain is null or empty ");
        }
        if (!checkPattern(keyPattern,domain+".keyName")){
            throw new ConfigKeyPatternException(" config key pattern error ");
        }
        return zkNodePathPrefix+domain;
    }


    private static boolean checkPattern(String pattern , String value){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(value);
        return m.matches();
    }

    public static boolean isDynamicConfig(String key){
        if (Strings.isNullOrEmpty(key)){
            return false;
        }
        key = key.trim();
        if (key.startsWith(prefix)&&key.endsWith(suffix)){
            return true;
        }
        return false;
    }

    public static String getConfigKeyFromDynamicKey(String dynamicKey){
        return dynamicKey.substring(dynamicKey.indexOf(prefix)+prefix.length(),dynamicKey.indexOf(suffix));
    }

    public static String getConfigRootPath(){
        return zkNodePathPrefix;
    }


}
