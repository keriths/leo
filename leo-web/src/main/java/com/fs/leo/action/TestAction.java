package com.fs.leo.action;

import com.fs.leo.client.ConfigManager;
import com.fs.leo.demo.service.TestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by fanshuai on 16/7/2.
 */
@Controller
public class TestAction {

    @Resource(name = "testService")
    TestService testService;
    @RequestMapping(value = "/queryAllDomain")
    @ResponseBody
    public String queryAllDomain(String domain){
        try {
            Map domainConfigs = ConfigManager.getDefaultInstance().getDomainConfigValues(domain);
            return domainConfigs.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "出异常了";
        }
    }

    @RequestMapping(value = "/queryByKey")
    @ResponseBody
    public String queryByKey(String configKey){
        try {
            return ConfigManager.getDefaultInstance().getConfigValue(configKey);
        } catch (Exception e) {
            e.printStackTrace();
            return "出异常了";
        }
    }

    @RequestMapping(value = "/addConfigValue")
    @ResponseBody
    public String addConfigValue(String configKey,String value){
        try {
            if(ConfigManager.getDefaultInstance().setConfigValues(configKey, value)){
                return "success";
            }
            return "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "出异常了";
        }
    }
}
