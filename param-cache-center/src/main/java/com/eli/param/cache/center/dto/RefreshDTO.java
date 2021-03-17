package com.eli.param.cache.center.dto;

import lombok.Data;

import java.util.List;

/**
 * @author eli
 */
@Data
public class RefreshDTO {

    private String appCode;


    private List<String> cacheConfigIds;


}
