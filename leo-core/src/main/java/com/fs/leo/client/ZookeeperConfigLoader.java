package com.fs.leo.client;

import com.fs.leo.exceptions.ConfigKeyPatternException;
import com.google.common.base.Strings;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import sun.security.krb5.Config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fanshuai on 16/7/2.
 */
public class ZookeeperConfigLoader implements ConfigLoader ,CuratorListener,ConnectionStateListener{
    private static final String careFlag="care";
    private String zkAddress;
    private CuratorFramework curatorFramework ;
    private CuratorListener curatorListener;
    private ConnectionStateListener connectionStateListener;
    private Map<String,String> careWatchNodeMap = new ConcurrentHashMap<String, String>();
    private List<ConfigChangeListener> configChangeListenerList = new ArrayList<ConfigChangeListener>();

    public void addConfigChangeListener(ConfigChangeListener configChangeListener){
        if (configChangeListenerList.contains(configChangeListener)){
            return;
        }
        configChangeListenerList.add(configChangeListener);
    }

    public List<String> getAllDomains() throws Exception {
        if (existsNode(ConfigKeyUtils.getConfigRootPath()){
            return getChildren(ConfigKeyUtils.getConfigRootPath());
        }
        return null;
    }

    public ZookeeperConfigLoader(String zkAddress) throws Exception {
        this.zkAddress = zkAddress;
        init();
    }
    private void init() throws Exception {
        curatorFramework = newCuratorClient();
    }
    private CuratorFramework newCuratorClient() throws Exception {
        CuratorFramework curatorClient = CuratorFrameworkFactory.newClient(
                zkAddress, 60*1000, 30*1000, new RetryNTimes(3, 1000));
        curatorClient.getConnectionStateListenable().addListener(this);
        curatorClient.getCuratorListenable().addListener(this);
        curatorClient.start();
        try {
            curatorClient.getZookeeperClient().blockUntilConnectedOrTimedOut();
        } catch (Exception e) {
            throw new Exception("failed to connect to zookeeper: "+zkAddress,e);
        }
        return curatorClient;
    }


    @Override
    public String getConfigValue(String key) throws Exception {
        String zkNodePath = null;
        try {
            zkNodePath = ConfigKeyUtils.configKeyToZKNodePath(key);
        } catch (ConfigKeyPatternException e) {
            return null;
        }
        careWatchNodeMap.put(zkNodePath,careFlag);
        return getValue(zkNodePath);
    }

    @Override
    public boolean setConfigvalue(String key, String value) throws Exception {
        String path = ConfigKeyUtils.configKeyToZKNodePath(key);
        if (value==null){
            setData(path,new byte[0]);
        }else {
            setData(path,value.getBytes());
        }
        return true;
    }

    @Override
    public Map<String, String> getDomainConfigValues(String domain) throws Exception {
        Map<String,String> domainConfigValueMap = new HashMap<String, String>();
        String domainPath = ConfigKeyUtils.getDomainNodePath(domain);
        if (!existsNode(domainPath)){
            return domainConfigValueMap;
        }
        List<String> childrenPathList =  getChildren(domainPath);
        for (String childrenPath:childrenPathList){
            try {
                String confitNodePath = domainPath+"/"+childrenPath;
                String configKey = ConfigKeyUtils.zkNodePathToConfigKey(confitNodePath);
                String value = getValue(confitNodePath);
                domainConfigValueMap.put(configKey,value);
            }catch (Exception e){

            }
        }
        return domainConfigValueMap;
    }

    private void processConfigChanged(String zkNodePath){
        try {
            String configKey = ConfigKeyUtils.zkNodePathToConfigKey(zkNodePath);
            if (Strings.isNullOrEmpty(configKey)){
                return;
            }
            String care = careWatchNodeMap.get(zkNodePath);
            if (careFlag.equals(care)){
                String newValue = getValue(zkNodePath);
                doConfigValueChanged(configKey,newValue);
            }
        }catch (Exception e){

        }
    }
    private void processConfigDeleted(String zkNodePath){
        String configKey = ConfigKeyUtils.zkNodePathToConfigKey(zkNodePath);
        if (Strings.isNullOrEmpty(configKey)){
            return;
        }
        String care = careWatchNodeMap.get(zkNodePath);
        if (careFlag.equals(care)){
            doConfigValueChanged(configKey,null);
        }
    }
    @Override
    public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
        if(event.getType() == CuratorEventType.WATCHED) {
            WatchedEvent watchedEvent = event.getWatchedEvent();
            if(watchedEvent.getPath() != null) {
                String path = watchedEvent.getPath();
                if (watchedEvent.getType() == Watcher.Event.EventType.NodeCreated || watchedEvent.getType() == Watcher.Event.EventType.NodeDataChanged) {
                    processConfigChanged(path);
                } else if (watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted) {
                    processConfigDeleted(path);
                }
            }
        }
    }

    public byte[] getDataWatched(String path) throws Exception {
        try {
            byte[] data = curatorFramework.getData().watched().forPath(path);
            return data;
        } catch(KeeperException.NoNodeException e) {
            curatorFramework.checkExists().watched().forPath(path);
            return null;
        }
    }


    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        if (newState == ConnectionState.RECONNECTED) {
            Set<Map.Entry<String,String>> entries = careWatchNodeMap.entrySet();
            for (Map.Entry<String,String> entry:entries){
                try {
                    String zkNodePath = entry.getKey();
                    String configKey = ConfigKeyUtils.zkNodePathToConfigKey(zkNodePath);
                    if (Strings.isNullOrEmpty(configKey)){
                        continue;
                    }
                    String newValue = getValue(zkNodePath);
                    doConfigValueChanged(configKey, newValue);
                }catch (Exception e){

                }
            }
        }
    }

    private void doConfigValueChanged(String configKey, String newValue) {
        for (ConfigChangeListener configChangeListener:configChangeListenerList){
            try {
                configChangeListener.onConfigChanged(configKey,newValue);
            }catch (Exception e){

            }

        }
    }

    public boolean existsNode(String pathNode) throws Exception{
        Stat stat = curatorFramework.checkExists().watched().forPath(pathNode);
        if(stat==null){
            return false;
        }
        return true;
    }

    public void setData(String node,byte[] value) throws Exception {
        if(existsNode(node)){
            curatorFramework.setData().forPath(node,value);
        }
        try {
            curatorFramework.create().creatingParentsIfNeeded().forPath(node,value);
        }catch (KeeperException.NodeExistsException e){
            curatorFramework.setData().forPath(node,value);
        }
    }

    private String getValue(String path) throws Exception {
        byte[] data = getDataWatched(path);
        if (data==null){
            return null;
        }
        return new String(data,"UTF-8");
    }

    public List<String> getChildren(String nodePath) throws Exception {
        return  curatorFramework.getChildren().watched().forPath(nodePath);
    }



}
