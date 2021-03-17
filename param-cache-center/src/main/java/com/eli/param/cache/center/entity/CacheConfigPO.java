package com.eli.param.cache.center.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author eli
 */
@Data
@TableName(value = "cache_config", schema = "cache")
public class CacheConfigPO {

    private long id;

    private String appCode;

    private String datasourceCode;

    private String tableName;

    private String clazzName;

    private java.sql.Timestamp gmtCreate;

    private java.sql.Timestamp gmtModified;

    private long version;

}
