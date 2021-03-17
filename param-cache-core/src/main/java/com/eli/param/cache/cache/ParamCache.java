package com.eli.param.cache.cache;

import com.alibaba.fastjson.JSON;
import com.eli.param.cache.domain.AppConfig;
import com.eli.param.cache.domain.ParamData;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author eli
 */
@Slf4j
@SuppressWarnings("unchecked")
public class ParamCache {

    private volatile ConcurrentHashMap<String, SegmentCache> paramCache;

    private final AppConfig appConfig;

    private final ReentrantLock reentrantLock = new ReentrantLock(true);

    public ParamCache(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.paramCache = new ConcurrentHashMap<>();
    }

    public <T> List<T> getParamList(String key, Class<T> clazz) {
        SegmentCache segmentCache = paramCache.get(clazz.getSimpleName());
        if (segmentCache == null) {
            return null;
        }
        return (List<T>) segmentCache.get(key);
    }

    public <T> List<T> getParamList(Class<T> clazz) {
        SegmentCache segmentCache = paramCache.get(clazz.getSimpleName());
        if (segmentCache == null) {
            return null;
        }
        return (List<T>) segmentCache.getAll();
    }


    public <T> T getParam(String key, Class<T> clazz) {
        List<T> paramList = getParamList(key, clazz);
        if (null == paramList || paramList.size() == 0) {
            return null;
        } else {
            return paramList.get(0);
        }
    }


    /**
     * 合并缓存
     *
     * @param paramDataList 缓存参数
     */
    public void mergeCache(List<ParamData> paramDataList) {
        reentrantLock.lock();
        try {
            ConcurrentHashMap<String, SegmentCache> newCache = new ConcurrentHashMap<>(paramCache);
            for (ParamData paramData : paramDataList) {
                SegmentCache segmentCache = paramCache.get(paramData.getClazzName());
                if (segmentCache != null) {
                    // 多线程下可能会造成不安全
                    if (segmentCache.getVersion() < paramData.getVersion()) {
                        log.info("更新{}", paramData.getTableName());
                        // 更新缓存
                        SegmentCache newSegmentCache = buildCache(paramData);
                        newCache.put(paramData.getClazzName(), newSegmentCache);
                    }
                } else {
                    log.info("新增{}", paramData.getTableName());
                    // 新增
                    SegmentCache newSegmentCache = buildCache(paramData);
                    newCache.put(paramData.getClazzName(), newSegmentCache);
                }
            }
            // CopyOnWrite
            paramCache = newCache;
        } catch (Exception e) {
            log.error("缓存merge异常", e);
        } finally {
            reentrantLock.unlock();
        }
    }


    public SegmentCache buildCache(ParamData paramData) throws ClassNotFoundException {
        // 获取class实例
        String clazzName = paramData.getClazzName();
        Class<?> clazz = Class.forName(appConfig.getClassLocation() + "." + clazzName);
        List<?> objects = JSON.parseArray(JSON.toJSONString(paramData.getData()), clazz);
        ConcurrentHashMap<String, List<Object>> cache = new ConcurrentHashMap<>(paramData.getData().size());
        for (Object o : objects) {
            Filterable filterable = (Filterable) o;
            List<Object> value = cache.get(filterable.filterKey());
            if (value == null) {
                value = new LinkedList<>();
            }
            value.add(o);
            cache.put(filterable.filterKey(), value);
        }
        return new SegmentCache(cache, paramData.getVersion());
    }


}
