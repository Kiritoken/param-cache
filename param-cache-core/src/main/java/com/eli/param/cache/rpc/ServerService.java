package com.eli.param.cache.rpc;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.eli.param.cache.domain.CacheConfig;
import com.eli.param.cache.domain.ParamData;
import com.eli.param.cache.utils.HttpClientUtil;
import com.eli.param.cache.utils.result.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author eli
 */
@Slf4j
public class ServerService {

    /**
     * 获取zookeeper集群地址
     *
     * @param serverBaseUrl 服务url
     * @return 集群地址
     */
    public static String getZkAddress(String serverBaseUrl) {
        log.info("开始获取zookeeper集群地址...");
        long start = System.currentTimeMillis();
        String s = HttpClientUtil.doGet(serverBaseUrl + "/connectionString");
        String connectionString = parseResult(s, String.class);
        log.info("获取zookeeper集群地址完毕,zookeeper:{},耗时[{}]ms", connectionString, System.currentTimeMillis() - start);
        return connectionString;
    }


    public static List<ParamData> pullParamData(String serverBaseUrl, String appCode) {
        log.info("开始获取全量参数信息...");
        long start = System.currentTimeMillis();
        String s = HttpClientUtil.doGet(serverBaseUrl + "/param/force?appCode=" + appCode);
        JSONObject jsonObject = JSON.parseObject(s);
        JSONArray result = jsonObject.getJSONArray("result");
        return result.toJavaList(ParamData.class);
    }


    public static CacheConfig getCacheConfig(String serverBaseUrl, String appCode) {
        log.info("开始获取最新的CacheConfig...");
        long start = System.currentTimeMillis();
        String s = HttpClientUtil.doGet(serverBaseUrl + "/param/config?appCode=" + appCode);
        JSONObject jsonObject = JSON.parseObject(s);
        CacheConfig cacheConfig = jsonObject.getObject("result", CacheConfig.class);
        log.info("最新的CacheConfig获取完毕,cacheConfig:{},耗时[{}]ms", cacheConfig, System.currentTimeMillis() - start);
        return cacheConfig;
    }


    private static <T> T parseResult(String res, Class<T> clazz) {
        Result<T> result = JSON.parseObject(res, new TypeReference<Result<T>>() {
        });
        return (T) result.getResult();
    }


}
