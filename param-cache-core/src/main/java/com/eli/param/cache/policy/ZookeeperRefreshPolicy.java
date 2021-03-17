package com.eli.param.cache.policy;


import com.alibaba.fastjson.JSON;
import com.eli.param.cache.cache.ParamCache;
import com.eli.param.cache.core.ZooKeeperManager;
import com.eli.param.cache.domain.AppConfig;
import com.eli.param.cache.domain.CacheConfig;
import com.eli.param.cache.domain.ParamData;
import com.eli.param.cache.rpc.ServerService;
import com.eli.param.cache.utils.Constants;
import com.eli.param.cache.watcher.CacheConfigWatcher;
import com.eli.param.cache.watcher.SessionWatcher;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * 使用zookeeper节点监听刷新
 *
 * @author eli
 */
@Data
@Slf4j
public class ZookeeperRefreshPolicy extends AbstractRefreshPolicy {

    private final AppConfig appConfig;

    private ZooKeeperManager zooKeeperManager;

    private CacheConfig cacheConfig;

    private NodeCache nodeCache;

    private ParamCache paramCache;

    /**
     * 等待时间
     */
    private static final int WAIT_TIME = 1000;

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY = 4;


    public ZookeeperRefreshPolicy(AppConfig appConfig, ParamCache paramCache) {
        this.appConfig = appConfig;
        this.paramCache = paramCache;
        initCuratorFramework();
    }

    private void initCuratorFramework() {
        // 指数退避重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(WAIT_TIME, MAX_RETRY);
        // 获取zookeeper集群信息
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ServerService.getZkAddress(this.appConfig.getServerBaseUrl()))
                .sessionTimeoutMs(10 * 1000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        zooKeeperManager = new ZooKeeperManager(appConfig, curatorFramework);
        SessionWatcher sessionWatcher = new SessionWatcher(this);
        zooKeeperManager.addConnectionStateListener(sessionWatcher);
    }

    @Override
    public void refresh() {
        // 开启zookeeper客户端
        zooKeeperManager.start();
        // 获取配置节点数据
        cacheConfig = getCacheConfigFromZk();
        // 拉取缓存服务数据
        List<ParamData> paramDataList = pullParamData();
        log.info("{}", paramDataList);
        // 增量构建本地缓存数据
        log.info("{} 开始刷新缓存", Thread.currentThread().getName());
        paramCache.mergeCache(paramDataList);
        log.info("{} 刷新缓存结束", Thread.currentThread().getName());
        // 配置监听器
        registerNodeListener();
        // 注册机器信息
        createMachineNode();
    }

    private CacheConfig getCacheConfigFromZk() {
        CacheConfig cacheConfig = null;
        // 获取参数配置
        cacheConfig = ServerService.getCacheConfig(appConfig.getServerBaseUrl(), appConfig.getAppCode());
        return cacheConfig;
    }

    private List<ParamData> pullParamData() {
        return ServerService.pullParamData(appConfig.getServerBaseUrl(), appConfig.getAppCode());
    }

    private void createMachineNode() {
        String machinePath = Constants.SLASH + appConfig.getAppCode() + Constants._CONFIG + Constants.SLASH + appConfig.getAppCode();
        zooKeeperManager.createNode(machinePath, JSON.toJSONString(cacheConfig), CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    private void registerNodeListener() {
        String nodePath = Constants.SLASH + appConfig.getAppCode() + Constants._CONFIG;
        try {
            if (nodeCache != null) {
                nodeCache.close();
                nodeCache = null;
            }
            nodeCache = new NodeCache(zooKeeperManager.getCuratorFramework(), nodePath);
            nodeCache.start(true);
            nodeCache.getListenable().addListener(new CacheConfigWatcher(nodeCache, appConfig, paramCache));
        } catch (Exception e) {
            log.error("节点监听失败", e);
            throw new RuntimeException("节点监听失败");
        }
    }
}
