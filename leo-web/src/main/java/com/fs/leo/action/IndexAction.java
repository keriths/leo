package com.fs.leo.action;

import com.alibaba.fastjson.JSONObject;
import com.fs.leo.client.ConfigManager;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Thinkpad on 2016/7/3.
 */
@Controller
public class IndexAction {
    @RequestMapping("/")
    public String index(){

        return "redirect:/index.html";
    }

    @RequestMapping("/")
    @ResponseBody
    public String getAllDomains(){
        JSONObject json = new JSONObject();
        try {
            List allDomains = ConfigManager.getDefaultInstance().getAllDomains();
            json.put("domainList",allDomains);
        } catch (Exception e) {
            json.put("code",500);
            json.put("error",e.getMessage());
        }
        return json.toJSONString();
    }

}
