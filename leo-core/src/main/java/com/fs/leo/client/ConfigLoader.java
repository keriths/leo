package com.fs.leo.client;

import com.fs.leo.exceptions.ConfigKeyPatternException;

import java.util.Map;

/**
 * Created by fanshuai on 16/7/2.
 */
public interface ConfigLoader {
    String getConfigValue(String key) throws Exception;

    boolean setConfigvalue(String key,String value) throws Exception;

    Map<String,String> getDomainConfigValues(String domain) throws Exception;
    public void addConfigChangeListener(ConfigChangeListener configChangeListener);
}
