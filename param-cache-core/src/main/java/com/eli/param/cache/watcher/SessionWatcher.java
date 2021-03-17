package com.eli.param.cache.watcher;

import com.eli.param.cache.core.ZooKeeperManager;
import com.eli.param.cache.policy.ZookeeperRefreshPolicy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

import java.util.concurrent.TimeUnit;

/**
 * 会话LOST后临时节点被删除,需要重新注册临时节点
 *
 * @author eli
 */
@Slf4j
public class SessionWatcher implements ConnectionStateListener {

    private static final int MAX_WAIT_TIME = 2;

    private final ZooKeeperManager zooKeeperManager;

    private final ZookeeperRefreshPolicy zookeeperRefreshPolicy;

    public SessionWatcher(ZookeeperRefreshPolicy zookeeperRefreshPolicy) {
        this.zookeeperRefreshPolicy = zookeeperRefreshPolicy;
        this.zooKeeperManager = zookeeperRefreshPolicy.getZooKeeperManager();
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        int retry = 1;
        if (newState == ConnectionState.LOST) {
            while (true) {
                try {
                    log.info("zookeeper会话连接丢失,开始第{}次尝试重新连接...", retry);
                    if (zooKeeperManager.getCuratorFramework().blockUntilConnected(MAX_WAIT_TIME, TimeUnit.SECONDS)) {
                        log.info("第{}次成功连接zookeeper,开始重新注册节点...", retry);
                        zookeeperRefreshPolicy.refresh();
                        break;
                    }
                    retry++;
                } catch (Exception e) {
                    log.error("重新连接zookeeper异常...", e);
                    break;
                }
            }
        }
    }
}
