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
    @RequestMapping(value = "/")
    @ResponseBody
    public String index(String configKey){
        try {
            System.out.println("******aaaaaa:"+ConfigManager.getDefaultInstance().getConfigValue("aaa"));
            System.out.println("******name:"+testService.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return "configKey"+configKey+" value is :"+ConfigManager.getDefaultInstance().getConfigValue(configKey);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
