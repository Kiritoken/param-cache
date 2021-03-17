package com.eli.param.cache.starter;

import com.eli.param.cache.domain.AppConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author eli
 */
@Data
@ConfigurationProperties(prefix = "param.cache")
public class AppProperties extends AppConfig {

}
