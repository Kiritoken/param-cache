package com.eli.param.cache.domain;

import lombok.Data;

/**
 * @author eli
 */
@Data
public class AppConfig {

    /**
     * 应用代码
     */
    private String appCode;

    /**
     * 缓存服务url
     */
    private String serverBaseUrl;


    /**
     * 轮询Cron表达式
     */
    private String pollingCron;


    /**
     * 参数entity所在包
     */
    private String classLocation;

}
