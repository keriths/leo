package com.fs.leo.client;

import com.google.common.base.Strings;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fanshuai on 16/7/2.
 */
public class ConfigManager implements ConfigChangeListener{
    private final static Object getIntenceLock = new Object();
    private final static String nullValueFlag = "@^#5(*%J!~~!$><";
    private static Map<String,ConfigManager> instanceMap = new ConcurrentHashMap<String, ConfigManager>();
    private String zkAddress;
    private Map<String,String> configCacheMap = new ConcurrentHashMap<String, String>();
    private ConfigLoader configValueLoader;
    private Properties localProperties = new Properties ();
    public void addlocalProperties(Properties properties){
        localProperties.putAll(properties);
    }
    private ConfigManager(String zkAddress) throws Exception {
        this.zkAddress = zkAddress;
        configValueLoader = new ZookeeperConfigLoader(zkAddress);
        configValueLoader.addConfigChangeListener(this);
    }
    public  static ConfigManager getInstance(String address) throws Exception {
        if (Strings.isNullOrEmpty(address)){
            return null;
        }
        address = address.trim();
        ConfigManager configManager = instanceMap.get(address);
        if (configManager!=null){
            return configManager;
        }
        synchronized (getIntenceLock){
            configManager = instanceMap.get(address);
            if (configManager!=null){
                return configManager;
            }
            configManager = new ConfigManager(address);
            instanceMap.put(address, configManager);
            return configManager;
        }
    }

    public static ConfigManager getDefaultInstance() throws Exception {
        return getInstance(APPEnv.getConfigZKaddress());
    }

    private String getConfigValueFromLoader(String key) throws Exception {
        String configValue = configValueLoader.getConfigValue(key);
        if (configValue == null){
            configValue = nullValueFlag;
        }
        configCacheMap.put(key,configValue);
        return configValue;
    }

    @Override
    public void onConfigChanged(String key, String value) {
        if (value==null){
            value = nullValueFlag;
        }
        configCacheMap.put(key,value);
    }



    public String getConfigValue(String key) throws Exception {
        if (Strings.isNullOrEmpty(key)){
            return null;
        }
        key = key.trim();
        Object localValue = localProperties.get(key);
        if (localValue!=null){
            if (ConfigKeyUtils.isDynamicConfig(localValue.toString())){
                key = ConfigKeyUtils.getConfigKeyFromDynamicKey(localValue.toString());
            }else {
                return localValue.toString();
            }
        }
        if (!ConfigKeyUtils.checkConfigKeyPattern(key)){
            return null;
        }
        String value = configCacheMap.get(key);
        if (value==null){
            value = getConfigValueFromLoader(key);
        }
        if (nullValueFlag.equals(value)){
            return null;
        }
        return value;
    }

    public boolean setConfigValues(String key,String value) throws Exception {
        return configValueLoader.setConfigvalue(key,value);
    }

    public Map<String,String> getDomainConfigValues(String domain) throws Exception {
        return configValueLoader.getDomainConfigValues(domain);
    }

    public  List<String> getAllDomains() throws Exception {
        return configValueLoader.getAllDomains();
    }

}
