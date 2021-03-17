package com.eli.param.cache.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eli.param.cache.center.entity.CacheConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
@Mapper
public interface CacheConfigMapper extends BaseMapper<CacheConfigPO> {


    @Select("select * from ${tableName}")
    List<Map<String, Object>> getParam(String tableName);


}
