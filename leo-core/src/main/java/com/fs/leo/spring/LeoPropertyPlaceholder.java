package com.fs.leo.spring;

import com.fs.leo.client.ConfigManager;
import com.fs.leo.exceptions.ResetBeanPropertyValueException;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.util.StringValueResolver;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.*;

/**
 * Created by fanshuai on 16/7/2.
 */
public class LeoPropertyPlaceholder implements BeanFactoryPostProcessor {
    private Resource[] locations;
    private final Logger log = Logger.getLogger(LeoPropertyPlaceholder.class);
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String bname=null;
        String pname=null;
        try {
            initLocalPropertieFiles();//兼容文件配置方式，文件中有的全记录下来
            StringValueResolver stringValueResolver=new LeoStringValueResolver();
            String [] beanNames = beanFactory.getBeanDefinitionNames();
            for (String beanName:beanNames){
                bname = beanName;
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
                for(PropertyValue propertyValue : propertyValues.getPropertyValues()){
                    log.info("beanName:"+beanName + " propertyName:" + propertyValue.getName() + " valueType:" + propertyValue.getValue().getClass().getName());
                    pname = propertyValue.getName();
                    propertyValues.add(propertyValue.getName(),reSetValue(propertyValue.getValue(),stringValueResolver));
                }
            }
            beanFactory.resolveAliases(stringValueResolver);
            beanFactory.addEmbeddedValueResolver(stringValueResolver);
        }catch (Exception e){
            throw new ResetBeanPropertyValueException(" reset beanname("+bname+") property("+pname+") value exception:"+e.getMessage(),e);
        }
    }

    private void initLocalPropertieFiles() throws Exception {
        if(locations==null){
            return ;
        }
        for (Resource resource : locations ){
            if(!resource.exists()){
                throw new Exception();
            }
            try {
                Properties properties = new Properties();
                properties.load(resource.getInputStream());
                ConfigManager.getDefaultInstance().addlocalProperties(properties);
            } catch (IOException e) {
                throw new Exception();
            }
        }
    }

    public Object reSetValue(Object value,StringValueResolver stringValueResolver) throws Exception{
        if(value instanceof String){
            return mergeStringValue((String) value, stringValueResolver);
        }
        if(value instanceof TypedStringValue){
            return mergeTypedStringValue((TypedStringValue)value,stringValueResolver);
        }
        if(value instanceof Set){
            return mergeSetValue((Set)value,stringValueResolver);
        }
        if(value instanceof List){
            return mergeListValue((List)value,stringValueResolver);
        }
        if(value instanceof Map){
            return mergeMapValue((Map)value,stringValueResolver);
        }
        if (value instanceof Object[]){
            return mergeArrayValue((Object[])value,stringValueResolver);
        }
        return value;
    }

    private Object mergeArrayValue(Object[] arrayVal,StringValueResolver stringValueResolver)throws Exception {
        for (int i = 0;i<arrayVal.length;i++){
            arrayVal[i]=reSetValue(arrayVal[i],stringValueResolver);
        }
        return arrayVal;
    }

    private Object mergeMapValue(Map mapVal,StringValueResolver stringValueResolver) throws Exception{
        Map newMapVal = new LinkedHashMap();

        Set<Map.Entry> entries = mapVal.entrySet();
        for (Map.Entry<Object,Object> entry:entries){
            newMapVal.put(entry.getKey(),reSetValue(entry.getValue(),stringValueResolver));
        }

        mapVal.clear();
        mapVal.putAll(newMapVal);
        return mapVal;
    }

    private Object mergeListValue(List listVal,StringValueResolver stringValueResolver) throws Exception{
        for(int i =0;i<listVal.size();i++){
            Object value = listVal.get(i);
            listVal.set(i,reSetValue(value,stringValueResolver));
        }
        return listVal;
    }

    private Object mergeSetValue(Set setVal,StringValueResolver stringValueResolver) throws Exception{
        Set newSetVal = new LinkedHashSet();
        for(Object value:setVal){
            newSetVal.add(reSetValue(value,stringValueResolver));
        }
        setVal.clear();
        setVal.addAll(newSetVal);
        return setVal;
    }

    private Object mergeTypedStringValue(TypedStringValue value,StringValueResolver stringValueResolver) throws Exception {
        value.setValue(mergeStringValue(value.getValue(),stringValueResolver));
        return value;
    }

    private String mergeStringValue(String value,StringValueResolver stringValueResolver) throws Exception{
        return stringValueResolver.resolveStringValue(value);
    }

    public void setLocations(Resource[] locations) {
        this.locations = locations;
    }
}
