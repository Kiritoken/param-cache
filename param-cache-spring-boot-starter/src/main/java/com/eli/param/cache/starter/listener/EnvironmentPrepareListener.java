package com.eli.param.cache.starter.listener;

import com.eli.param.cache.core.ParamCacheClient;
import com.eli.param.cache.domain.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * 1、获取属性配置
 * 2、查询zk集群地址
 * 3、初始化zkCli
 * 4、启动超时重连后台守护进程
 *
 * @author eli
 */
@Slf4j
public class EnvironmentPrepareListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {


    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {
        log.info("开始初始化参数缓存客户端...");
        StopWatch watch = new StopWatch();
        watch.start();
        // 获取环境参数配置
        ConfigurableEnvironment environment = applicationEnvironmentPreparedEvent.getEnvironment();
        AppConfig appConfig = getAppConfig(environment);
        // 初始化
        ParamCacheClient.getInstance().init(appConfig);
        //
        watch.stop();
        log.info("初始化参数缓存客户端完毕,耗时[{}]ms...", watch.getTotalTimeMillis());
    }


    private AppConfig getAppConfig(ConfigurableEnvironment environment) {
        AppConfig appConfig = new AppConfig();
        Class<AppConfig> appConfigClass = AppConfig.class;
        Field[] declaredFields = appConfigClass.getDeclaredFields();
        for (Field field : declaredFields) {
            String property = environment.getProperty("param.cache." + convertCamelString(field.getName()));
            try {
                field.setAccessible(true);
                field.set(appConfig, property);
            } catch (IllegalAccessException e) {
                log.error("获取应用配置异常", e);
                throw new RuntimeException("获取应用配置异常,请检查配置");
            }
        }
        return appConfig;
    }

    private String convertCamelString(String src) {
        if (null == src || src.isEmpty()) {
            return src;
        }
        char[] chars = src.toCharArray();
        StringBuilder sb = new StringBuilder(src.length() * 2);
        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                sb.append("-").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
