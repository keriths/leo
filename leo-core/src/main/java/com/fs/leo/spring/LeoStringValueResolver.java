package com.fs.leo.spring;

import com.fs.leo.client.ConfigManager;
import com.fs.leo.exceptions.CannotConnectionException;
import org.springframework.util.StringValueResolver;

import java.util.Properties;

/**
 * Created by fanshuai on 16/7/2.
 */
public class LeoStringValueResolver implements StringValueResolver {
    private String prefix="${";
    private String suffix = "}";
    private Properties props;
    public LeoStringValueResolver(String prefix,String suffix,Properties props){
        this.prefix = prefix;
        this.suffix = suffix;
        this.props=props;
    }
    @Override
    public String resolveStringValue(String value){
        if(!checkCanReset(value)){
            return value;
        }
        try {
            String valueKey = getRatPropertiesKey(value);
            String newString = ConfigManager.getDefaultInstance().getConfigValue(valueKey);
            if(newString==null&&props!=null){
                newString = props.getProperty(valueKey);
            }
            if(newString==null){
                throw new Exception(value+" setting is null");
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
    private String getRatPropertiesKey(String value){
        return value.substring(value.indexOf(prefix)+prefix.length(),value.indexOf(suffix));
    }
    private boolean checkCanReset(String value){
        if(!value.startsWith(prefix)){
            return false;
        }
        if(!value.endsWith(suffix)){
            return false;
        }
        return true;
    }
}
