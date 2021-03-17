package com.eli.param.cache.policy;

import com.eli.param.cache.domain.AppConfig;

/**
 * 调用缓存服务接口轮询刷新
 *
 * @author eli
 */
public class RpcRefreshPolicy extends AbstractRefreshPolicy {


    public RpcRefreshPolicy(AppConfig appConfig) {
    }

    @Override
    public void refresh() {

    }
}
