package com.eli.param.cache.core;

import com.eli.param.cache.cache.ParamCache;
import com.eli.param.cache.domain.AppConfig;
import com.eli.param.cache.policy.RpcRefreshPolicy;
import com.eli.param.cache.policy.ZookeeperRefreshPolicy;
import lombok.extern.slf4j.Slf4j;


/**
 * 参数缓存客户端
 *
 * @author eli
 */
@Slf4j
public class ParamCacheClient {

    private AppConfig appConfig;

    private ParamCache paramCache;


    private ParamCacheClient() {
    }


    /**
     * 获取单例对象
     *
     * @return 参数缓存客户端
     */
    public static ParamCacheClient getInstance() {
        return ClientHolder.INSTANCE;
    }


    /**
     * 内部类单例
     * 延迟加载
     */
    private static class ClientHolder {
        private static final ParamCacheClient INSTANCE = new ParamCacheClient();
    }


    public void init(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.paramCache = new ParamCache(appConfig);
        ZookeeperRefreshPolicy zookeeperRefreshPolicy = new ZookeeperRefreshPolicy(appConfig, paramCache);
        RpcRefreshPolicy rpcRefreshPolicy = new RpcRefreshPolicy(appConfig);
        try {
            zookeeperRefreshPolicy.refresh();
        } catch (Exception e) {
            log.error("zookeeper客户端启动失败,降级使用轮询模式...", e);
            rpcRefreshPolicy.refresh();
        }
    }

    public ParamCache getParamCache() {
        return paramCache;
    }
}
