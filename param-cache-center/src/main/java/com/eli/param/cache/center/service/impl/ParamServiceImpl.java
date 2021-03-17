package com.eli.param.cache.center.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.eli.param.cache.center.dto.RefreshDTO;
import com.eli.param.cache.center.entity.CacheConfigPO;
import com.eli.param.cache.center.mapper.CacheConfigMapper;
import com.eli.param.cache.center.service.IParamService;
import com.eli.param.cache.domain.CacheConfig;
import com.eli.param.cache.domain.ParamConfig;
import com.eli.param.cache.domain.ParamData;
import com.eli.param.cache.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
@Slf4j
@Service
public class ParamServiceImpl implements IParamService {

    @Autowired
    private CacheConfigMapper cacheConfigMapper;

    @Autowired
    private CuratorFramework curatorFramework;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ParamData> queryAllData(String appCode) {
        // 1. 查询所有配置信息
        QueryWrapper<CacheConfigPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_code", appCode);
        List<CacheConfigPO> cacheConfigs = cacheConfigMapper.selectList(queryWrapper);
        log.info("{}", cacheConfigs);
        // 2. 获得所有配置的缓存
//        long version = System.currentTimeMillis();
        List<ParamData> paramDataList = new LinkedList<>();
        for (CacheConfigPO config : cacheConfigs) {
            List<Map<String, Object>> param = cacheConfigMapper.getParam(config.getTableName());
            ParamData paramData = ParamData.builder().tableName(config.getTableName())
                    .clazzName(config.getClazzName())
                    .datasourceCode(config.getDatasourceCode())
                    .version(config.getVersion())
                    .data(param).build();
            paramDataList.add(paramData);
        }

        // 4. 更新redis数据

        // 5. 更新node节点
        // 判断是否存在节点，不存在创建


        return paramDataList;
    }


    @Override
    public CacheConfig queryCacheConfig(String appCode) {
        QueryWrapper<CacheConfigPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_code", appCode);
        List<CacheConfigPO> cacheConfigs = cacheConfigMapper.selectList(queryWrapper);
        CacheConfig cacheConfig = new CacheConfig();
        List<ParamConfig> paramConfigs = new ArrayList<>();
        for (CacheConfigPO cacheConfigPO : cacheConfigs) {
            ParamConfig paramConfig = new ParamConfig();
            paramConfig.setDatasourceCode(cacheConfigPO.getDatasourceCode());
            paramConfig.setTableName(cacheConfigPO.getTableName());
            paramConfig.setVersion(cacheConfigPO.getVersion());
            paramConfigs.add(paramConfig);
        }
        cacheConfig.setAppCode(appCode);
        cacheConfig.setParamConfigs(paramConfigs);
        return cacheConfig;
    }

    private void updateCacheConfig(long version, String appCode) {
        CacheConfigPO cacheConfigPO = new CacheConfigPO();
        cacheConfigPO.setVersion(version);
        UpdateWrapper<CacheConfigPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("app_code", appCode);
        // 乐观锁保证最新
        updateWrapper.le("version", version);
        cacheConfigMapper.update(cacheConfigPO, updateWrapper);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refresh(RefreshDTO refreshDTO) throws Exception {
        long version = System.currentTimeMillis();
        String appCode = refreshDTO.getAppCode();
        List<String> cacheConfigIds = refreshDTO.getCacheConfigIds();

        CacheConfigPO cacheConfigPO = new CacheConfigPO();
        cacheConfigPO.setVersion(version);
        UpdateWrapper<CacheConfigPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.le("version", version);
        if (CollectionUtils.isEmpty(cacheConfigIds)) {
            //全量刷新
            updateWrapper.eq("app_code", appCode);
        } else {
            updateWrapper.in("id", cacheConfigIds);
        }
        cacheConfigMapper.update(cacheConfigPO, updateWrapper);
        // 更新redis

        // 更新节点
        String configNodePath = Constants.SLASH + appCode + Constants._CONFIG;
        CacheConfig cacheConfig = queryCacheConfig(appCode);
        curatorFramework.setData().forPath(configNodePath, JSON.toJSONString(cacheConfig).getBytes());
    }
}
