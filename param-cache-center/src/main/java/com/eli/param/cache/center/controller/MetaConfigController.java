package com.eli.param.cache.center.controller;

import com.eli.param.cache.center.utils.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eli
 */
@RestController
public class MetaConfigController {

    @Value("${zk.connectionString}")
    private String connectionString;


    /**
     * 查询zk集群连接地址
     *
     * @return connectionString
     */
    @GetMapping("connectionString")
    public Result<String> queryConnectionString() {
        return Result.wrapSuccessfulResult(connectionString);
    }


}
