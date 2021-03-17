package com.eli.param.cache.watcher;

import com.alibaba.fastjson.JSON;
import com.eli.param.cache.cache.ParamCache;
import com.eli.param.cache.domain.AppConfig;
import com.eli.param.cache.domain.CacheConfig;
import com.eli.param.cache.domain.ParamData;
import com.eli.param.cache.rpc.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;

import java.util.List;

/**
 * @author eli
 */
@Slf4j
public class CacheConfigWatcher implements NodeCacheListener {

    private final NodeCache nodeCache;


    private final AppConfig appConfig;

    private final ParamCache paramCache;

    public CacheConfigWatcher(NodeCache nodeCache, AppConfig appConfig, ParamCache paramCache) {
        this.nodeCache = nodeCache;
        this.appConfig = appConfig;
        this.paramCache = paramCache;
    }


    @Override
    public void nodeChanged() throws Exception {
        String s = new String(nodeCache.getCurrentData().getData());
        CacheConfig cacheConfig = JSON.parseObject(s, CacheConfig.class);
        log.info("{}缓存节点监听到变化,{}", Thread.currentThread().getName(), cacheConfig);
        // 获取最新配置
        List<ParamData> paramDataList = ServerService.pullParamData(appConfig.getServerBaseUrl(), appConfig.getAppCode());
        // merge
        log.info("开始merge缓存...");
        long begin = System.currentTimeMillis();
        paramCache.mergeCache(paramDataList);
        log.info("缓存merge结束,耗时[{}]ms...", System.currentTimeMillis() - begin);

    }
}
