package com.eli.param.cache.core;

import com.eli.param.cache.domain.AppConfig;
import com.eli.param.cache.policy.ZookeeperRefreshPolicy;
import com.eli.param.cache.watcher.SessionWatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

/**
 * @author eli
 */
@Slf4j
public class ZooKeeperManager implements IZookeeperOperation {

    private final CuratorFramework curatorFramework;

    private final AppConfig appConfig;

    public ZooKeeperManager(AppConfig appConfig, CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
        this.appConfig = appConfig;
    }

    public void start() {
        //启动客户端
        if (!curatorFramework.isStarted()) {
            curatorFramework.start();
        }
    }

    public CuratorFramework getCuratorFramework() {
        return curatorFramework;
    }


    public void addConnectionStateListener(ConnectionStateListener connectionStateListener) {
        curatorFramework.getConnectionStateListenable().addListener(connectionStateListener);
    }

    public void destroy() {
        if (curatorFramework != null) {
            curatorFramework.close();
            log.info("关闭curatorFramework连接...");
        }
    }

    @Override
    public String getData(String nodePath) {
        try {
            byte[] bytes = curatorFramework.getData().forPath(nodePath);
            return new String(bytes);
        } catch (Exception e) {
            log.error("获取{}节点数据异常", nodePath, e);
            return null;
        }
    }

    @Override
    public boolean isExistPath(String nodePath) {
        try {
            Stat stat = curatorFramework.checkExists().forPath(nodePath);
            return stat != null;
        } catch (Exception e) {
            log.error("判断节点是否异常", e);
            throw new RuntimeException("判断节点是否异常");
        }
    }

    @Override
    public void createNode(String path, String data, CreateMode createMode) {
        try {
            curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(createMode)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(path, data.getBytes());
        } catch (Exception e) {
            log.error("zk节点创建失败，失败原因为:", e);
            throw new RuntimeException("zk节点创建失败");
        }
    }
}
