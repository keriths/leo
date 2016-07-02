package com.fs.leo.spring;

import com.fs.leo.client.ConfigKeyUtils;
import com.fs.leo.client.ConfigManager;
import com.fs.leo.exceptions.CannotConnectionException;
import org.apache.log4j.Logger;
import org.springframework.util.StringValueResolver;

import java.util.Properties;

/**
 * Created by fanshuai on 16/7/2.
 */
public class LeoStringValueResolver implements StringValueResolver {
    private final Logger log = Logger.getLogger(LeoStringValueResolver.class);
    @Override
    public String resolveStringValue(String value){
        if(!ConfigKeyUtils.isDynamicConfig(value)){
            return value;
        }
        try {
            String valueKey = ConfigKeyUtils.getConfigKeyFromDynamicKey(value);
            String newString = ConfigManager.getDefaultInstance().getConfigValue(valueKey);
            if(newString==null){
                log.warn("**************** setting name ["+value+"] not found really value set to null  ****************");
            }
            value = newString;
        } catch (CannotConnectionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return value;
    }
}
