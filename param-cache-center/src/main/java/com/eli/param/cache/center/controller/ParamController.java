package com.eli.param.cache.center.controller;

import com.alibaba.fastjson.JSON;
import com.eli.param.cache.center.service.IParamService;
import com.eli.param.cache.center.utils.result.Result;
import com.eli.param.cache.domain.CacheConfig;
import com.eli.param.cache.domain.ParamData;
import com.eli.param.cache.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author eli
 */
@Slf4j
@RestController
public class ParamController {

    @Autowired
    private IParamService paramService;


    @Autowired
    private CuratorFramework curatorFramework;


    @GetMapping("/param/force")
    public Result<List<ParamData>> getParamByForce(@RequestParam String appCode) {
        List<ParamData> paramData = paramService.queryAllData(appCode);
        return Result.wrapSuccessfulResult(paramData);
    }


    @GetMapping("/param/config")
    public Result<CacheConfig> getCacheConfig(@RequestParam String appCode) throws Exception {
        //
        CacheConfig cacheConfig = paramService.queryCacheConfig(appCode);

        // 设置
        String configNodePath = Constants.SLASH + appCode + Constants._CONFIG;
        // 不存在 创建
        if (curatorFramework.checkExists().forPath(configNodePath) == null) {
            curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(configNodePath, JSON.toJSONString(cacheConfig).getBytes());
        } else {
            // 更新
            curatorFramework.setData().forPath(configNodePath, JSON.toJSONString(cacheConfig).getBytes());
        }
        return Result.wrapSuccessfulResult(cacheConfig);
    }


}
