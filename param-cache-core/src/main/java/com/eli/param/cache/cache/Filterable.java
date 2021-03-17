package com.eli.param.cache.cache;

/**
 * @author eli
 */
public interface Filterable {


    /**
     * 返回实体对应的搜索key
     * 建议使用主键或唯一索引
     *
     * @return key
     */
    String filterKey();
}
