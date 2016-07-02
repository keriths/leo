package com.fs.leo.demo.action;

import com.fs.leo.client.ConfigManager;
import com.fs.leo.demo.service.TestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

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
            return ConfigManager.getDefaultInstance().getDomainConfigValues(domain).toString();
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
            return ConfigManager.getDefaultInstance().setConfigValues(configKey, value);
        } catch (Exception e) {
            e.printStackTrace();
            return "出异常了";
        }
    }
}
