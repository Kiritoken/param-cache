package com.eli.param.cache.center.config;

import com.eli.param.cache.core.ZooKeeperManager;
import com.eli.param.cache.rpc.ServerService;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author eli
 */
@Configuration
public class ZookeeperConfig {

    @Value("${zk.connectionString}")
    private String connectionString;

    @Bean
    public CuratorFramework curatorFramework() {
        // 指数退避重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 6);
        // 获取zookeeper集群信息
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .sessionTimeoutMs(10 * 1000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework.start();
        return curatorFramework;
    }

}
