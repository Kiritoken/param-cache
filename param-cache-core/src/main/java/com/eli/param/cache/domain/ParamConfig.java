package com.eli.param.cache.domain;

import lombok.Data;

/**
 * @author eli
 */
@Data
public class ParamConfig {

    /**
     * 数据源代码
     */
    private String datasourceCode;

    /**
     * 表名 schema.table
     */
    private String tableName;

    /**
     * 版本号 时间戳
     */
    private Long version;


}
