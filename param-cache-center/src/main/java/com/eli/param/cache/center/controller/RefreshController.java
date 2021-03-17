package com.eli.param.cache.center.controller;

import com.eli.param.cache.center.dto.RefreshDTO;
import com.eli.param.cache.center.service.IParamService;
import com.eli.param.cache.center.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eli
 */
@Slf4j
@RestController
public class RefreshController {

    @Autowired
    private IParamService paramService;


    @PostMapping("/refresh")
    public Result refresh(@RequestBody RefreshDTO refreshDTO) throws Exception {
        paramService.refresh(refreshDTO);
        return Result.wrapSuccessfulResult(null);
    }


}
