package com.fs.test.leo;

import com.fs.leo.client.ConfigKeyUtils;
import com.fs.leo.exceptions.ConfigKeyPatternException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fanshuai on 16/7/2.
 */
public class TestConfigKeyUtils {

//    @Test
//    public void checkConfigKeyPatternTest(){
//        Assert.assertTrue(ConfigKeyUtils.checkConfigKeyPattern("domain.keyName"));
//        Assert.assertTrue(ConfigKeyUtils.checkConfigKeyPattern("d.keyName"));
//        Assert.assertTrue(ConfigKeyUtils.checkConfigKeyPattern("domain.k"));
//        Assert.assertTrue(ConfigKeyUtils.checkConfigKeyPattern("a.k"));
//        Assert.assertFalse(ConfigKeyUtils.checkConfigKeyPattern(".keyname"));
//        Assert.assertFalse(ConfigKeyUtils.checkConfigKeyPattern("a..keyname"));
//        Assert.assertFalse(ConfigKeyUtils.checkConfigKeyPattern("a.k_eyname"));
//        Assert.assertFalse(ConfigKeyUtils.checkConfigKeyPattern("4.keyname"));
//        Assert.assertFalse(ConfigKeyUtils.checkConfigKeyPattern("a1.keyname"));
//    }

    @Test
    public void configKeyToZKNodePathTest() throws ConfigKeyPatternException {
        configKeyToZKNodePathTrueTest("asf", "sadff");
        configKeyToZKNodePathTrueTest("asf", "222");
        configKeyToZKNodePathTrueTest("asf", "2s");
        configKeyToZKNodePathFalseTest("2","a");
        configKeyToZKNodePathFalseTest("2","a_");
        configKeyToZKNodePathFalseTest("2","_");
        configKeyToZKNodePathFalseTest("2","/");
        System.out.println(ConfigKeyUtils.zkNodePathToConfigKey("/config/domain/keyName"));
        System.out.println(ConfigKeyUtils.zkNodePathToConfigKey("/config/domain/keyName/"));
        System.out.println(ConfigKeyUtils.zkNodePathToConfigKey("/config/domain/keyName/a"));
        System.out.println(ConfigKeyUtils.zkNodePathToConfigKey("/config/d1omain/keyName/"));
        System.out.println(ConfigKeyUtils.zkNodePathToConfigKey("/config/domain/key2Name/"));
        System.out.println(ConfigKeyUtils.zkNodePathToConfigKey("/config/domain/keyN_ame/"));
        System.out.println(ConfigKeyUtils.zkNodePathToConfigKey("/config/2/3/"));
    }
    private void configKeyToZKNodePathTrueTest(String domain,String keyName) throws ConfigKeyPatternException {
        Assert.assertTrue(("/config/"+domain+"/"+keyName).equals(ConfigKeyUtils.configKeyToZKNodePath(domain + "." + keyName)));
    }
    private void configKeyToZKNodePathFalseTest(String domain,String keyName)   {
        try {
            ("/config/"+domain+"/"+keyName).equals(ConfigKeyUtils.configKeyToZKNodePath(domain + "." + keyName));
            Assert.fail();
        }catch (ConfigKeyPatternException e){

        }

    }

}
