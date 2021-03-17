package com.eli.param.cache.starter.autoconfig;

import com.eli.param.cache.starter.AppProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 参数缓存自动装配
 *
 * @author eli
 */
@Configuration
@ConditionalOnProperty(prefix = "param.cache", value = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(AppProperties.class)
public class ParamCacheAutoConfiguration {
}
