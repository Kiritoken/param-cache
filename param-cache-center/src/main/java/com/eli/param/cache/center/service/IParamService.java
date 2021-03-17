package com.eli.param.cache.center.service;

import com.eli.param.cache.center.dto.RefreshDTO;
import com.eli.param.cache.domain.CacheConfig;
import com.eli.param.cache.domain.ParamData;

import java.util.List;

/**
 * @author eli
 */
public interface IParamService {

    List<ParamData> queryAllData(String appCode);

    CacheConfig queryCacheConfig(String appCode);


    void refresh(RefreshDTO refreshDTO) throws Exception;
}
