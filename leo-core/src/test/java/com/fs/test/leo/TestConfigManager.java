package com.fs.test.leo;

import com.fs.leo.client.ConfigManager;
import org.junit.Test;

/**
 * Created by fanshuai on 16/7/2.
 */
public class TestConfigManager {

    @Test
    public void setvalueTest() throws Exception {

        while (true){
            try {
                System.out.println(ConfigManager.getDefaultInstance().getConfigValue("domain.aaa"));
                System.out.println(ConfigManager.getDefaultInstance().getConfigValue("domain.bbb"));
                System.out.println(ConfigManager.getDefaultInstance().getConfigValue("domain.ccc"));
                System.out.println(ConfigManager.getDefaultInstance().getConfigValue("domain.ddd"));
                System.out.println(ConfigManager.getDefaultInstance().getConfigValue("domain.eee"));
                Thread.sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
    @Test
    public void setvalueTest222() throws Exception {
        ConfigManager.getDefaultInstance().setConfigValues("domain.ddd","domain.ccc");

    }
}
