package com.eli.param.cache.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ParamData {

    private String datasourceCode;

    private String tableName;

    private String clazzName;

    private Long version;

    private List<Map<String, Object>> data;



}
