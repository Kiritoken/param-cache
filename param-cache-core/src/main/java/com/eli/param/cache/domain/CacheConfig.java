package com.eli.param.cache.domain;

import lombok.Data;

import java.util.List;

/**
 * 缓存配置
 *
 * @author eli
 */
@Data
public class CacheConfig {

    private String appCode;

    private List<ParamConfig> paramConfigs;

}
